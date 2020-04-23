package com.romanoindustries.loanmanager.viewloaninfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.databinding.ActivityLoanInfoBinding;
import com.romanoindustries.loanmanager.datamodel.Loan;

import java.text.DateFormat;
import java.util.Calendar;

public class LoanInfoActivity extends AppCompatActivity {
    private static final String TAG = "LoanInfoActivity";
    public static final String LOAN_ID_KEY = "loan_id_key";
    private ActivityLoanInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanInfoBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        Intent intent = getIntent();
        int loanId = intent.getIntExtra(LOAN_ID_KEY, -1);
        if (loanId == -1) {
            Toast.makeText(this, R.string.info_activity_error_message, Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        LoanInfoViewModel viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(LoanInfoViewModel.class);
        viewModel.getAllLoans().observe(this, loans -> {
            if (loans != null) {
                for (Loan loan : loans) {
                    if (loan.getId() == loanId) {
                        displayLoan(loan);
                        break;
                    }
                }
            }
        });
    }

    private void displayLoan(Loan loan) {
        String name = loan.getDebtorName();
        if (!name.isEmpty()) {
            binding.infoNameTv.setText(name);
        }

        String phone = loan.getPhoneNumber();
        Log.d(TAG, "displayLoan: phone=" + phone);

        if (!phone.equals("")) {
            binding.infoPhoneTv.setText(phone);
        } else {
            binding.infoPhoneTv.setText(R.string.not_specified_string);
        }

        binding.infoStartDateTv.setText(formatDate(loan.getStartDateInMs()));

        if (loan.getPaymentDateInMs() != 0) {
            binding.infoEndDateTv.setText(formatDate(loan.getPaymentDateInMs()));
        } else {
            binding.infoEndDateTv.setText(R.string.not_specified_string);
        }

        if (loan.getNextChargingDateInMs() != 0) {
            binding.infoNextAccrualTv.setText(formatDate(loan.getNextChargingDateInMs()));
        } else {
            binding.infoNextAccrualTv.setText(R.string.not_specified_string);
        }

        int startAmount = loan.getStartAmount();
        binding.infoStartAmountTv.setText(String.valueOf(startAmount));

        int currentAmount = loan.getCurrentAmount();
        binding.infoCurrentAmountTv.setText(String.valueOf(currentAmount));

        double interestRate = loan.getInterestRate();
        if (interestRate > 0.0) {
            String rateStr = interestRate + "%";
            binding.infoInterestRateTv.setText(rateStr);
        } else {
            binding.infoInterestRateTv.setText(R.string.not_specified_string);
        }

        int periodInDays = loan.getPeriodInDays();
        if (periodInDays != 0) {
            binding.infoPeriodTv.setText(String.valueOf(periodInDays));
        } else {
            binding.infoPeriodTv.setText(R.string.not_specified_string);
        }

        binding.infoIdTv.setText(String.valueOf(loan.getId()));

        String note = loan.getSpecialNote();
        if (!note.equals("")) {
            binding.infoNoteTv.setText(note);
        } else {
            binding.infoNoteTv.setText(R.string.not_specified_string);
        }

        String loanType = getString(R.string.not_specified_string);
        switch (loan.getType()) {

            case Loan.TYPE_IN:
                loanType = getString(R.string.loan_type_incoming);
                break;

            case Loan.TYPE_OUT:
                loanType = getString(R.string.loan_type_outgoing);
                break;

            case Loan.TYPE_ARCHIVED_IN:
                loanType = getString(R.string.loan_type_archived_in);
                break;

            case Loan.TYPE_ARCHIVED_OUT:
                loanType = getString(R.string.loan_type_archived_out);
                break;
        }
        binding.infoLoanTypeTv.setText(loanType);
    }

    private String formatDate(long dateMs) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMs);
        return DateFormat.getDateInstance().format(calendar.getTime());
    }
}

















