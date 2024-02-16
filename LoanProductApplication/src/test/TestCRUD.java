package test;

import static org.junit.Assert.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;
import databaseConnector.LoanProductSQL;
import databaseConnector.ProductSQL;
import databaseHelper.DatabaseHelper;
import product.AppStarter;
import product.LoanProduct;
import product.Product;
import utils.DbUtils;
import utils.Util;

//@SuppressWarnings("hiding")
public class TestCRUD {

	private Connection con;
	private PreparedStatement stmt;
	
	/**
	 * Tests working of creatProduct method 
	 * @throws Exception
	 */
	@Test
	public void testCreateProduct() throws Exception {
		// Create a test product
		String productType = "Loan";
		// You can replace these values with any other test values as needed
		Date startDate = Util.toSQLDate(Util.parseDate(("15/04/2023")));
		Date endDate = Util.toSQLDate(Util.parseDate(("15/04/2024")));

		Product product = new Product(-1, productType, startDate, endDate, new ArrayList<>());
		ProductSQL.insert(product);

		// Check that the product is not null
		assertNotNull(product);

		stmt = null;
		ResultSet rs = null;
		con = null;

		int productId = 0;
		String testProductType = "";
		Date testStartDate = null;
		Date testEndDate = null;
		int latestRpoductId = 0;
		try {
			con = DatabaseHelper.getConnection();
			stmt = con.prepareStatement("SELECT MAX(product_Id) AS maxProductId FROM Product");

			rs = stmt.executeQuery();

			if (rs.next()) {
				latestRpoductId = rs.getInt("maxProductId");
				System.out.println(rs.getInt("maxProductId"));
			} else {
				throw new SQLException("No data found in Product table");
			}
			rs = null;
			stmt = con.prepareStatement("SELECT * FROM PRODUCT WHERE PRODUCT_ID=?");
			stmt.setLong(1, latestRpoductId);

			rs = stmt.executeQuery();

			if (rs.next()) {
				productId = rs.getInt("PRODUCT_ID");
				testProductType = rs.getString("PRODUCTTYPE");
				testStartDate = rs.getDate("STARTDATE");
				testEndDate = rs.getDate("ENDDATE");

			} else {
				System.out.println("There is no product with id " + productId);
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

		assertEquals(testProductType, product.getProductType());
		assertEquals(testStartDate, product.getStartDate());
		assertEquals(testEndDate, product.getEndDate());
	}
	
	/**
	 * Tests ReadProduct Method
	 * @throws SQLException
	 */
	@Test
	public void testReadProduct() throws SQLException {
		// Read the test product from the database
		Product product = ProductSQL.readProduct(266); 

		// Verify that the product is not null
		assertNotNull(product);

		// Verify the properties of the product
		assertEquals("Loan", product.getProductType());
		assertEquals(Util.toSQLDate(Util.parseDate(("17/01/2023"))), product.getStartDate());
		assertEquals(Util.toSQLDate(Util.parseDate(("17/01/2025"))), product.getEndDate());
	}
	
	/**
	 * Tests deleting functionality
	 * @throws SQLException
	 */
	@Test
	public void testDeleteProduct() throws SQLException{
		ProductSQL.deleteProduct(267);
		Product product = ProductSQL.readProduct(267); 
		
		assertNull(product);	
	}
	
	/**
	 * Tests updating functionality of Product
	 * @throws SQLException
	 */
	@Test
	public void testUpdateProduct() throws SQLException{
		AppStarter.inputs = new HashMap<>();
		AppStarter.inputs.put("endDate", "19/01/2027");
		 ProductSQL.updateProduct(265);
		
		 //Reading the updated product
		 Product p = ProductSQL.readProduct(265);
		 
		 //checking where endDate changed or not
		 assertEquals(Util.toSQLDate(Util.parseDate(("19/01/2027"))), p.getEndDate());	

	}
	
	/**
	 * test Reading product from dataBase
	 * @throws SQLException
	 */
	@Test
	public void testReadLoanProduct() throws SQLException {
		// Read the test product from the database
		LoanProduct loanProduct = (LoanProduct) LoanProductSQL.readProduct(266); 

		// Verify that the product is not null
		assertNotNull(loanProduct);

		// Verify the properties of the product
		assertEquals("Loan", loanProduct.getProductType());
		assertEquals(104, loanProduct.getLoanId());
		assertTrue(loanProduct.getTotalValue() == 120000);
		assertTrue(loanProduct.getRate() == 6);
	}
}
