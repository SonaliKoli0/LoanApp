package databaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import databaseHelper.DatabaseHelper;
import product.AppStarter;
import product.LoanProduct;
import product.Product;
import product.Schedule;
import utils.Constants;
import utils.DbUtils;

public class LoanProductSQL extends ProductSQL {

	protected static final String SAVE_LOAN_PRODUCT = "INSERT INTO  LOANPRODUCT (productId,loanvalue,interestrate,payment_type) VALUES (?,?,?,?)";
	protected static final String READ_LOAN_PRODUCT = "SELECT * FROM LOANPRODUCT INNER JOIN PRODUCT ON LOANPRODUCT.PRODUCTID=PRODUCT.PRODUCT_ID WHERE LOANPRODUCT.PRODUCTID=? ";
	protected static final String DELETE_LOAN_PRODUCT = "DELETE FROM LOANPRODUCT WHERE PRODUCTID=?";
	protected static final String GET_LOAN_PRODUCT = "SELECT * FROM LOANPRODUCT WHERE PRODUCTID=?";
	protected static final String GET_LOAN_ID = "SELECT loanId FROM LoanProduct WHERE rownum = 1 ORDER BY loanId DESC";
	protected static final String LOANID = "LOANID";
	protected static final String LOANVALUE = "LOANVALUE";

	protected static final String INTERESTRATE = "INTERESTRATE";

	protected static final String PAYMENT_TYPE = "PAYMENT_TYPE";

	/**
	 * Method for inserting Loan product details in to the database
	 * 
	 * @param product
	 * @throws Exception
	 */
	public static void insertLoanProduct(Product product) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
		long id = -1;
		ProductSQL.insert(product);
		LoanProduct loanProduct = (LoanProduct) product;
		Connection con = null;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_LOAN_PRODUCT, Statement.RETURN_GENERATED_KEYS);
			if (product.getStartDate() != null) {
				stmt.setInt(j++, product.getProductId());
				stmt.setInt(j++, (int) loanProduct.getTotalValue());
				stmt.setInt(j++, (int) loanProduct.getRate());
				stmt.setString(j++, loanProduct.getPaymentOption());

			} else {
				stmt.setNull(j++, Types.DATE);
			}

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}
			try (ResultSet generatedKeys = stmt
					.executeQuery(GET_LOAN_ID)) {
				if (generatedKeys.next()) {
					id = generatedKeys.getLong(LOANID);

					loanProduct.setLoanId((int) id);

				} else {
					throw new SQLException("Inserting product failed, no ID obtained.");
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
		ScheduleSQL.insertDisbursementSchedule(product);
	}

	/**
	 * Method for updating the details for loan Product in database
	 * 
	 * @param productId
	 */

	public static void updateProduct(int productId) {
		int index = 1;
		PreparedStatement stmt = null;
		ProductSQL.updateProduct(productId);
		Connection con = null;
		try {
			String sql = "UPDATE LoanProduct SET";
			if (AppStarter.inputs.containsKey(Constants.TOTALVALUE)) {
				sql += "totalValue =?";
			} else if (AppStarter.inputs.containsKey(Constants.RATE)) {
				sql += "rate =?";
			} else {
				return;
			}
			sql += " WHERE LOANID = (SELECT LOANID FROM PRODUCT WHERE PRODUCT_ID = ?)";
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(sql);
			if (AppStarter.inputs.containsKey(Constants.TOTALVALUE)) {
				stmt.setDouble(index++, Double.parseDouble(AppStarter.inputs.get(Constants.TOTALVALUE)));
			}
			if (AppStarter.inputs.containsKey(Constants.RATE)) {
				stmt.setDouble(index++, Double.parseDouble(AppStarter.inputs.get(Constants.RATE)));
			}
			stmt.setInt(index++, productId);
			stmt.executeUpdate();
			System.out.println("LoanProduct updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(stmt, con);
		}
	}

	/**
	 * Method for getting the details of loan product from the database
	 * 
	 * @param productId
	 * @return
	 */
	public static Product readProduct(int productId) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		Product loanProduct = ProductSQL.readProduct(productId);
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(GET_LOAN_PRODUCT);
			stmt.setLong(1, productId);

			rs = stmt.executeQuery();

			if (rs.next()) {

				int loanId = rs.getInt(LOANID);
				Long totalValue = rs.getLong(LOANVALUE);
				Double rate = rs.getDouble(INTERESTRATE);
				String option = rs.getString(PAYMENT_TYPE);
				List<Schedule> ls = ScheduleSQL.readDisbursementSchedule(loanId);
				loanProduct = new LoanProduct(loanProduct, loanId, totalValue, rate, ls, option);
				System.out.println("Product read successfully");

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
		return loanProduct;
	}

	/**
	 * Method to delete the product from the database
	 * 
	 * @param productId
	 */
	public static void deleteProduct(int productId) {
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			LoanProduct loanProduct = (LoanProduct) LoanProductSQL.readProduct(productId);
			ScheduleSQL.deleteDisbursementSchedule(loanProduct.getLoanId());
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(DELETE_LOAN_PRODUCT);
			stmt.setInt(1, productId);
			stmt.executeUpdate();
			ProductSQL.deleteProduct(productId);
			System.out.println("Product deleted successfully");

		} catch (Exception e) {
			// System.out.println("No schedule found for product id " + id);
		} finally {
			DbUtils.close(stmt, con);
		}
	}

}