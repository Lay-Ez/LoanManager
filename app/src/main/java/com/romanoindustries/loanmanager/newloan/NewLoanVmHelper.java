package com.romanoindustries.loanmanager.newloan;

import android.util.Log;

import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.ArrayList;
import java.util.Calendar;

public class NewLoanVmHelper {
    private static final String TAG = "NewLoanVmHelper";

    public NewLoanVmHelper() {
    }

    public Loan composeLoanFromVm(NewLoanViewModel viewModel) {

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
        Log.d(TAG, "composeLoanFromVm: end date in ms= " + paymentDateInMs);

        Calendar calendar = Calendar.getInstance();
        long addedTime = calendar.getTimeInMillis();
        Log.d(TAG, "composeLoanFromVm: loan added at " + addedTime + " ms");

        long countStartTime = normalizeTime(addedTime);
        Log.d(TAG, "composeLoanFromVm: started counting down at " + countStartTime);

        long nextChargeDateInMs = calculateNextChargingTime(countStartTime, periodInDays);
        Log.d(TAG, "composeLoanFromVm: next interest charging time= " + nextChargeDateInMs);


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

        loan.setChargeEvents(new ArrayList<>()); /* implement later */

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

    public void loadLoanIntoVm(Loan loan, NewLoanViewModel viewModel) {
        Log.d(TAG, "loadLoanIntoVm: loan name=" + loan.getDebtorName());
    }
}































