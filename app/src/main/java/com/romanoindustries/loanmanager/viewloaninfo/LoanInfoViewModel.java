package com.romanoindustries.loanmanager.viewloaninfo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.loanrepo.LoanRepo;

import java.util.List;

public class LoanInfoViewModel extends AndroidViewModel {

    private LiveData<List<Loan>> allLoans;

    public LoanInfoViewModel(@NonNull Application application) {
        super(application);
        LoanRepo loanRepo = new LoanRepo(application);
        allLoans = loanRepo.getAllLoans();
    }

    public LiveData<List<Loan>> getAllLoans() {
        return allLoans;
    }
}
