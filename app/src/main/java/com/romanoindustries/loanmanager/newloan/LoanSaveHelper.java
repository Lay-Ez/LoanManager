package com.romanoindustries.loanmanager.newloan;

import android.util.Log;

import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.viewmodels.NewLoanViewModel;

import java.util.Calendar;

public class LoanSaveHelper {
    private static final String TAG = "LoanSaveHelper";

    private Loan loan;

    public LoanSaveHelper(NewLoanViewModel viewModel) {
        composeLoanFromVm(viewModel);
    }

    public void composeLoanFromVm(NewLoanViewModel viewModel) {

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
        loan.setDebtorName(name);
        loan.setSpecialNote(note);
        loan.setType(loanType);
        loan.setStartAmount(amount);
        loan.setCurrentAmount(amount);
        loan.setPhoneNumber(phone);
        loan.setStartDateInMs(addedTime);
        loan.setPaymentDateInMs(noEndDate ? 0 : paymentDateInMs);
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
