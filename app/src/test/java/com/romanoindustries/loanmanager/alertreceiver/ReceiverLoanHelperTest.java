package com.romanoindustries.loanmanager.alertreceiver;

import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.viewloaninfo.LoanInfoHelper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class ReceiverLoanHelperTest {


    @Test
    public void loanEndTomorrowTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        Loan tomorrowsLoan = new Loan();
        tomorrowsLoan.setPaymentDateInMs(calendar.getTimeInMillis());
        Assert.assertTrue(ReceiverLoanHelper.loanEndsTomorrow(tomorrowsLoan));
    }

    @Test
    public void loanEndTomorrowPast() {
        Loan loan = new Loan();
        loan.setPaymentDateInMs(Calendar.getInstance().getTimeInMillis() - 15*24*60*60*1000);
        Assert.assertFalse(ReceiverLoanHelper.loanEndsTomorrow(loan));
    }


    @Test
    public void loanEndTomorrowToday() {
        Loan todaysLoan = new Loan();
        todaysLoan.setPaymentDateInMs(Calendar.getInstance().getTimeInMillis());
        Assert.assertFalse(ReceiverLoanHelper.loanEndsTomorrow(todaysLoan));
    }

    @Test
        public void processLoansInterestRateSixHoursPast() {

        Loan loan = new Loan();
        loan.setInterestRate(4.5);
        loan.setPeriodInDays(1);
        loan.setStartAmount(1000);
        loan.setCurrentAmount(1000);
        loan.setNextChargingDateInMs(Calendar.getInstance().getTimeInMillis() - 6*60*60*1000 /* six hours in the past */);

        loan = ReceiverLoanHelper.processLoansInterestRate(loan);
        Assert.assertEquals(1045, loan.getCurrentAmount());
        System.out.println(loan.getNextChargingDateInMs());

    }

    @Test
    public void processLoansInterestRateNoRate() {
        Loan loan = new Loan();

        loan.setInterestRate(0.00);
        loan.setPeriodInDays(0);
        loan.setStartAmount(1000);
        loan.setCurrentAmount(1000);
        loan.setNextChargingDateInMs(0 /* no charging date */);

        loan = ReceiverLoanHelper.processLoansInterestRate(loan);
        Assert.assertEquals(1000, loan.getCurrentAmount());
        System.out.println(loan.getNextChargingDateInMs());
    }

    @Test
    public void processLoansInterestRate15DaysPast() {
        Loan loan = new Loan();
        loan.setInterestRate(2.05);
        loan.setPeriodInDays(7);
        loan.setStartAmount(1000);
        loan.setCurrentAmount(1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() - 15*24*60*60*1000);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        loan.setNextChargingDateInMs(calendar.getTimeInMillis());

        loan = ReceiverLoanHelper.processLoansInterestRate(loan);
        System.out.println(loan.getNextChargingDateInMs());
        Assert.assertEquals(1061, loan.getCurrentAmount());
        loan.getChargeEvents().forEach(l -> {
            System.out.println("date = " + LoanInfoHelper.formatDate(l.getChargeEventDate().getTimeInMillis()));
            System.out.println("start amount = " + l.getStartAmount());
            System.out.println("end amount = " + l.getEndAmount());
        });
    }
}