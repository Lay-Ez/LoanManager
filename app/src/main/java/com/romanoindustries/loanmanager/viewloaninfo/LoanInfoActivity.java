package com.romanoindustries.loanmanager.viewloaninfo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

public class LoanInfoActivity extends AppCompatActivity {
    private static final String TAG = "LoanInfoActivity";
    public static final String LOAN_ID_KEY = "loan_id_key";

    private TextView loanTypeTv;
    private TextView nameTv;
    private TextView phoneTv;
    private TextView startDateTv;
    private TextView endDateTv;
    private TextView nextAccrualDateTv;
    private TextView startAmountTv;
    private TextView currentAmountTv;
    private TextView interestRateTv;
    private TextView interestPeriodTv;
    private TextView idTv;
    private TextView noteTv;

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
        loanTypeTv = findViewById(R.id.info_loan_type_tv);
        nameTv = findViewById(R.id.info_name_tv);
        phoneTv = findViewById(R.id.info_phone_tv);
        startDateTv = findViewById(R.id.info_start_date_tv);
        endDateTv = findViewById(R.id.info_end_date_tv);
        nextAccrualDateTv = findViewById(R.id.info_next_accrual_tv);
        startAmountTv = findViewById(R.id.info_start_amount_tv);
        currentAmountTv = findViewById(R.id.info_current_amount_tv);
        interestRateTv = findViewById(R.id.info_interest_rate_tv);
        interestPeriodTv = findViewById(R.id.info_period_tv);
        idTv = findViewById(R.id.info_id_tv);
        noteTv = findViewById(R.id.info_note_tv);
    }

    private void displayLoan(Loan loan) {
        String name = loan.getDebtorName();
        if (!name.isEmpty()) {
            nameTv.setText(name);
        }
    }
}
