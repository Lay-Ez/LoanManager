package com.romanoindustries.loanmanager.newloan;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.loanrepo.LoanRepo;

import java.util.Calendar;
import java.util.List;

public class NewLoanViewModel extends AndroidViewModel {

    private LoanRepo loanRepo;
    private LiveData<List<Loan>> allLoans;

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

    public NewLoanViewModel(@NonNull Application application) {
        super(application);
        loanRepo = new LoanRepo(application);
        allLoans = loanRepo.getAllLoans();

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

    public LiveData<List<Loan>> getAllLoans() {
        return allLoans;
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

    public LiveData<String> getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note.setValue(note);
    }

    public LiveData<Integer> getLoanType() {
        return loanType;
    }

    public void setLoanType(int loanType) {
        this.loanType.setValue(loanType);
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
















