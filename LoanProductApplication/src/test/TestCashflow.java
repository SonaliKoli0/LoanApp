package test;

import static org.junit.Assert.*;

import java.sql.Date;
import java.time.LocalDate;
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

		Product lp = (LoanProduct) LoanProductSQL.readProduct(289);
		Cashflow cs = new LoanCashflow();
		ArrayList<Cashflow> cashflows = cs.generateCashflows(lp);
		
		ArrayList<Cashflow> expectedCashflows = new ArrayList<Cashflow>() {

			{
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/01/2026")), "PRINCIPAL", "OUT", 10000.0));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/02/2026")), "INTEREST", "IN", 50.0));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/02/2026")), "PRINCIPAL", "IN",
						1111.111111111111));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("12/03/2026")), "PRINCIPAL", "OUT", 30000.0));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/03/2026")), "INTEREST", "IN",
						97.67025089605735));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/03/2026")), "PRINCIPAL", "IN",
						4861.111111111111));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/04/2026")), "INTEREST", "IN",
						170.1388888888889));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/04/2026")), "PRINCIPAL", "IN",
						4861.111111111111));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/05/2026")), "INTEREST", "IN",
						145.83333333333337));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/05/2026")), "PRINCIPAL", "IN",
						4861.111111111112));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("15/06/2026")), "PRINCIPAL", "OUT", 30000.0));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/06/2026")), "INTEREST", "IN",
						191.52777777777777));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/06/2026")), "PRINCIPAL", "IN",
						10861.111111111113));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/07/2026")), "INTEREST", "IN",
						217.22222222222226));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/07/2026")), "PRINCIPAL", "IN",
						10861.111111111113));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/08/2026")), "INTEREST", "IN",
						162.91666666666669));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/08/2026")), "PRINCIPAL", "IN",
						10861.111111111113));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/09/2026")), "PRINCIPAL", "OUT", 30000.0));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/09/2026")), "INTEREST", "IN",
						108.61111111111113));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/09/2026")), "PRINCIPAL", "IN",
						25861.111111111113));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/10/2026")), "INTEREST", "IN",
						129.30555555555557));
				add(new LoanCashflow(289, Util.toSQLDate(Util.parseDate("01/10/2026")), "PRINCIPAL", "IN",
						25861.111111111113));
			}

		};
		
		assertEquals(expectedCashflows.size(), cashflows.size());

	    // Check each element individually
	    for (int i = 0; i < expectedCashflows.size(); i++) {
	        assertEquals(expectedCashflows.get(i), cashflows.get(i));
	    }

	}

}
