package product;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import databaseHelper.DatabaseHelper;
import utils.Constants;

public class AppStarter {
	public static HashMap<String, Object> inputs = new HashMap<>();

	public static void addInput(String key, Object val) {
		inputs.put(key, val);
		return;
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
		AppStarter.getInputs();

	}

	// for performing the action on the product based on the user inputs
	public static void getInputs() throws Exception {
		while (true) {
			try {
				System.out.println("Select action you want to perform:\n1.New\n2.Update\n3.Read\n4.Cancel\n5.Exit");
				System.out.println("Select one action from above\n");
				Scanner read = new Scanner(System.in);
				String action = read.next();
				if (action.equals("1") || action.equals("2") || action.equals("3") || action.equals("4")
						|| (action.equals("5"))) {
					if (action.equals("5")) {

						System.out.println("Thank you :)");
						System.exit(0);

					}

					AppStarter.addInput(Constants.ACTION, action);
					System.out.println("Select product type :\n1.Loan\n");
					System.out.println("Select product Type from above:\n");
					String productType = read.next();
					if (!(productType.equals("1"))) {
						System.out.println("Please select Valid option\n");
						AppStarter.getInputs();
					}
					AppStarter.addInput(Constants.PRODUCTTYPE, productType);

					Product.checkAction(productType, action);
				} else {
					System.out.println("Please select Valid option\n");
					AppStarter.getInputs();
				}
			} catch (Exception e) {
				System.exit(0);
			}
		}
	}
}