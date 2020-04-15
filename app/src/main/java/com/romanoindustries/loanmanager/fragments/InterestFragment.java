package com.romanoindustries.loanmanager.fragments;


import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.romanoindustries.loanmanager.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterestFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "InterestFragment";

    private TextInputLayout percentageInputLayout;
    private TextInputEditText percentageEditText;
    private Spinner periodSpinner;

    /* track current loan fields status*/
    private double interestPercent = 0.0;
    private int loanPeriodInDays;


    public InterestFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interest, container, false);
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        percentageInputLayout = view.findViewById(R.id.interest_percentage_input_layout);
        percentageEditText = view.findViewById(R.id.interest_percentage_edit_text);
        percentageEditText.addTextChangedListener(new PercentTextWatcher());
        periodSpinner = view.findViewById(R.id.interest_period_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.periods,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(spinnerAdapter);
        periodSpinner.setOnItemSelectedListener(this);
    }

    public double getInterestPercent() {
        String percentageString = percentageEditText.getText().toString();
        return Double.valueOf(percentageString);
    }

    public int getLoanPeriodInDays() {
        return loanPeriodInDays;
    }

    class PercentTextWatcher implements android.text.TextWatcher {

        private static final int DIGITS_BEFORE_ZERO = 2;
        private static final int DIGITS_AFTER_ZERO = 2;
        Pattern mPattern = Pattern.compile("([0-9]{0," + (DIGITS_BEFORE_ZERO) + "}+((\\.[0-9]{0," + (DIGITS_AFTER_ZERO) + "})?))||(\\.)?");

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            Matcher matcher = mPattern.matcher(s);
            if (!matcher.matches()) {
                Log.d(TAG, "afterTextChanged: incorrect format");
                percentageInputLayout.setError(getString(R.string.new_loan_percentage_format_error));
            } else {
                percentageInputLayout.setError(null);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {

            case 0:
                loanPeriodInDays = 1;
                break;
            case 1:
                loanPeriodInDays = 3;
                break;
            case 2:
                loanPeriodInDays = 7;
                break;
            case 3:
                loanPeriodInDays = 14;
                break;
            case 4:
                loanPeriodInDays = 30;
                break;
            case 5:
                loanPeriodInDays = 365;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
