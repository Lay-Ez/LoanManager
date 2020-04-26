package com.romanoindustries.loanmanager.alertreceiver;

import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.Calendar;

public class ReceiverLoanHelper {

    public static Loan processLoansInterestRate(Loan loan) {
        double interestRate = loan.getInterestRate();
        if (interestRate == 0) {
            return loan;
        }
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long nextChargingDateInMs = loan.getNextChargingDateInMs();

        while (nextChargingDateInMs < currentTime) {
            int currentAmount = loan.getCurrentAmount();
            double resultingAmount = currentAmount * (1+interestRate/100);
            loan.setCurrentAmount((int) resultingAmount);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(nextChargingDateInMs);
            calendar.add(Calendar.DAY_OF_YEAR, loan.getPeriodInDays());
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            nextChargingDateInMs = calendar.getTimeInMillis();
        }
        loan.setNextChargingDateInMs(nextChargingDateInMs);
        return loan;
    }

    public static boolean loanEndsTomorrow(Loan loan) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        int tomorrow = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(loan.getPaymentDateInMs());
        int loanEndDay = calendar.get(Calendar.DAY_OF_YEAR);
        return loanEndDay == tomorrow;
    }

}
