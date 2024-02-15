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
    //returns start date 
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
	
	
	
	
	
    // calls the action user want to perform
	public void callAction(String Action, Product p) {
		switch (Action) {
		case "REPAY":
			if (p instanceof LoanProduct) {
				LoanProduct lp = (LoanProduct) p;
				Schedule sch = lp.getSchedule();

				lp.repayAmount(sch.getId(), sch.getAmount(), sch.getDate());
			}
			break;
		case Product.READ:
			{Product lp = (LoanProduct) p;
			lp = lp.readProduct(lp.getProductId());
			System.out.println(lp);}
			break;
		case Product.REMOVE:
			{Product lp1 = (LoanProduct) p;
			lp1.deleteProduct(lp1.getProductId());}
			break;
		case Product.CASHFLOW:
			LoanProduct lp1=(LoanProduct) p;
			Date d=Util.parseDate(AppStarter.inputs.get("Date"));
			lp1.getCashflow(lp1.getProductId(), d);
			break;
		case Product.AMEND:
			LoanProduct loanProduct = (LoanProduct) p;
			int id = p.getProductId();
			loanProduct = (LoanProduct) loanProduct.readProduct(id);
			loanProduct.updateProduct( id);
			loanProduct.setProductId(id);
			callAction(Product.READ, loanProduct);
			break;
		default:
			break;
		}

	}
    // gives product details
	protected Product readProduct(int productId) {
		
		return null;

	}
    //updates product using productId
	protected void updateProduct(int productId) {
	

	}
    //deletes product using productId
	protected void deleteProduct(int productId) {
		

	}
	//creates product
	protected void createProduct(){
		
		
	}
	//Method for generating cash flow
	protected void getCashflow(int productId, Date date) {
		// TODO Auto-generated method stub
		
	}
    // checks which action wants to perform and calls that action
	public static Product checkAction(HashMap<String, String> inputs) throws Exception {
		String action = inputs.get(Product.ACTION);
		String productType = inputs.get("productType");
		if (action.equals(Product.NEW)) {
			if (productType.equals("Loan")) {
				Date startDate = Util.parseDate(inputs.get("startDate"));
				Date endDate = Util.parseDate(inputs.get("endDate"));
				double rate = Double.parseDouble(inputs.get("rate"));
				double totalValue = Double.parseDouble(inputs.get("totalValue"));
				String schedule = inputs.get("schedule");
				String[] scheduleArray = schedule.split("\\.");
				List<Schedule> disbursementSchedule = new ArrayList<Schedule>();
				for (String s : scheduleArray) {
					Schedule sch = new Schedule();
					String[] oneSchedule = s.split("=");
					sch.setDate(Util.parseDate(oneSchedule[0]));
					sch.setAmount(Double.parseDouble(oneSchedule[1]));
					disbursementSchedule.add(sch);
				}

				LoanProduct lp = new LoanProduct(-1, -1, startDate, endDate, productType, totalValue, rate,
						disbursementSchedule, null);
				lp.createProduct();
				System.out.println(lp);
				return lp;
			}
		} else {
			if (productType.equals(Product.LOAN)) {
				LoanProduct lp = new LoanProduct();

				if (action.equals("REPAY")) {

					Schedule sch = new Schedule();
					sch.setId(Integer.parseInt(inputs.get("loanId")));
					sch.setAmount(Double.parseDouble(inputs.get("amount")));
					sch.setDate(Util.parseDate(inputs.get("date")));
					lp.setSchedule(sch);
				}else{
				lp.setProductId(Integer.parseInt(inputs.get("productId")));}

				lp.callAction(action, lp);
				return lp;
			}

		}
		return null;

	}
	
	//for printing product details
	@Override
	public String toString() {
		return "Product{" + "productId=" + productId + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", + cashflows=" + cashflows + '}';
	}

	


}
