package product;

import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utils.Constants;
import utils.Util;

public class Product {
	public final static String START_DATE_ERROR = "start Date cannot be greater than or equal to endDate!!";
	public final static String TOTAL_VALUE_SUM_ERROR = "Total of disbursed amount should be equals to TotalLoanValue!!";
	public final static String DISBURSEMENT_DATE_ERROR = "Last disbursement Date should be at least one month before end date !!";
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
	 * @param product
	 */
	public void callAction(String Action, Product product) {
		switch (Action) {
			case Constants.READ: {
		
				product = product.readProduct(product.getProductId());
				ArrayList<Cashflow> list =product.getListOfCashflows(product);
				product.setCashflows(list);
				System.out.println(product);
			}
				break;
			case Constants.REMOVE: {
				
				product.deleteProduct(product.getProductId());
				
			}
				break;
			case Constants.CASHFLOW: {
				
			
				product.getCashflow(product.getProductId());
			}
				break;
			case Constants.AMEND: {
				
				int id = product.getProductId();
				product =  product.readProduct(id);
				product.updateProduct(id);
				product.setProductId(id);
				callAction(Constants.READ, product);
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

	public Product buildProduct(HashMap<String, String> inputs) throws Exception {
		return null;
	}

	// checks which action wants to perform and calls that action
	public static Product checkAction(HashMap<String, String> inputs) throws Exception {
		try {
			String productType = inputs.get(Constants.PRODUCTTYPE);
			String action = inputs.get(Constants.ACTION);

			if (productType.equals(Constants.LOAN)) {
				LoanProduct loanProduct = new LoanProduct();
				if (action.equals(Constants.NEW)) {
					loanProduct = (LoanProduct) loanProduct.buildProduct(inputs);
					loanProduct.createProduct();
					System.out.println(loanProduct);
					return loanProduct;

				} else {
					loanProduct.setProductId(Integer.parseInt(inputs.get(Constants.PRODUCTID)));
					loanProduct.callAction(action, loanProduct);
					return loanProduct;
				}
			}
		} catch (Exception e) {
			System.err.print(Constants.DETAILS_ERROR);
			System.exit(0);
		}

		return null;
	}

	public static void inputValidation(Date startDate, Date endDate, double totalValue, double rate,
			List<Schedule> disbursementSchedule) {
		if (startDate.compareTo(endDate) != -1) {
			System.err.print(Product.START_DATE_ERROR);
			System.exit(0);
		}
		if (Util.addMonths(disbursementSchedule.get(disbursementSchedule.size() - 1).getDate(), 1).after(endDate)) {
			System.err.print(Product.DISBURSEMENT_DATE_ERROR);
			System.exit(0);
		}
		double checkTotalValue = 0;
		for (int i = 0; i < disbursementSchedule.size(); i++) {
			checkTotalValue += disbursementSchedule.get(i).getAmount();
		}

		if (totalValue != checkTotalValue) {
			System.err.print(Product.TOTAL_VALUE_SUM_ERROR);
			System.exit(0);
		}
	}

	// for printing product details
	@Override
	public String toString() {
		StringBuilder product= new StringBuilder();
		product.append(Constants.PRODUCTID);
		product.append("\t\t");
		product.append(this.getProductId());
		product.append("\n");
		product.append(Constants.STARTDATE);
		product.append("\t\t");
		product.append(Util.formatDate(getStartDate()));
		product.append("\n");
		product.append(Constants.ENDDATE);
		product.append("\t\t");
		product.append(Util.formatDate(getEndDate()));
		product.append("\n");
		
		product.append(Constants.SCHEDULE);
		product.append("\t\t");
	
		product.append(Constants.CASHFLOW);
		product.append("\t\t");
		product.append(this.getCashflows());
		product.append("\n");
		return product+"";
		
//		return Constants.PRODUCT + "{" + Constants.PRODUCTID + "=" + productId + "," + Constants.STARTDATE + "="
//				+ startDate + "," + Constants.ENDDATE + "=" + endDate
//				+ "," + Constants.CASHFLOW + "=" + cashflows + '}';
	}

	public void createLoanProduct() {
		// TODO Auto-generated method stub

	}

	public ArrayList<Cashflow> getListOfCashflows(Product p) {
		// TODO Auto-generated method stub
		return null;
	}

}