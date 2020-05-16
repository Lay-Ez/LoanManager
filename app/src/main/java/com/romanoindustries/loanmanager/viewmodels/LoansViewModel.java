package com.romanoindustries.loanmanager.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.loanrepo.LoanRepo;

import java.util.List;

public class LoansViewModel extends AndroidViewModel {

    private LoanRepo loanRepo;
    private LiveData<List<Loan>> allLoans;
    private LiveData<List<Loan>> inLoans;
    private LiveData<List<Loan>> outLoans;
    private LiveData<List<Loan>> archivedLoans;

    public LoansViewModel(@NonNull Application application) {
        super(application);
        Log.d("LoansViewModel", "LoansViewModel: create");
        loanRepo = new LoanRepo(application);
        allLoans = loanRepo.getAllLoans();
        inLoans = loanRepo.getInLoans();
        outLoans = loanRepo.getOutLoans();
        archivedLoans = loanRepo.getArchivedLoans();
    }

    public void insert(Loan loan) {
        loanRepo.insert(loan);
    }

    public void update(Loan loan) {
        loanRepo.update(loan);
    }

    public void delete(Loan loan) {
        loanRepo.delete(loan);
    }

    public void deleteAllLoans() {
        loanRepo.deleteAllLoans();
    }

    public LiveData<List<Loan>> getInLoans() {
        return inLoans;
    }

    public LiveData<List<Loan>> getOutLoans() {
        return outLoans;
    }

    public LiveData<List<Loan>> getArchivedLoans() {
        return archivedLoans;
    }
}