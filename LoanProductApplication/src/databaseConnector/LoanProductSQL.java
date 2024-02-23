package databaseConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;

import databaseHelper.DatabaseHelper;
import product.LoanProduct;
import product.Product;
import product.Schedule;
import utils.DbUtils;
import utils.Util;

public class LoanProductSQL extends ProductSQL {

	protected static final String SAVE_LOAN_PRODUCT = "INSERT INTO  LOANPRODUCT (productId,loanvalue,interestrate) VALUES (?,?,?)";
	protected static final String READ_LOAN_PRODUCT = "SELECT * FROM LOANPRODUCT WHERE PRODUCTID=? ";

	/**
	 *  Method for inserting Loan product details in to the database
	 * @param p
	 * @throws Exception
	 */
	public static void insertLoanProduct(Product p) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
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

		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
		ScheduleSQL.insertDisbursementSchedule(p);
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

				int productId = rs.getInt("PRODUCTID");
				Long totalValue = rs.getLong("LOANVALUE");
				Double rate = rs.getDouble("INTERESTRATE");
				String status = rs.getString("STATUS");
				Date sDate = (Date) lp.getStartDate();
				Date eDate = (Date) lp.getEndDate();
				 Date currentDate = Util.toSQLDate(Calendar.getInstance().getTime());
				 if(status == null){
					if(currentDate.before(sDate)){
						status = "PENDING";
					}else if(currentDate.after(eDate)){
						status = "COMPLETED";
					}else{
						status = "ACTIVE";
					}
				}
				List<Schedule> ls = ScheduleSQL.readDisbursementSchedule(productId);
				lp = new LoanProduct(lp, totalValue, rate, ls, status);			
//				System.out.println("Displaying Product with id  " +id);

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
	public static void cancelProduct(int id) {
		PreparedStatement stmt = null;
		Connection con = null;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("UPDATE loanProduct SET status = ? WHERE productId = ?");
			stmt.setString(1, "CANCELLED");
			stmt.setInt(2, id);
			stmt.executeQuery();
			System.out.println("Product Cancelled successfully");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("No Product found for product id " + id);
		} finally {
			DbUtils.close(stmt, con);
		}
	}

}