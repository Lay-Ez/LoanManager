package com.romanoindustries.loanmanager.viewloaninfo;

import com.romanoindustries.loanmanager.MyApp;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class LoanInfoHelper {

    static String composeShareText(Loan loan) {
        String name = loan.getDebtorName();
        String amount = NumberFormat.getNumberInstance(Locale.US).format(loan.getCurrentAmount());
        String startDate = formatDate(loan.getStartDateInMs());
        String endDate = formatDate(loan.getPaymentDateInMs());

        boolean loanHasEndDate = loan.getPaymentDateInMs() > 1;
        if (loan.getType() == Loan.TYPE_IN) {
            if (loanHasEndDate) {
                return MyApp.getContext().getString(R.string.incoming_loan_msg, name, amount, startDate, endDate);
            } else {
                return MyApp.getContext().getString(R.string.incoming_loan_msg_no_date, name, amount, startDate);
            }
        } else {
            if (loanHasEndDate) {
                return MyApp.getContext().getString(R.string.outgoing_loan_msg, name, amount, startDate, endDate);
            } else {
                return MyApp.getContext().getString(R.string.outgoing_loan_msg_no_date, name, amount, startDate);
            }
        }
    }

    public static String formatDate(long dateMs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMs);
        return DateFormat.getDateInstance().format(calendar.getTime());
    }

}
