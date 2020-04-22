package com.romanoindustries.loanmanager.viewloaninfo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

public class LoanInfoActivity extends AppCompatActivity {
    private static final String TAG = "LoanInfoActivity";
    public static final String LOAN_ID_KEY = "loan_id_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_info);
        initViews();
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

    private void initViews() {

    }

    private void displayLoan(Loan loan) {

    }
}
