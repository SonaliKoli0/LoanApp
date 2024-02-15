package utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtils {
	// Method for creating new prepared statement for executing query
	 public static PreparedStatement newPreparedStatement(Connection con, String st) throws SQLException {

		if (con == null) {
			throw new SQLException("CalypsoException: ioSQL.newPreparedStatement connection is null");
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(st);
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return stmt;
	}
	//Method to close the statement
	 static public void close(Statement stmt) {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception e) {
			}
		}

}
