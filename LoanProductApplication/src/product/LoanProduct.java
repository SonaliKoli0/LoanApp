package product;

import java.util.Date;
import java.util.List;

import databaseConnector.LoanProductSQL;
import utils.Util;

import java.util.ArrayList;

public class LoanProduct extends Product {

	private int loanId;
	private double rate;
	private double totalValue;
	private List<Schedule> disbursementSchedule;
	private Schedule schedule;

	public LoanProduct() {

	}

	public LoanProduct(int productId, int loanId, Date startDate, Date endDate, String productType, double totalValue,
			double rate, List<Schedule> disbursementSchedule, ArrayList<Cashflow> cashflows) {

		super(productId, productType, startDate, endDate, cashflows);
		this.loanId = loanId;
		this.rate = rate;
		this.totalValue = totalValue;
		this.disbursementSchedule = disbursementSchedule;

	}

	public LoanProduct(Product p, int loanId, double totalValue, double rate, List<Schedule> disbursementSchedule) {

		super(p);
		this.loanId = loanId;
		this.rate = rate;
		this.totalValue = totalValue;
		this.disbursementSchedule = disbursementSchedule;

	}

	public int getLoanId() {
		return loanId;
	}

	public void setLoanId(int loanId) {
		this.loanId = loanId;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public List<Schedule> getDisbursementSchedule() {

		return disbursementSchedule;
	}

	public void setDisbursementSchedule(List<Schedule> disbursementSchedule) {
		this.disbursementSchedule = disbursementSchedule;
	}

	// Method prints disbursement schedule
	public String printDisbursementSchedule() {
		String str = new String();
		str += "{";
		for (Schedule s : disbursementSchedule) {
			str += s.toString() + ",";
		}
		str += "}";
		return str;
	}

	/**
	 * updates product details using the product id given by user and the
	 * details user want to update
	 */

	@Override
	public void updateProduct(int productId) {
		try {
			LoanProductSQL.updateProduct(productId);

		} catch (Exception e) {
			System.out.println("Product with product Id " + productId + "does not exist");
		}

	}

	/**
	 * creates the product using the product details given by user
	 */
	@Override
	public void createProduct() {

		try {
			LoanProductSQL.insertLoanProduct(this);
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * Returns the product details using the productId
	 */
	@Override
	public Product readProduct(int id) {
		Product lp = null;
		try {

			lp = LoanProductSQL.readProduct(id);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return lp;
	}

	/**
	 * Deletes product by using productId
	 */
	@Override
	public void deleteProduct(int ProductId) {

		try {

			LoanProductSQL.deleteProduct(ProductId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getCashflow(int productId, Date date, String cashflowType) {
		LoanProduct lp = (LoanProduct) LoanProductSQL.readProduct(productId);
		
		ArrayList<Cashflow> cashflows = LoanCashflow.generateCashflows(lp, cashflowType);
		lp.setCashflows(cashflows);
		for (int i = 0; i < cashflows.size(); i++) {
			LoanCashflow cashflow = (LoanCashflow) cashflows.get(i);
			System.out.println(cashflow);
		}

	}

	// Method for printing the loan product details
	@Override
	public String toString() {
		return "LoanProduct{" +

				"productId=" + this.getProductId() + ", loanId=" + getLoanId() + ", rate=" + rate + ",Start Date="
				+ Util.formatDate(getStartDate()) + ",End Date=" + Util.formatDate(getEndDate()) + ", totalValue="
				+ totalValue + ", disbursementSchedule= " + disbursementSchedule + '}';
	}

}