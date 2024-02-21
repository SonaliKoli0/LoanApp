package test;
 
import static org.junit.Assert.*;
 
import java.util.ArrayList;
 
import org.junit.Test;
 
import databaseConnector.LoanProductSQL;
import product.Cashflow;
import product.LoanCashflow;
import product.LoanProduct;
import product.Product;
import utils.Util;
 
public class TestCashflow {
 
	@Test
	public void testLoanEMI() {
 
		Product lp = (LoanProduct) LoanProductSQL.readProduct(202);
		Cashflow cs = new LoanCashflow();
		ArrayList<Cashflow> cashflows = cs.generateCashflows(lp);
 
		ArrayList<Cashflow> expectedCashflows = new ArrayList<Cashflow>() {
 
			{
				add(new LoanCashflow(202, Util.toSQLDate("01/01/2026"), "PRINCIPAL", "OUT", 30000.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/02/2026"), "INTEREST", "IN", 150.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/02/2026"), "PRINCIPAL", "IN", 6000.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/03/2026"), "INTEREST", "IN", 120.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/03/2026"), "PRINCIPAL", "IN", 6000.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/03/2026"), "PRINCIPAL", "OUT", 30000.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/04/2026"), "INTEREST", "IN", 240.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/04/2026"), "PRINCIPAL", "IN", 16000.0));
				add(new LoanCashflow(202, Util.toSQLDate("12/04/2026"), "PRINCIPAL", "OUT", 40000.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/05/2026"), "INTEREST", "IN", 286.66666666666663));
				add(new LoanCashflow(202, Util.toSQLDate("01/05/2026"), "PRINCIPAL", "IN", 36000.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/06/2026"), "INTEREST", "IN", 180.0));
				add(new LoanCashflow(202, Util.toSQLDate("01/06/2026"), "PRINCIPAL", "IN", 36000.0));
			}
 
		};
 
		// Check each element individually
		for (int i = 0; i < expectedCashflows.size(); i++) {
			assertEquals(expectedCashflows.get(i), cashflows.get(i));
		}
 
	}
 
	@Test
	public void testAtMaturity() {
		Product lp = (LoanProduct) LoanProductSQL.readProduct(203);
		Cashflow cs = new LoanCashflow();
		ArrayList<Cashflow> cashflows = cs.generateCashflows(lp);
 
		ArrayList<Cashflow> expectedCashflows = new ArrayList<Cashflow>() {
 
			{
				add(new LoanCashflow(203, Util.toSQLDate("01/01/2026"), "PRINCIPAL", "OUT", 10000.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/02/2026"), "INTEREST", "IN", 50.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/03/2026"), "INTEREST", "IN", 50.0));
				add(new LoanCashflow(203, Util.toSQLDate("12/03/2026"), "PRINCIPAL", "OUT", 30000.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/04/2026"), "INTEREST", "IN", 146.7741935483871));
				add(new LoanCashflow(203, Util.toSQLDate("01/05/2026"), "INTEREST", "IN", 200.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/06/2026"), "INTEREST", "IN", 200.0));
				add(new LoanCashflow(203, Util.toSQLDate("15/06/2026"), "PRINCIPAL", "OUT", 30000.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/07/2026"), "INTEREST", "IN", 280.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/08/2026"), "INTEREST", "IN", 350.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/09/2026"), "INTEREST", "IN", 350.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/09/2026"), "PRINCIPAL", "OUT", 30000.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/10/2026"), "INTEREST", "IN", 500.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/11/2026"), "INTEREST", "IN", 499.99999999999994));
				add(new LoanCashflow(203, Util.toSQLDate("01/12/2026"), "INTEREST", "IN", 500.0));
				add(new LoanCashflow(203, Util.toSQLDate("01/01/2027"), "INTEREST", "IN", 499.99999999999994));
				add(new LoanCashflow(203, Util.toSQLDate("01/01/2027"), "PRINCIPAL", "IN", 100000.0));
			}
 
		};
 
		assertEquals(expectedCashflows.size(), cashflows.size());
 
		// Check each element individually
		for (int i = 0; i < expectedCashflows.size(); i++) {
			assertEquals(expectedCashflows.get(i), cashflows.get(i));
		}
	}
}

