package product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import databaseHelper.DatabaseHelper;

public class AppStarter {
	public static HashMap<String, String> inputs;
   public static void setInputs(String[] args){
	   inputs = new HashMap<>();
		for (int i = 0; i < args.length - 1; i++) {
			if (i < args.length - 1) {
				inputs.put(args[i], args[i + 1]);
				i++;
			}
		}
   }
	public static void main(String[] args) throws Exception {
		try {
			// Establish database connection
			Connection connection = DatabaseHelper.getConnection();
			// Perform database operations here
			System.out.println("APP Started");
			// Close the connection
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		setInputs(args);
		//for performing the action on the product based on the user inputs

		Product.checkAction(inputs);

	}
}
