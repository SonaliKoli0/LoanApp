package product;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import databaseConnector.ScheduleSQL;
import utils.Util;

public class LoanCashflow extends Cashflow {
	protected double principal;
	protected double interestValue;

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

	public static ArrayList<Cashflow> generateCashflows(LoanProduct p, String cashflowType) {
		if (cashflowType.equals("MONTHLY_EMI")) {
			return getMonthlyEMI(p);
		} else if (cashflowType.equals("MONTHLY_INTEREST")) {
			return getMonthlyInterest(p);
		}
		return null;
	}

	private static ArrayList<Cashflow> getMonthlyInterest(LoanProduct p) {
		// TODO Auto-generated method stub
		List<Schedule> ds = ScheduleSQL.readDisbursementSchedule(p.getLoanId());
		Map<LocalDate, Double> disbursements = new HashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		for (int i = 0; i < ds.size(); i++) {

			disbursements.put(LocalDate.parse(Util.formatDate(ds.get(i).getDate()) + "", formatter),
					ds.get(i).getAmount());

		}

		ArrayList<Cashflow> ls = new ArrayList<Cashflow>();
		// Calculate monthly loan parameters
		Date startDate = p.getStartDate();

		long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(LocalDate.parse(p.getStartDate() + "")),
				YearMonth.from(LocalDate.parse(p.getEndDate() + "")));

		int i = 0;
		double monthlyInterestRate = p.getRate() / 12.0; // Convert to monthly
		// Interest Rate
		double remainingPrincipal = 0;
		double totalInterestPaid = 0.0;
		double monthlyInterest = 0.0;
		// double totalPrincipalPaid = 0.0;

		DecimalFormat df = new DecimalFormat("#,##0.00");

		// Iterate through each month
		double disbursementAmount = 0;
		for (int month = 1; month <= monthsBetween; month++) {
			LocalDate currentMonth = Util.convertToLocalDateViaSqlDate(startDate).plusMonths(month - 1);

			if (disbursements.containsKey(currentMonth) && i == 0) {
				disbursementAmount = disbursements.get(currentMonth);
				remainingPrincipal += disbursementAmount;
				LoanCashflow lc2 = new LoanCashflow(p.getProductId(), Util.convertLocalDateToUtil(currentMonth),
						"PRINCIPAL", "OUT", disbursementAmount);
				ls.add(lc2);
			}

			monthlyInterest = remainingPrincipal * monthlyInterestRate / 100;
			// totalInterestPaid += monthlyInterest;

			currentMonth = currentMonth.plusMonths(1);
			LoanCashflow lc = new LoanCashflow(p.getProductId(), Util.convertLocalDateToUtil(currentMonth), "INTEREST",
					"IN", monthlyInterest);

			ls.add(lc);

			if (disbursements.containsKey(currentMonth) && i != 0) {
				disbursementAmount = disbursements.get(currentMonth);
				remainingPrincipal += disbursementAmount;
			}
			if (month == monthsBetween && remainingPrincipal > 0.01) {
				LoanCashflow lc2 = new LoanCashflow(p.getProductId(), Util.convertLocalDateToUtil(currentMonth),
						"CLEARENCE", "IN", remainingPrincipal);
				ls.add(lc2);
			}
			totalInterestPaid += monthlyInterest;
		}
		i++;
		System.out.println("\nTotal Interest Paid: " + df.format(totalInterestPaid));
		System.out.println("Total Principal Paid: " + df.format(remainingPrincipal) + "\n");
		System.out.println("PID" + "\t" + "FLOW" + "\t" + "DATE" + "\t\t" + "TYPE" + "\t\t" + "AMOUNT");
		return ls;
	}

	private static ArrayList<Cashflow> getMonthlyEMI(LoanProduct p) {
		// TODO Auto-generated method stub
		List<Schedule> ds = ScheduleSQL.readDisbursementSchedule(p.getLoanId());
		Map<LocalDate, Double> disbursements = new HashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		for (int i = 0; i < ds.size(); i++) {

			disbursements.put(LocalDate.parse(Util.formatDate(ds.get(i).getDate()) + "", formatter),
					ds.get(i).getAmount());

		}

		ArrayList<Cashflow> ls = new ArrayList<Cashflow>();
		// Calculate monthly loan parameters
		Date startDate = p.getStartDate();

		long monthsBetween = ChronoUnit.MONTHS.between(YearMonth.from(LocalDate.parse(p.getStartDate() + "")),
				YearMonth.from(LocalDate.parse(p.getEndDate() + "")));

		int i = 0;
		double monthlyInterestRate = p.getRate() / 12.0; // Convert to monthly
															// rate

		double remainingPrincipal = 0;
		double totalInterestPaid = 0.0;
		double totalPrincipalPaid = 0.0;

		DecimalFormat df = new DecimalFormat("#,##0.00");

		// Iterate through each month
		double disbursementAmount = 0;
		for (int month = 1; month <= monthsBetween; month++) {
			LocalDate currentMonth = Util.convertToLocalDateViaSqlDate(startDate).plusMonths(month - 1);

			if (disbursements.containsKey(currentMonth) && i == 0) {
				disbursementAmount = disbursements.get(currentMonth);
				remainingPrincipal += disbursementAmount;
				LoanCashflow lc2 = new LoanCashflow(p.getProductId(), Util.convertLocalDateToUtil(currentMonth),
						"PRINCIPAL", "OUT", disbursementAmount);
				ls.add(lc2);
			}

			double monthlyInterest = remainingPrincipal * monthlyInterestRate / 100;
			totalInterestPaid += monthlyInterest;

			double paymentAmount = (remainingPrincipal / (monthsBetween - month + 1)) + monthlyInterest;

			remainingPrincipal -= paymentAmount - monthlyInterest;
			totalPrincipalPaid += paymentAmount - monthlyInterest;
			currentMonth = currentMonth.plusMonths(1);
			LoanCashflow lc = new LoanCashflow(p.getProductId(), Util.convertLocalDateToUtil(currentMonth), "INTEREST",
					"IN", monthlyInterest);

			ls.add(lc);
			LoanCashflow lc1 = new LoanCashflow(p.getProductId(), Util.convertLocalDateToUtil(currentMonth),
					"PRINCIPAL", "IN", paymentAmount - monthlyInterest);

			ls.add(lc1);

			if (month == monthsBetween && remainingPrincipal > 0.01) {
				paymentAmount = remainingPrincipal;
				remainingPrincipal = 0.0;
				LoanCashflow lc2 = new LoanCashflow(p.getProductId(), Util.convertLocalDateToUtil(currentMonth),
						"PRINCIPAL", "OUT", paymentAmount);
				ls.add(lc2);
				// System.out.printf("Principal Out %s %s\n", currentMonth,
				// df.format(paymentAmount));
			}
			if (disbursements.containsKey(currentMonth) && i != 0) {
				disbursementAmount = disbursements.get(currentMonth);
				remainingPrincipal += disbursementAmount;
			}
		}
		i++;
		System.out.println("\nTotal Interest Paid: " + df.format(totalInterestPaid));
		System.out.println("Total Principal Paid: " + df.format(totalPrincipalPaid) + "\n");
		System.out.println("PID" + "\t" + "FLOW" + "\t" + "DATE" + "\t\t" + "TYPE" + "\t\t" + "AMOUNT");
		return ls;
	}

	public String toString() {
		return this.getProductId() + "\t" + this.getDirection() + "\t" + Util.formatDate(this.getDate()) + "\t"
				+ this.getType() + "\t" + this.getPrincipal();
	}

}
