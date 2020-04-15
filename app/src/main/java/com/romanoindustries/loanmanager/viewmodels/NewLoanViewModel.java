package com.romanoindustries.loanmanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class NewLoanViewModel extends ViewModel {
    private MutableLiveData<String> name;
    private MutableLiveData<String> phone;
    private MutableLiveData<Integer> amount;
    private MutableLiveData<Long> paymentDateInMs;
    private MutableLiveData<Boolean> noEndDate;
    private MutableLiveData<Boolean> applyInterestRate;
    private MutableLiveData<Integer> wholeInterestPercent;
    private MutableLiveData<Integer> decimalInterestPercent;
    private MutableLiveData<Integer> periodInDays;

    private MutableLiveData<Integer> loanType; /* for later use*/

    public NewLoanViewModel() {
        name = new MutableLiveData<>("");
        phone = new MutableLiveData<>("");
        amount = new MutableLiveData<>(null);
        paymentDateInMs = new MutableLiveData<>(Calendar.getInstance().getTimeInMillis());
        noEndDate = new MutableLiveData<>(false);
        applyInterestRate = new MutableLiveData<>(false);
        wholeInterestPercent = new MutableLiveData<>(0);
        decimalInterestPercent = new MutableLiveData<>(0);
        periodInDays = new MutableLiveData<>(1);
    }

    public LiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public LiveData<String> getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.setValue(phone);
    }

    public LiveData<Integer> getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount.setValue(amount);
    }

    public LiveData<Long> getPaymentDateInMs() {
        return paymentDateInMs;
    }

    public void setPaymentDateInMs(long paymentDateInMs) {
        this.paymentDateInMs.setValue(paymentDateInMs);
    }

    public LiveData<Boolean> getNoEndDate() {
        return noEndDate;
    }

    public void setNoEndDate(boolean noEndDate) {
        this.noEndDate.setValue(noEndDate);
    }

    public LiveData<Boolean> getApplyInterestRate() {
        return applyInterestRate;
    }

    public void setApplyInterestRate(boolean applyInterestRate) {
        this.applyInterestRate.setValue(applyInterestRate);
    }

    public LiveData<Integer> getWholeInterestPercent() {
        return wholeInterestPercent;
    }

    public void setWholeInterestPercent(int wholeInterestPercent) {
        this.wholeInterestPercent.setValue(wholeInterestPercent);
    }

    public LiveData<Integer> getDecimalInterestPercent() {
        return decimalInterestPercent;
    }

    public void setDecimalInterestPercent(int decimalInterestPercent) {
        this.decimalInterestPercent.setValue(decimalInterestPercent);
    }

    public LiveData<Integer> getPeriodInDays() {
        return periodInDays;
    }

    public void setPeriodInDays(int periodInDays) {
        this.periodInDays.setValue(periodInDays);
    }
}
















