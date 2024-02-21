package product;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import databaseConnector.LoanProductSQL;
import utils.Constants;
import utils.Util;

import java.util.ArrayList;

public class LoanProduct extends Product {

	protected int loanId;
	protected double rate;
	protected double totalValue;
	protected List<Schedule> disbursementSchedule;
	protected Schedule schedule;
	protected String paymentOption;

	public LoanProduct() {

	}

	public LoanProduct(int productId, int loanId, Date startDate, Date endDate, String productType, double totalValue,
			double rate, List<Schedule> disbursementSchedule, ArrayList<Cashflow> cashflows, String paymentOption) {

		super(productId, productType, startDate, endDate, cashflows);
		this.loanId = loanId;
		this.rate = rate;
		this.totalValue = totalValue;
		this.disbursementSchedule = disbursementSchedule;
		this.paymentOption = paymentOption;

	}

	public LoanProduct(Product p, int loanId, double totalValue, double rate, List<Schedule> disbursementSchedule,
			String paymentOption) {

		super(p);
		this.loanId = loanId;
		this.rate = rate;
		this.totalValue = totalValue;
		this.disbursementSchedule = disbursementSchedule;
		this.paymentOption = paymentOption;

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

	@Override
	public void getCashflow(int productId) {
		Product lp = (LoanProduct) LoanProductSQL.readProduct(productId);
		
		ArrayList<Cashflow> cashflows =getListOfCashflows(lp); 
		lp.setCashflows(cashflows);
		System.out.println(LoanCashflow.PID + "\t" + LoanCashflow.FLOW + "\t" + Constants.DATE + "\t\t"
				+ LoanCashflow.TYPE + "\t\t" + Constants.AMOUNT);
		for (int i = 0; i < cashflows.size(); i++) {
			LoanCashflow cashflow = (LoanCashflow) cashflows.get(i);
			System.out.println(cashflow);
		}

	}
	@Override
	public ArrayList<Cashflow> getListOfCashflows(Product product){
		Cashflow cs = new LoanCashflow();
		return cs.generateCashflows(product);
	}
	

	public Product buildProduct(HashMap<String, String> inputs) throws Exception {
		try {
			String productType = inputs.get(Constants.PRODUCTTYPE);
			Date startDate = Util.parseDate(inputs.get(Constants.STARTDATE));
			Date endDate = Util.parseDate(inputs.get(Constants.ENDDATE));
			
			double rate = Double.parseDouble(inputs.get(Constants.RATE));
			double totalValue = Double.parseDouble(inputs.get(Constants.TOTALVALUE));
			String schedule = inputs.get(Constants.SCHEDULE);
			String paymentOption = inputs.get(Constants.PAYMENTOPTION);
			if(paymentOption==null){
				paymentOption=Constants.MONTHLYPAYMENT;
			}
			String[] scheduleArray = schedule.split("\\,");
			List<Schedule> disbursementSchedule = new ArrayList<Schedule>();
			for (String s : scheduleArray) {
				Schedule sch = new Schedule();
				String[] oneSchedule = s.split("=");
				sch.setDate(Util.parseDate(oneSchedule[0]));
				sch.setAmount(Double.parseDouble(oneSchedule[1]));
				disbursementSchedule.add(sch);
			}
			Product.inputValidation(startDate, endDate, totalValue, rate, disbursementSchedule);

			LoanProduct loanProduct = new LoanProduct(-1, -1, startDate, endDate, productType, totalValue, rate,
					disbursementSchedule, null, paymentOption);
			return loanProduct;
		} catch (Exception e) {
			System.err.print(Constants.DETAILS_ERROR);
			System.exit(0);
		}
		return null;
	}

	// Method for printing the loan product details
	@Override
	public String toString() {
		StringBuilder loanProduct= new StringBuilder();
		loanProduct.append("\nLoan Product Details:\n");
		loanProduct.append(Constants.PRODUCTID);
		loanProduct.append("\t\t");
		loanProduct.append(this.getProductId());
		loanProduct.append("\n\n");
		loanProduct.append(Constants.LOANID);
		loanProduct.append("\t\t\t");
		loanProduct.append(getLoanId());
		loanProduct.append("\n\n");
		loanProduct.append(Constants.RATE);
		loanProduct.append("\t\t\t");
		loanProduct.append(rate);
		loanProduct.append("\n\n");
		loanProduct.append(Constants.STARTDATE);
		loanProduct.append("\t\t");
		loanProduct.append(Util.formatDate(getStartDate()));
		loanProduct.append("\n\n");
		loanProduct.append(Constants.ENDDATE);
		loanProduct.append("\t\t\t");
		loanProduct.append(Util.formatDate(getEndDate()));
		loanProduct.append("\n\n");
		loanProduct.append(Constants.TOTALVALUE);
		loanProduct.append("\t\t");
		loanProduct.append(totalValue);
		loanProduct.append("\n\n");
		loanProduct.append(Constants.SCHEDULE);
		loanProduct.append("\t\t");
		loanProduct.append(disbursementSchedule);
		loanProduct.append("\n\n");
		loanProduct.append(Constants.PAYMENTOPTION);
		loanProduct.append("\t\t");
		loanProduct.append(paymentOption);
		loanProduct.append("\n\n");
		loanProduct.append(Constants.CASHFLOW);
		loanProduct.append("\t\t");
		loanProduct.append(this.getCashflows());
		loanProduct.append("\n\n");
		return loanProduct+"";
		
	}

	public String getPaymentOption() {
		return paymentOption;
	}

	public void setPaymentOption(String paymentOption) {
		this.paymentOption = paymentOption;
	}

}