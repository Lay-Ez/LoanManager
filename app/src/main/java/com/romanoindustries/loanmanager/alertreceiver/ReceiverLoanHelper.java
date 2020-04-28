package com.romanoindustries.loanmanager.alertreceiver;

import com.romanoindustries.loanmanager.datamodel.InterestAccrualEvent;
import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.Calendar;

public class ReceiverLoanHelper {

    public static Loan processLoansInterestRate(Loan loan) {
        double interestRate = loan.getInterestRate();
        if (interestRate == 0) {
            return loan;
        }
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long chargingDateInMs = loan.getNextChargingDateInMs();

        while (chargingDateInMs < currentTime) {

            int startAmount = loan.getCurrentAmount();
            double resultingAmount = startAmount * (1+interestRate/100);
            loan.setCurrentAmount((int) resultingAmount);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(chargingDateInMs);
            InterestAccrualEvent accrualEvent = new InterestAccrualEvent(calendar, startAmount, (int) resultingAmount);
            loan.getChargeEvents().add(accrualEvent);

            calendar.add(Calendar.DAY_OF_YEAR, loan.getPeriodInDays());
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            chargingDateInMs = calendar.getTimeInMillis();
        }
        loan.setNextChargingDateInMs(chargingDateInMs);
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
