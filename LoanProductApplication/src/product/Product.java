package product;

import java.util.Date;
import product.AppStarter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.Util;

public class Product {

	public static final String NEW = "NEW";
	public static final String AMEND = "AMEND";
	public static final String READ = "READ";
	public static final String REMOVE = "REMOVE";
	public static final String ACTION = "Action";
	public static final String LOAN = "Loan";
	public static final String CASHFLOW = "CASHFLOW";

	private int productId;
	private Date startDate;
	private Date endDate;
	private String action;
	private ArrayList<Cashflow> cashflows;
	private String productType;

	public Product() {

	}

	public Product(Product p) {

		this.productId = p.getProductId();
		this.startDate = p.getStartDate();
		this.endDate = p.getEndDate();
		this.cashflows = p.getCashflows();
		this.productType = p.getProductType();
	}

	public Product(int productId, String productType, Date startDate, Date endDate, ArrayList<Cashflow> cashflows) {
		this.productId = productId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.cashflows = cashflows;
		this.productType = productType;
	}

	// returns productId
	public int getProductId() {
		return productId;
	}

	// sets productId
	public void setProductId(int productId) {
		this.productId = productId;
	}

	// returns start date
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ArrayList<Cashflow> getCashflows() {
		return cashflows;
	}

	public void setCashflows(ArrayList<Cashflow> cashflows) {
		this.cashflows = cashflows;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public List<Schedule> getDisbursementSchedule() {

		return null;
	}

	/**
	 * method calls the corresponding action that user wants to perform.
	 * 
	 * @param Action
	 * @param p
	 */
	public void callAction(String Action, Product product) {
		switch (Action) {
		case Product.READ: {
			Product loanProduct = (LoanProduct) product;
			loanProduct = loanProduct.readProduct(loanProduct.getProductId());
			System.out.println(loanProduct);
		}
			break;
		case Product.REMOVE: {
			Product loanProduct = (LoanProduct) product;
			loanProduct.deleteProduct(loanProduct.getProductId());
		}
			break;
		case Product.CASHFLOW: {
			LoanProduct loanProduct = (LoanProduct) product;
			// Date date = Util.parseDate(AppStarter.inputs.get("Date"));
			loanProduct.getCashflow(loanProduct.getProductId());
		}
			break;
		case Product.AMEND: {
			LoanProduct loanProduct = (LoanProduct) product;
			int id = product.getProductId();
			loanProduct = (LoanProduct) loanProduct.readProduct(id);
			loanProduct.updateProduct(id);
			loanProduct.setProductId(id);
			callAction(Product.READ, loanProduct);
		}
			break;
		default:
			break;
		}

	}

	// gives product details
	protected Product readProduct(int productId) {
		return null;
	}

	// updates product using productId
	protected void updateProduct(int productId) {
	}

	// deletes product using productId
	protected void deleteProduct(int productId) {
	}

	// creates product
	protected void createProduct() {
	}

	// Method for generating cash flow
	protected void getCashflow(int productId) {
		// TODO Auto-generated method stub

	}

	public static LoanProduct createLoanProduct(HashMap<String, String> inputs) throws Exception {
		String productType = inputs.get("productType");
		Date startDate = Util.parseDate(inputs.get("startDate"));
		Date endDate = Util.parseDate(inputs.get("endDate"));
		double rate = Double.parseDouble(inputs.get("rate"));
		double totalValue = Double.parseDouble(inputs.get("totalValue"));
		String schedule = inputs.get("schedule");
		String paymentOption = inputs.get("paymentOption");
		String[] scheduleArray = schedule.split("\\_");
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
	}

	// checks which action wants to perform and calls that action
	public static Product checkAction(HashMap<String, String> inputs) throws Exception {
		String productType = inputs.get("productType");
		String action = inputs.get(Product.ACTION);
		LoanProduct loanProduct = new LoanProduct();
		if (action.equals(Product.NEW)) {
			if (productType.equals("Loan")) {
				loanProduct = Product.createLoanProduct(inputs);
				loanProduct.createProduct();
				System.out.println(loanProduct);
				return loanProduct;
			}
		} else {
			loanProduct.setProductId(Integer.parseInt(inputs.get("productId")));
			loanProduct.callAction(action, loanProduct);
			return loanProduct;
		}

		return null;
	}

	public static void inputValidation(Date startDate, Date endDate, double totalValue, double rate,
			List<Schedule> disbursementSchedule) {
		if (startDate.compareTo(endDate) != -1) {
			System.err.print("start Date cannot be greater than or equal to endDate!!");
			System.exit(0);
		}

		double checkTotalValue = 0;
		for (int i = 0; i < disbursementSchedule.size(); i++) {
			checkTotalValue += disbursementSchedule.get(i).getAmount();
		}

		if (totalValue != checkTotalValue) {
			System.err.print("Total of disbursed amount should be equals to TotalLoanValue!!");
			System.exit(0);
		}
	}

	// for printing product details
	@Override
	public String toString() {
		return "Product{" + "productId=" + productId + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", cashflows=" + cashflows + '}';
	}

	public void createLoanProduct() {
		// TODO Auto-generated method stub

	}

}
