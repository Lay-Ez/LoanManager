package com.romanoindustries.loanmanager.alertreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.romanoindustries.loanmanager.MyApp;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.loanrepo.LoanRepo;
import com.romanoindustries.loanmanager.notifications.NotificationPreferencesHelper;

import java.util.List;

public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "AlertReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        LoanRepo loanRepo = new LoanRepo(MyApp.getApplication());
        RateAccrualTask task = new RateAccrualTask(loanRepo, pendingResult, notificationsEnabled());
        task.execute();
    }

    private static class RateAccrualTask extends AsyncTask<Void, Void, Void> {

        private LoanRepo repo;
        private PendingResult pendingResult;
        private boolean shouldNotify;

        public RateAccrualTask(LoanRepo repo, PendingResult pendingResult, boolean shouldNotify) {
            this.repo = repo;
            this.pendingResult = pendingResult;
            this.shouldNotify = shouldNotify;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<Loan> activeLoans = repo.getAllActiveLoans();
            for (Loan loan : activeLoans) {
                try {
                    ReceiverLoanHelper.processLoansInterestRate(loan);
                    if (shouldNotify && ReceiverLoanHelper.loanEndsTomorrow(loan)) {
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

    private boolean notificationsEnabled() {
        Context context = MyApp.getContext();
        int notificationMode = NotificationPreferencesHelper.getNotificationMode(context);
        return notificationMode == NotificationPreferencesHelper.NOTIFICATION_MODE_SHOW_ALL;
    }
}
