package product;
 
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import databaseConnector.ScheduleSQL;
import utils.Util;
 
public class LoanCashflow extends Cashflow {
	protected double principal;
	protected double interestValue;
 
	protected static double remainingPrincipal = 0, remainingPrincipal1, totalInterestPaid = 0, totalPrincipalPaid = 0,
			paymentAmount, monthlyInterestRate;
	static int disbursementIndex = 0;
	static Date cashflowDate, lastRepayment = null;
	static List<Schedule> ds = null;
	static ArrayList<Cashflow> ls = null;
	int productId = 0;
	boolean firstDisbursement = false;
 
	public LoanCashflow(int id, Date date, String type, String direction, Double amount) {
		super(id, date, type, direction);
		this.principal = amount;
 
	}
 
	public LoanCashflow() {
 
	}
 
	public void setPrincipal(double principal) {
 
	}
 
	public void setInterestValue(double interesValue) {
 
	}
 
	public double getPrincipal() {
 
		return principal;
 
	}
 
	public double getInterestValue() {
 
		return interestValue;
	}
 
	public double getLastPayOutDay(int loanId) {
		return 12.44;
	}
 
	public double getCurrentPrincipal(double currDisbursedAmount, double currRepayedAmount) {
		return 12.44;
	}
 
	public double getInterestValue(double interestRate, double amount) {
		return 12.44;
	}
 
	public double getMonthlyInterestRate(double rate) {
		return 12.44;
	}
 
 
	@Override
	public ArrayList<Cashflow> generateCashflows(Product p) {
 
		LoanProduct lp = (LoanProduct) p;
        String option=lp.getPaymentOption();
		ds = ScheduleSQL.readDisbursementSchedule(lp.getLoanId());
		ls = new ArrayList<Cashflow>();
		productId = p.getProductId();
		//System.out.println(ds.get(0).getDate()+"  "+(p.getStartDate()+""));
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String mydate = formatter.format(ds.get(0).getDate());
		long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(LocalDate.parse(mydate)),
				YearMonth.from(LocalDate.parse(p.getEndDate() + "")));
 
		monthlyInterestRate = lp.getRate() / 12.0; // Convert to monthly
 
		lastRepayment = ds.get(0).getDate();
		DecimalFormat df = new DecimalFormat("#,##0.00");

 
		// Iterate through each month
		for (int month = 0; month < monthsBetween; month++) {
			cashflowDate = Util.addMonths(ds.get(0).getDate(), month);
			remainingPrincipal1 = 0;
			interestValue = 0;
 
			handleDisbursement(p, ls);
			if (option instanceof String && option.equals("AT_MATURITY")) {
				handleRepaymentsAtMaturity(month, monthsBetween);
			} else {
				handleRepayments(month, monthsBetween);
				checkRemainingPrincipal(month, monthsBetween);
			}
 
		}
 
		// Output summary
		System.out.println("\nTotal Interest Paid: " + df.format(totalInterestPaid));
		System.out.println("Total Principal Paid: " + df.format(totalPrincipalPaid) + "\n");
		System.out.println("PID" + "\t" + "FLOW" + "\t" + "DATE" + "\t\t" + "TYPE" + "\t\t" + "AMOUNT");
		disbursementIndex=0;
		remainingPrincipal=0;
		return ls;
	}
 
	private void handleRepaymentsAtMaturity(int month, long monthsBetween) {
		double dailyInterestRate = monthlyInterestRate
				/ Util.getNumberOfDaysInMonth(cashflowDate);
		double monthlyInterest = (remainingPrincipal) * dailyInterestRate*Util.getNumberOfDaysInMonth(cashflowDate) / 100;
		remainingPrincipal += remainingPrincipal1;
		totalInterestPaid += (monthlyInterest + interestValue);
		totalPrincipalPaid = remainingPrincipal;
 
		lastRepayment = Util.addMonths(cashflowDate,1);
		ls.add(new LoanCashflow(productId, Util.addMonths(cashflowDate,1), "INTEREST", "IN", monthlyInterest + interestValue));
		if (monthsBetween-1 == month) {
			ls.add(new LoanCashflow(productId, Util.addMonths(cashflowDate,1), "PRINCIPAL", "IN", totalPrincipalPaid));
		}
	}
 
	// Handle disbursements within the current month
	public void handleDisbursement(Product p, List<Cashflow> ls) {
		interestValue = 0;
		remainingPrincipal1 = 0;
		while (disbursementIndex < ds.size()
&& ds.get(disbursementIndex).getDate().before(Util.addMonths(cashflowDate, 1))) {
			double remainingPrincipal2 = ds.get(disbursementIndex).getAmount();
			double dailyInterestRate = monthlyInterestRate
					/ Util.getNumberOfDaysInMonth(ds.get(disbursementIndex).getDate());
			double interest = remainingPrincipal2 * (dailyInterestRate) * Math
					.abs(Util.getDifferenceDays(ds.get(disbursementIndex).getDate(), Util.addMonths(lastRepayment, 1)))
					/ 100;
			LoanCashflow lc2 = new LoanCashflow(p.getProductId(), ds.get(disbursementIndex).getDate(), "PRINCIPAL",
					"OUT", ds.get(disbursementIndex).getAmount());
			ls.add(lc2);
			remainingPrincipal1 += remainingPrincipal2;
			interestValue += interest;
			disbursementIndex++;
		}
	}
 
	// Handle repayments within the current month
	public void handleRepayments(int month, long monthsBetween) {
		if(month!=monthsBetween){
		if (remainingPrincipal >= 0) {
			if (remainingPrincipal == 0) {
				remainingPrincipal = remainingPrincipal1;
				interestValue = 0;
				remainingPrincipal1 = 0;
			}
 
			double monthlyInterest = (remainingPrincipal) * monthlyInterestRate / 100;
			remainingPrincipal += remainingPrincipal1;
 
			paymentAmount = ((remainingPrincipal) / (monthsBetween - month )) + monthlyInterest + interestValue;
			remainingPrincipal -= paymentAmount - (monthlyInterest + interestValue);
			totalInterestPaid += (monthlyInterest + interestValue);
			totalPrincipalPaid += paymentAmount - (monthlyInterest + interestValue);
			lastRepayment =Util.addMonths(cashflowDate,1);
			ls.add(new LoanCashflow(productId,lastRepayment, "INTEREST", "IN", monthlyInterest + interestValue));
			ls.add(new LoanCashflow(productId,lastRepayment, "PRINCIPAL", "IN",
					paymentAmount - (monthlyInterest + interestValue)));
		}}
	}
 
	public void checkRemainingPrincipal(int month, long monthsBetween) {
		if (month == monthsBetween && remainingPrincipal > 0.01) {
			double dailyInterestRate = monthlyInterestRate / Util.getNumberOfDaysInMonth(cashflowDate);
			double interest = remainingPrincipal1 * (dailyInterestRate)
					* Util.getDifferenceDays(lastRepayment, cashflowDate);
			paymentAmount = remainingPrincipal;
			remainingPrincipal = 0.0;
			LoanCashflow lc1 = new LoanCashflow(productId, cashflowDate, "INTEREST", "IN", interest);
			ls.add(lc1);
			LoanCashflow lc2 = new LoanCashflow(productId, cashflowDate, "PRINCIPAL", "IN", paymentAmount);
			ls.add(lc2);
		}
	}
	
	 @Override
	    public boolean equals(Object obj) {
	        if (this == obj)
	            return true;
	        if (obj == null || getClass() != obj.getClass())
	            return false;
	        LoanCashflow other = (LoanCashflow) obj;
	        return productId == other.productId && 
	               Objects.equals(date, other.date) && 
	               Objects.equals(type, other.type) && 
	               Objects.equals(direction, other.direction) && 
	               Double.compare(principal, other.principal) == 0;
	    }
	 
	public String toString() {
		return this.getProductId() + "\t" + this.getDirection() + "\t" + Util.formatDate(this.getDate()) + "\t"
				+ this.getType() + "\t" + this.getPrincipal();
	}
 
}