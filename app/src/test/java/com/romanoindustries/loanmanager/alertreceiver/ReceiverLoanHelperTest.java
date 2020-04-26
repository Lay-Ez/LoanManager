package com.romanoindustries.loanmanager.alertreceiver;

import com.romanoindustries.loanmanager.datamodel.Loan;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;

public class ReceiverLoanHelperTest {

    @Test
    public void processLoansInterestRate() {

        Loan loan = new Loan();
        loan.setInterestRate(4.5);
        loan.setPeriodInDays(1);
        loan.setStartAmount(1000);
        loan.setCurrentAmount(1000);
        loan.setNextChargingDateInMs(Calendar.getInstance().getTimeInMillis() - 6*60*60*1000 /* six hours in the past */);

        loan = ReceiverLoanHelper.processLoansInterestRate(loan);
        Assert.assertEquals(1045, loan.getCurrentAmount());
        System.out.println(loan.getNextChargingDateInMs());

        loan.setInterestRate(2.05);
        loan.setPeriodInDays(7);
        loan.setStartAmount(1000);
        loan.setCurrentAmount(1000);
        loan.setNextChargingDateInMs(Calendar.getInstance().getTimeInMillis() - 15*12*60*60*1000 /* 15 days in the past*/);

        loan = ReceiverLoanHelper.processLoansInterestRate(loan);
        Assert.assertEquals(1040, loan.getCurrentAmount());
        System.out.println(loan.getNextChargingDateInMs());

        loan.setInterestRate(0.00);
        loan.setPeriodInDays(0);
        loan.setStartAmount(1000);
        loan.setCurrentAmount(1000);
        loan.setNextChargingDateInMs(0 /* no charging date */);

        loan = ReceiverLoanHelper.processLoansInterestRate(loan);
        Assert.assertEquals(1000, loan.getCurrentAmount());
        System.out.println(loan.getNextChargingDateInMs());
    }
}