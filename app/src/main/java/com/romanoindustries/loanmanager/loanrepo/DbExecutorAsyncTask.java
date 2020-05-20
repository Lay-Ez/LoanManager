package com.romanoindustries.loanmanager.loanrepo;

import android.os.AsyncTask;

import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.roomdb.LoanDao;

public class DbExecutorAsyncTask extends AsyncTask<Loan, Void, Void> {
    static final int TASK_TYPE_INSERT = 1;
    static final int TASK_TYPE_UPDATE = 2;
    static final int TASK_TYPE_DELETE_SINGLE = 3;
    static final int TASK_TYPE_DELETE_ALL = 4;

    private int taskType;
    private LoanDao loanDao;

    DbExecutorAsyncTask(int taskType, LoanDao loanDao) {
        this.taskType = taskType;
        this.loanDao = loanDao;
    }

    @Override
    protected Void doInBackground(Loan... loans) {
        Loan loan = loans[0];

        switch (taskType) {

            case TASK_TYPE_INSERT:
                loanDao.insert(loan);
                break;

            case TASK_TYPE_UPDATE:
                loanDao.update(loan);
                break;

            case TASK_TYPE_DELETE_SINGLE:
                loanDao.delete(loan);
                break;

            case TASK_TYPE_DELETE_ALL:
                loanDao.deleteAllLoans();
                break;
                
            default:
                break;

        }
        return null;
    }
}
