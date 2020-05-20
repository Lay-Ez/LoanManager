package com.romanoindustries.loanmanager.newloan;

import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.ArrayList;
import java.util.Calendar;

public class NewLoanVmHelper {

    public NewLoanVmHelper() {
    }

    Loan composeLoanFromVm(NewLoanViewModel viewModel) {

        String name = viewModel.getName().getValue();
        String phone = viewModel.getPhone().getValue();
        int amount = viewModel.getAmount().getValue();
        String note = viewModel.getNote().getValue();
        int loanType = viewModel.getLoanType().getValue();
        long paymentDateInMs = viewModel.getPaymentDateInMs().getValue();
        boolean noEndDate = viewModel.getNoEndDate().getValue();
        boolean applyInterestRate = viewModel.getApplyInterestRate().getValue();
        int wholePercent = viewModel.getWholeInterestPercent().getValue();
        int decimalPercent = viewModel.getDecimalInterestPercent().getValue();
        int periodInDays = viewModel.getPeriodInDays().getValue();

        paymentDateInMs = normalizeTime(paymentDateInMs);

        Calendar calendar = Calendar.getInstance();
        long addedTime = calendar.getTimeInMillis();

        long countStartTime = normalizeTime(addedTime);

        long nextChargeDateInMs = calculateNextChargingTime(countStartTime, periodInDays);

        Loan loan = new Loan();
        loan.setType(loanType);
        loan.setDebtorName(name);
        loan.setSpecialNote(note);
        loan.setStartAmount(amount);
        loan.setCurrentAmount(amount);
        loan.setPhoneNumber(phone);
        loan.setStartDateInMs(addedTime);
        loan.setPaymentDateInMs(noEndDate ? 0 : paymentDateInMs);


        if (applyInterestRate) {
            double finalPercent = (double) wholePercent + ((double) decimalPercent / 100);
            loan.setInterestRate(finalPercent);
            loan.setPeriodInDays(periodInDays);
            loan.setNextChargingDateInMs(nextChargeDateInMs);
        } else {
            loan.setInterestRate(0);
            loan.setPeriodInDays(0);
            loan.setNextChargingDateInMs(0);
        }

        loan.setChargeEvents(new ArrayList<>());

        return loan;
    }

    public long calculateNextChargingTime(long startTime , int periodDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startTime);
        calendar.add(Calendar.DAY_OF_YEAR, periodDays);
        return calendar.getTimeInMillis();
    }

    public long normalizeTime(long rawStartTime) {
        Calendar calendarEndDate = Calendar.getInstance();
        calendarEndDate.setTimeInMillis(rawStartTime);
        calendarEndDate.set(Calendar.HOUR_OF_DAY, 12);
        calendarEndDate.set(Calendar.MINUTE, 0);
        calendarEndDate.set(Calendar.SECOND, 0);
        return  calendarEndDate.getTimeInMillis();
    }
}































