package com.romanoindustries.loanmanager.loanrepo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.roomdb.LoanDao;
import com.romanoindustries.loanmanager.roomdb.LoanDatabase;

import java.util.List;

import static com.romanoindustries.loanmanager.loanrepo.DbExecutorAsyncTask.*;

public class LoanRepo {

    private LoanDao loanDao;
    private LiveData<List<Loan>> allLoans;
    private LiveData<List<Loan>> inLoans;
    private LiveData<List<Loan>> outLoans;
    private LiveData<List<Loan>> archivedLoans;

    public LoanRepo(Application application) {
        LoanDatabase loanDatabase = LoanDatabase.getInstance(application);
        loanDao = loanDatabase.loanDao();
        allLoans = loanDao.getAllLoans();
        inLoans = loanDao.getInLoans();
        outLoans = loanDao.getOutLoans();
        archivedLoans = loanDao.getArchivedLoans();
    }

    public void insert(Loan loan) {
        new DbExecutorAsyncTask(TASK_TYPE_INSERT, loanDao).execute(loan);
    }

    public void update(Loan loan) {
        new DbExecutorAsyncTask(TASK_TYPE_UPDATE, loanDao).execute(loan);
    }

    public  void delete(Loan loan) {
        new DbExecutorAsyncTask(TASK_TYPE_DELETE_SINGLE, loanDao).execute(loan);
    }

    public void deleteAllLoans() {
        new DbExecutorAsyncTask(TASK_TYPE_DELETE_ALL, loanDao).execute();
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

    public LiveData<List<Loan>> getAllLoans() {
        return allLoans;
    }
}












