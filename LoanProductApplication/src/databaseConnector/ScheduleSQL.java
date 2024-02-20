package databaseConnector;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import databaseHelper.DatabaseHelper;
import product.LoanProduct;
import product.Product;
import product.Schedule;
import utils.DbUtils;
import utils.Util;

public class ScheduleSQL {

	protected static final String SAVE_DisbursementSchedule = "INSERT INTO  DisbursementSchedule (LoanID,DisbursementDate,DisbursementAmount) VALUES (?,?, ?)";
	protected static final String DELETE_DisbursementSchedule = "DELETE FROM disbursementSchedule WHERE LOANID=?";
	protected static final String GET_DisbursementSchedule_BY_LOAN_ID = "SELECT * FROM disbursementschedule WHERE disbursementSchedule.LoanID=?";
	
	/**
	 * Method for adding the disbursement schedule in the DB
	 * @param product
	 * @throws Exception
	 */
	public static void insertDisbursementSchedule(Product product) throws Exception {
		PreparedStatement stmt = null;
		
		int affectedRows = 0;
		LoanProduct lp = (LoanProduct) product;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();

			if (product.getStartDate() != null) {
				for (Schedule schedule : product.getDisbursementSchedule()) {
					int index = 1;
					stmt = con.prepareStatement(SAVE_DisbursementSchedule, Statement.RETURN_GENERATED_KEYS);
					stmt.setInt(index++, lp.getLoanId());
					stmt.setDate(index++, Util.toSQLDate(schedule.getDate()));
					stmt.setDouble(index++, (schedule.getAmount()));
					affectedRows = stmt.executeUpdate();
				}
			}

			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}
		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt, con);
		}
	}

	/**
	 * Method for getting the disbursement schedule from the DB
	 * 
	 * @param loanId
	 * @return
	 */
	public static List<Schedule> readDisbursementSchedule(int loanId) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		List<Schedule> ls = new ArrayList<Schedule>();
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement(GET_DisbursementSchedule_BY_LOAN_ID);
			stmt.setInt(1, loanId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("DisbursementId");
				Double amount = rs.getDouble("disbursementamount");
				Date date = rs.getDate("DisbursementDate");
				Schedule sch = new Schedule(id, amount, date);
				ls.add(sch);

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
		return ls;

	}

	/**
	 * Method for deleting the disbursement schedule from DB
	 * 
	 * @param id
	 */
	public static void deleteDisbursementSchedule(int id) {

		PreparedStatement stmt = null;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = DbUtils.newPreparedStatement(con, DELETE_DisbursementSchedule);
			stmt.setLong(1, id);

			stmt.executeUpdate();
			System.out.println("Disbursement deleted successfully");

		} catch (Exception e) {
			System.err.println("No disbursement schedule found for loan id " + id);
		} finally {

			DbUtils.close(stmt, con);
		}
	}

}