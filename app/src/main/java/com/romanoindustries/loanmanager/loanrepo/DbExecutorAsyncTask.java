package com.romanoindustries.loanmanager.loanrepo;

import android.os.AsyncTask;
import android.util.Log;

import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.roomdb.LoanDao;

public class DbExecutorAsyncTask extends AsyncTask<Loan, Void, Void> {
    private static final String TAG = "DbExecutorAsyncTask";
    public static final int TASK_TYPE_INSERT = 1;
    public static final int TASK_TYPE_UPDATE = 2;
    public static final int TASK_TYPE_DELETE_SINGLE = 3;
    public static final int TASK_TYPE_DELETE_ALL = 4;

    private int taskType;
    private LoanDao loanDao;

    public DbExecutorAsyncTask(int taskType, LoanDao loanDao) {
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
                Log.d(TAG, "doInBackground: unknown task type " + taskType);
                break;

        }
        return null;
    }
}
