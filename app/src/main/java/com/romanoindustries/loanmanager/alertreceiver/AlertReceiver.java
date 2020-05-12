package com.romanoindustries.loanmanager.alertreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.romanoindustries.loanmanager.MyApp;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.loanrepo.LoanRepo;

import java.util.List;

public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "AlertReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        LoanRepo loanRepo = new LoanRepo(MyApp.getApplication());
        RateAccrualTask task = new RateAccrualTask(loanRepo, pendingResult);
        task.execute();
    }

    private static class RateAccrualTask extends AsyncTask<Void, Void, Void> {

        private LoanRepo repo;
        private PendingResult pendingResult;

        public RateAccrualTask(LoanRepo repo, PendingResult pendingResult) {
            this.repo = repo;
            this.pendingResult = pendingResult;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<Loan> activeLoans = repo.getAllActiveLoans();
            for (Loan loan : activeLoans) {
                try {
                    ReceiverLoanHelper.processLoansInterestRate(loan);
                    if (ReceiverLoanHelper.loanEndsTomorrow(loan)) {
                        ReceiverLoanHelper.notifyLoanEndsTomorrow(loan);
                    }
                    repo.update(loan);
                } catch (Exception e) {
                    Log.e(TAG, "exception processing loans in background: ", e);
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pendingResult.finish();
        }
    }
}
