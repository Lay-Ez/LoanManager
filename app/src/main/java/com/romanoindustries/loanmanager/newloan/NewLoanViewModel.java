package com.romanoindustries.loanmanager.newloan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class NewLoanViewModel extends ViewModel {

    private MutableLiveData<String> name;
    private MutableLiveData<String> phone;
    private MutableLiveData<Integer> amount;
    private MutableLiveData<String> note;
    private MutableLiveData<Integer> loanType;
    private MutableLiveData<Long> paymentDateInMs;
    private MutableLiveData<Boolean> noEndDate;
    private MutableLiveData<Boolean> applyInterestRate;
    private MutableLiveData<Integer> wholeInterestPercent;
    private MutableLiveData<Integer> decimalInterestPercent;
    private MutableLiveData<Integer> periodInDays;

    public NewLoanViewModel() {
        name = new MutableLiveData<>("");
        phone = new MutableLiveData<>("");
        amount = new MutableLiveData<>(null);
        note = new MutableLiveData<>("");
        loanType = new MutableLiveData<>();

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.add(Calendar.DAY_OF_YEAR, 1);
        paymentDateInMs = new MutableLiveData<>(currentCalendar.getTimeInMillis());
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

    LiveData<String> getPhone() {
        return phone;
    }

    void setPhone(String phone) {
        this.phone.setValue(phone);
    }

    public LiveData<Integer> getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount.setValue(amount);
    }

    LiveData<String> getNote() {
        return note;
    }

    void setNote(String note) {
        this.note.setValue(note);
    }

    LiveData<Integer> getLoanType() {
        return loanType;
    }

    void setLoanType(int loanType) {
        this.loanType.setValue(loanType);
    }

    public LiveData<Long> getPaymentDateInMs() {
        return paymentDateInMs;
    }

    void setPaymentDateInMs(long paymentDateInMs) {
        this.paymentDateInMs.setValue(paymentDateInMs);
    }

    LiveData<Boolean> getNoEndDate() {
        return noEndDate;
    }

    void setNoEndDate(boolean noEndDate) {
        this.noEndDate.setValue(noEndDate);
    }

    LiveData<Boolean> getApplyInterestRate() {
        return applyInterestRate;
    }

    void setApplyInterestRate(boolean applyInterestRate) {
        this.applyInterestRate.setValue(applyInterestRate);
    }

    LiveData<Integer> getWholeInterestPercent() {
        return wholeInterestPercent;
    }

    void setWholeInterestPercent(int wholeInterestPercent) {
        this.wholeInterestPercent.setValue(wholeInterestPercent);
    }

    LiveData<Integer> getDecimalInterestPercent() {
        return decimalInterestPercent;
    }

    void setDecimalInterestPercent(int decimalInterestPercent) {
        this.decimalInterestPercent.setValue(decimalInterestPercent);
    }

    public LiveData<Integer> getPeriodInDays() {
        return periodInDays;
    }

    public void setPeriodInDays(int periodInDays) {
        this.periodInDays.setValue(periodInDays);
    }
}
















