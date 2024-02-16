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
import utils.DbUtils;

public class LoanProductSQL extends ProductSQL {

	protected static final String SAVE_LOAN_PRODUCT = "INSERT INTO  LOANPRODUCT (productId,loanvalue,interestrate) VALUES (?,?,?)";
	protected static final String READ_LOAN_PRODUCT = "SELECT * FROM LOANPRODUCT INNER JOIN PRODUCT ON LOANPRODUCT.PRODUCTID=PRODUCT.PRODUCT_ID WHERE LOANPRODUCT.PRODUCTID=? ";

	/**
	 *  Method for inserting Loan product details in to the database
	 * @param p
	 * @throws Exception
	 */
	public static void insertLoanProduct(Product p) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
		long id = -1;
		ProductSQL.insert(p);
		LoanProduct lp = (LoanProduct) p;
		Connection con = null;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(SAVE_LOAN_PRODUCT, Statement.RETURN_GENERATED_KEYS);
			if (p.getStartDate() != null) {
				stmt.setInt(j++, p.getProductId());
				stmt.setInt(j++, (int) lp.getTotalValue());
				stmt.setInt(j++, (int) lp.getRate());

			} else {
				stmt.setNull(j++, Types.DATE);
			}

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}
			try (ResultSet generatedKeys = stmt
					.executeQuery("SELECT loanId FROM LoanProduct WHERE rownum = 1 ORDER BY loanId DESC")) {
				if (generatedKeys.next()) {
					id = generatedKeys.getLong("loanId");

					lp.setLoanId((int) id);

				} else {
					throw new SQLException("Inserting product failed, no ID obtained.");
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
		ScheduleSQL.insertDisbursementSchedule(p);
	}

	/**
	 *  Method for updating the details for loan Product in database
	 * @param id
	 */

	public static void updateProduct(int id) {
		int index = 1;
		PreparedStatement stmt = null;
		ProductSQL.updateProduct(id);
		Connection con = null;
		try {
			String sql = "UPDATE LoanProduct SET";
			if (AppStarter.inputs.containsKey("totalValue")) {
				sql += "totalValue =?";
			}
			else if (AppStarter.inputs.containsKey("rate")) {
				sql += "rate =?";
			} else {
				return;
			}
			sql += " WHERE LOANID = (SELECT LOANID FROM PRODUCT WHERE PRODUCT_ID = ?)";
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(sql);
			if (AppStarter.inputs.containsKey("totalValue")) {
				stmt.setDouble(index++, Double.parseDouble(AppStarter.inputs.get("totalValue")));
			}
			if (AppStarter.inputs.containsKey("rate")) {
				stmt.setDouble(index++, Double.parseDouble(AppStarter.inputs.get("rate")));
			}
			stmt.setInt(index++, id);
			stmt.executeUpdate();
			System.out.println("LoanProduct updated successfully");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbUtils.close(stmt, con);
		}
	}

	/**
	 *  Method for getting the details of loan product from the database
	 * @param id
	 * @return
	 */
	public static Product readProduct(int id) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		Product lp = ProductSQL.readProduct(id);
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("SELECT * FROM LOANPRODUCT WHERE PRODUCTID=?");
			stmt.setLong(1, id);

			rs = stmt.executeQuery();

			if (rs.next()) {

				int loanId = rs.getInt("LOANID");
				Long totalValue = rs.getLong("LOANVALUE");
				Double rate = rs.getDouble("INTERESTRATE");
				List<Schedule> ls = ScheduleSQL.readDisbursementSchedule(loanId);
				lp = new LoanProduct(lp, loanId, totalValue, rate, ls);
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
		return lp;
	}

	/**
	 *  Method to delete the product from the database
	 * @param id
	 */
	public static void deleteProduct(int id) {
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			LoanProduct lp = (LoanProduct) LoanProductSQL.readProduct(id);
			ScheduleSQL.deleteDisbursementSchedule(lp.getLoanId());
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("DELETE FROM LOANPRODUCT WHERE PRODUCTID=?");
			stmt.setInt(1, id);
			stmt.executeUpdate();
			ProductSQL.deleteProduct(id);
			System.out.println("Product deleted successfully");

		} catch (Exception e) {
			System.out.println("No schedule found for product id " + id);
		} finally {
			DbUtils.close(stmt, con);
		}
	}

}