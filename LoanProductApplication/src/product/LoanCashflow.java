package product;


import java.util.Date;
import java.util.List;
import databaseConnector.ScheduleSQL;
import utils.Util;

public class LoanCashflow extends Cashflow {
	protected double principal;
	protected double interestValue;

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

	public static void generateCashflows(int loanId, Date date,double rate) {

		List<Schedule> ds = ScheduleSQL.readDisbursementSchedule(loanId);
		List<Schedule> rs = ScheduleSQL.readRepaymentSchedule(loanId);
		
		Double currentPrincipal = 0.0;
		double interestRateMonthly = (rate  / (double) 12);

		int i = 0, j = 0;
		Date lastDisbursementDate = new Date();
		Schedule s = ds.get(j);
		Schedule s1 = rs.get(i);
		for (i = 0, j = 0; i < rs.size() && j < ds.size();) {
			s = ds.get(j);
			s1 = rs.get(i);

			if (s1.getDate().after(s.getDate()) && s.getDate().before(date)) {
				System.out.println("PRINCIPAL" + "\tOUT" + "\t" + Util.formatDate(s.getDate()) + "\t" + s.getAmount());
				
				currentPrincipal += s.getAmount();
				lastDisbursementDate = s.getDate();
				j++;
			} else if (s1.getDate().before(s.getDate()) && s1.getDate().after(date)) {
				String[] splitDate = Util.formatDate(s.getDate()).toString().split("/");
				double interestRateDaywise = interestRateMonthly
						/ Util.getNumberOfDaysInMonth(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]));
				Long numberOfDays = Util.getDifferenceDays(lastDisbursementDate, s1.getDate());
				Double interestValue = (Double) (currentPrincipal * interestRateDaywise * numberOfDays) / 100;
				Double principal = (s1.getAmount() - interestValue);
				System.out.println("INTEREST\t" + "IN\t" + Util.formatDate(s1.getDate()) + "\t" + interestValue);
				System.out.println("PRINCIPAL\t" + "IN\t" + Util.formatDate(s1.getDate()) + "\t" + principal);
				currentPrincipal -= principal;
				System.out.println(Util.getDifferenceDays(lastDisbursementDate, s1.getDate()));
				i++;
			} else {
				String[] splitDate = Util.formatDate(s.getDate()).toString().split("/");
				double interestRateDaywise = interestRateMonthly
						/ Util.getNumberOfDaysInMonth(Integer.parseInt(splitDate[0]), Integer.parseInt(splitDate[1]));
				Long numberOfDays = Util.getDifferenceDays(lastDisbursementDate, s1.getDate());
				Double interestValue = (Double) (currentPrincipal * interestRateDaywise * numberOfDays) / 100;
				Double principal = (s1.getAmount() - interestValue);
				System.out.println("INTEREST\t" + "IN\t" + Util.formatDate(s1.getDate()) + "\t" + interestValue);
				System.out.println("PRINCIPAL\t" + "IN\t" + Util.formatDate(s1.getDate()) + "\t" + principal);
				currentPrincipal -= principal;
				i++;
				System.out.println("PRINCIPAL\t" + "OUT\t" + Util.formatDate(s.getDate()) + "\t" + s.getAmount());
				currentPrincipal += s.getAmount();
				lastDisbursementDate = s.getDate();
				j++;
			}
		}
		while (j < ds.size()) {
			System.out.println("PRINCIPAL\t" + "OUT\t" + Util.formatDate(s.getDate()) + "\t" + s.getAmount());
			currentPrincipal += s.getAmount();
			lastDisbursementDate = s.getDate();
			j++;
		}
		while (i < rs.size()) {
			Long numberOfDays = Util.getDifferenceDays(lastDisbursementDate, s1.getDate());
			double interestRateDaywise = interestRateMonthly / numberOfDays;
			Double interestValue = (Double) (currentPrincipal * interestRateDaywise) / 100;
			Double principal = (s1.getAmount() - interestValue);
			System.out.println("INTEREST\t" + "IN\t" + Util.formatDate(s1.getDate()) + "\t" + interestValue);
			System.out.println("PRINCIPAL\t" + "IN\t" + Util.formatDate(s1.getDate()) + "\t" + principal);
			currentPrincipal -= principal;

			i++;
		}
		

	}

}
