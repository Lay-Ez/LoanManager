package com.romanoindustries.loanmanager.alertreceiver;

import android.app.NotificationManager;
import android.content.Context;

import com.romanoindustries.loanmanager.MyApp;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.InterestAccrualEvent;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.notifications.NotificationHelper;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReceiverLoanHelper {

    public static Loan processLoansInterestRate(Loan loan) {
        double interestRate = loan.getInterestRate();
        if (interestRate == 0) {
            return loan;
        }
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long chargingDateInMs = loan.getNextChargingDateInMs();

        while (chargingDateInMs < currentTime) {

            int startAmount = loan.getCurrentAmount();
            double resultingAmount = startAmount * (1+interestRate/100);
            loan.setCurrentAmount((int) resultingAmount);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(chargingDateInMs);
            InterestAccrualEvent accrualEvent = new InterestAccrualEvent(calendar, startAmount, (int) resultingAmount);
            loan.getChargeEvents().add(accrualEvent);

            calendar.add(Calendar.DAY_OF_YEAR, loan.getPeriodInDays());
            calendar.set(Calendar.HOUR_OF_DAY, 12);
            chargingDateInMs = calendar.getTimeInMillis();
        }
        loan.setNextChargingDateInMs(chargingDateInMs);
        return loan;
    }

    public static void notifyLoanEndsTomorrow(Loan loan) {
        Context context = MyApp.getContext();
        String debtorName = loan.getDebtorName();
        String amount = NumberFormat.getNumberInstance(Locale.US).format(loan.getCurrentAmount());

        String title;
        String msgBody;

        if (loan.getType() == Loan.TYPE_IN) {
            title = context.getString(R.string.in_loan_notification_title);
            msgBody = context.getString(R.string.in_loan_notification_body, debtorName, amount);
        } else {
            title = context.getString(R.string.out_loan_notification_title);
            msgBody = context.getString(R.string.out_loan_notification_body, debtorName, amount);
        }

        NotificationHelper helper = new NotificationHelper(context);
        NotificationManager manager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(loan.getId(), helper
                .getMainChannelNotification(title, msgBody)
                .build());
    }

    public static boolean loanEndsTomorrow(Loan loan) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        int tomorrow = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(loan.getPaymentDateInMs());
        int loanEndDay = calendar.get(Calendar.DAY_OF_YEAR);
        return loanEndDay == tomorrow;
    }
}
