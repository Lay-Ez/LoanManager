package com.romanoindustries.loanmanager.alertreceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.romanoindustries.loanmanager.MyApp;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.loanrepo.LoanRepo;
import com.romanoindustries.loanmanager.notifications.NotificationHelper;

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
                loan = ReceiverLoanHelper.processLoansInterestRate(loan);
                repo.update(loan);
            }
            Context base = MyApp.getContext();
            NotificationHelper helper = new NotificationHelper(base);
            NotificationManager manager = (NotificationManager) base.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, helper
                    .getMainChannelNotification("Hello from receiver", "Msg body here")
                    .build());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pendingResult.finish();
        }
    }
}
