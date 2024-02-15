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
	protected static final String SAVE_RepaymentSchedule = "INSERT INTO  RepaymentSchedule (LID,RepaymentDate,RepaymentAmount) VALUES (?,?, ?)";
    //Method for adding the disbursement schedule in the DB
	public static void insertDisbursementSchedule(Product p) throws Exception {
		PreparedStatement stmt = null;
		long id = -1;
		int affectedRows = 0;
		LoanProduct lp = (LoanProduct) p;
		try {
			Connection con = DatabaseHelper.getConnection();

			if (p.getStartDate() != null) {
				for (Schedule schedule : p.getDisbursementSchedule()) {
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
			try (ResultSet generatedKeys = stmt
					.executeQuery("SELECT product_id FROM Product WHERE rownum = 1 ORDER BY product_id DESC")) {
				if (generatedKeys.next()) {
					id = generatedKeys.getLong("product_id");

					p.setProductId((int) id);
				} else {
					throw new SQLException("Inserting product failed, no ID obtained.");
				}
			}

		} catch (Exception e) {
			throw e;
		} finally {

			DbUtils.close(stmt);
		}
	}
    //Method for getting the disbursement schedule from the DB
	public static List<Schedule> readDisbursementSchedule(int loanId) {

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		List<Schedule> ls = new ArrayList<Schedule>();
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("SELECT * FROM disbursementschedule WHERE disbursementSchedule.LoanID=?");
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
			DbUtils.close(stmt);
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return ls;
	}
	//Method for getting the disbursement schedule from the DB
		public static List<Schedule> readRepaymentSchedule(int loanId) {

			PreparedStatement stmt = null;
			ResultSet rs = null;
			Connection con = null;
			List<Schedule> ls = new ArrayList<Schedule>();
			try {
				con = DatabaseHelper.getConnection();
				stmt = con.prepareStatement("SELECT * FROM repaymentschedule WHERE repaymentSchedule.LID=?");
				stmt.setInt(1, loanId);
				rs = stmt.executeQuery();
				while (rs.next()) {
					int id = rs.getInt("repaymentId");
					Double amount = rs.getDouble("repaymentamount");
					Date date = rs.getDate("repaymentDate");
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
				DbUtils.close(stmt);
				if (con != null) {
					try {
						con.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			return ls;
		}
    //Method for adding re-payment details in  the DB
	public static void insertRepaymentSchedule(int id, Double amount, Date date) throws Exception {
		PreparedStatement stmt = null;
		int j = 1;
		int affectedRows = 0;
		try {
			Connection con = DatabaseHelper.getConnection();
			if (amount != 0) {
				stmt = con.prepareStatement(SAVE_RepaymentSchedule, Statement.RETURN_GENERATED_KEYS);
				stmt.setInt(j++, id);
				stmt.setDate(j++, date);
				stmt.setDouble(j++, amount);
				affectedRows = stmt.executeUpdate();
				System.out.println("Repayment details added successfully!");
			}
			if (affectedRows == 0) {
				throw new SQLException("Inserting product failed, no rows affected.");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			DbUtils.close(stmt);
		}
	}

	


    //Method for deleting the disbursement schedule from DB
	public static void deleteDisbursementSchedule(int id) {

		PreparedStatement stmt = null;
		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = DbUtils.newPreparedStatement(con, "DELETE FROM disbursementSchedule WHERE LOANID=?");
			stmt.setLong(1, id);

			stmt.executeUpdate();
			System.out.println("Disbursement deleted successfully");

		} catch (Exception e) {
			System.out.println("No disbursement schedule found for loan id " + id);
		} finally {

			DbUtils.close(stmt);
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
    //Method for deleting the re-payment schedule from the DB
	public static void deleteRepaymentSchedule(int id) {

		PreparedStatement stmt = null;

		Connection con = null;

		try {
			con = DatabaseHelper.getConnection();
			stmt = DbUtils.newPreparedStatement(con, "DELETE FROM repaymentSchedule WHERE LID=?");
			stmt.setLong(1, id);
			stmt.executeUpdate();
			System.out.println("Repayment deleted successfully");

		} catch (Exception e) {
			System.out.println("No repayment schedule found for loan id " + id);
		} finally {

			DbUtils.close(stmt);
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	
}