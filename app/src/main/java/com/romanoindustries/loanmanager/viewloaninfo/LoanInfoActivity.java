package com.romanoindustries.loanmanager.viewloaninfo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.romanoindustries.loanmanager.R;

public class LoanInfoActivity extends AppCompatActivity {
    public static final String LOAN_ID_KEY = "loan_id_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_info);
    }
}
