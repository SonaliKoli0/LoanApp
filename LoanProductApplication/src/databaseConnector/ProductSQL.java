package databaseConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import databaseHelper.DatabaseHelper;
import product.AppStarter;
import product.Product;
import utils.Constants;
import utils.DbUtils;
import utils.Util;

public class ProductSQL {
	protected static final String SAVE_PRODUCT = "INSERT INTO Product (productType, startDate, endDate) VALUES (?, ?, ?)";
	protected static final String GET_CURRENT_PRODUCT_ID = "SELECT product_id FROM Product WHERE rownum = 1 ORDER BY product_id DESC";
	protected static final String GET_PRODUCT_BY_ID = "SELECT * FROM PRODUCT WHERE PRODUCT_ID=?";
	protected static final String DELETE_PRODUCT_BY_ID = "DELETE  FROM PRODUCT WHERE PRODUCT_ID=?";
    
	/**
	 *  Method to insert the product in to the database
	 * @param product
	 * @throws Exception
	 */
	public static void insert(Product product) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
		long pId = -1;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_PRODUCT, Statement.RETURN_GENERATED_KEYS);
			if (product.getStartDate() != null) {
				stmt.setString(j++, product.getProductType());
				stmt.setDate(j++, Util.toSQLDate(product.getStartDate()));
				stmt.setDate(j++, Util.toSQLDate(product.getEndDate()));

			} else {
				stmt.setNull(j++, Types.DATE);
			}

			// Retrieve the generated keys
			// Execute the update operation
			int affectedRows = stmt.executeUpdate();
			System.out.println("Created successfully");

			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}

			// Execute a separate query to retrieve the last inserted ID
			try (ResultSet generatedKeys = stmt
					.executeQuery(GET_CURRENT_PRODUCT_ID)) {
				if (generatedKeys.next()) {
					pId = generatedKeys.getLong("product_id");

					product.setProductId((int) pId);
				} else {
					throw new SQLException("Inserting product failed, no ID obtained.");
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
	}
	
	/**
	 *  Method to get the product details from the database based on the productId
	 * @param productId
	 * @return
	 */
	public static Product readProduct(int productId) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		Product p = null;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(GET_PRODUCT_BY_ID);
			stmt.setLong(1, productId);

			rs = stmt.executeQuery();

			if (rs.next()) {
				int productID = rs.getInt(Constants.PRODUCT_ID);
				Date startDate = rs.getDate(Constants.STARTDATE);
				String productType = rs.getString(Constants.PRODUCTTYPE);
				Date endDate = rs.getDate(Constants.ENDDATE);
				p = new Product(productID, productType, startDate, endDate, null);

			} else {
				System.out.println("There is no product with id " + productId);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			DbUtils.close(stmt, con);
		}
		return p;
	}

	/**
	 *  Method to delete the product details from the database based on the productId
	 * @param productId
	 */
	public static void deleteProduct(int productId) {
		PreparedStatement stmt = null;

		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(DELETE_PRODUCT_BY_ID);
			stmt.setLong(1, productId);

			stmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			DbUtils.close(stmt, con);
		}

	}

	/**
	 *  Method to delete the product details from the database based on the productId
	 * @param productId
	 */
	public static void updateProduct(int productId) {
		int index = 1;
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			String sql = "UPDATE product SET";
			if (AppStarter.inputs.containsKey(Constants.ENDDATE)) {
				sql += " endDate =?";
			}
			sql += " WHERE PRODUCT_ID = ?";

			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(sql);

			 if (AppStarter.inputs.containsKey(Constants.ENDDATE)) {
				stmt.setDate(index++, Util.toSQLDate(Util.parseDate(AppStarter.inputs.get(Constants.ENDDATE))));
			}
			stmt.setInt(index++, productId);
			stmt.executeUpdate();
			System.out.println("product updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(stmt, con);
		}
	}

}