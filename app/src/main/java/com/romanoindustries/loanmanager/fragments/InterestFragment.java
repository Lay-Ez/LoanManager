package com.romanoindustries.loanmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.viewmodels.NewLoanViewModel;

import java.util.Locale;

public class InterestFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "InterestFragment";

    public static final int LOAN_PERIOD_ONE_DAY = 1;
    public static final int LOAN_PERIOD_THREE_DAYS = 3;
    public static final int LOAN_PERIOD_ONE_WEEK = 7;
    public static final int LOAN_PERIOD_TWO_WEEKS = 14;
    public static final int LOAN_PERIOD_ONE_MONTH = 30;
    public static final int LOAN_PERIOD_ONE_YEAR = 365;

    private Spinner periodSpinner;
    private NumberPicker wholePercentNp;
    private NumberPicker decimalPercentNp;

    private NewLoanViewModel newLoanViewModel;

    public InterestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interest, container, false);
        initViews(view);
        newLoanViewModel = new ViewModelProvider(requireActivity()).get(NewLoanViewModel.class);
        observeViewModel(newLoanViewModel);
        return view;
    }

    private void initViews(View view) {
        periodSpinner = view.findViewById(R.id.interest_period_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.periods,
                android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodSpinner.setAdapter(spinnerAdapter);
        periodSpinner.setOnItemSelectedListener(this);

        wholePercentNp = view.findViewById(R.id.whole_np);
        wholePercentNp.setMinValue(0);
        wholePercentNp.setMaxValue(99);
        wholePercentNp.setOnValueChangedListener((picker, oldVal, newVal) -> {
            newLoanViewModel.setWholeInterestPercent(newVal);
        });

        decimalPercentNp = view.findViewById(R.id.decimal_np);
        decimalPercentNp.setMinValue(0);
        decimalPercentNp.setMaxValue(99);
        decimalPercentNp.setOnValueChangedListener((picker, oldVal, newVal) -> {
            newLoanViewModel.setDecimalInterestPercent(newVal);});
        decimalPercentNp.setFormatter(value -> String.format(Locale.US ,"%02d", value));
    }

    private void observeViewModel(NewLoanViewModel newLoanViewModel) {
        newLoanViewModel.getWholeInterestPercent().observe(this, integer -> wholePercentNp.setValue(integer));
        newLoanViewModel.getDecimalInterestPercent().observe(this, integer -> decimalPercentNp.setValue(integer));
        newLoanViewModel.getPeriodInDays().observe(this, this::restoreSpinnerPosition);
    }

    private void restoreSpinnerPosition(int loanPeriodInDays) {
        switch (loanPeriodInDays) {

            case LOAN_PERIOD_ONE_DAY:
                periodSpinner.setSelection(0);
                break;

            case LOAN_PERIOD_THREE_DAYS:
                periodSpinner.setSelection(1);
                break;

            case LOAN_PERIOD_ONE_WEEK:
                periodSpinner.setSelection(2);
                break;

            case LOAN_PERIOD_TWO_WEEKS:
                periodSpinner.setSelection(3);
                break;

            case LOAN_PERIOD_ONE_MONTH:
                periodSpinner.setSelection(4);
                break;

            case LOAN_PERIOD_ONE_YEAR:
                periodSpinner.setSelection(5);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int loanPeriodInDays = 1;
        switch (position) {

            case 0:
                loanPeriodInDays = LOAN_PERIOD_ONE_DAY;
                break;
            case 1:
                loanPeriodInDays = LOAN_PERIOD_THREE_DAYS;
                break;
            case 2:
                loanPeriodInDays = LOAN_PERIOD_ONE_WEEK;
                break;
            case 3:
                loanPeriodInDays = LOAN_PERIOD_TWO_WEEKS;
                break;
            case 4:
                loanPeriodInDays = LOAN_PERIOD_ONE_MONTH;
                break;
            case 5:
                loanPeriodInDays = LOAN_PERIOD_ONE_YEAR;
                break;
        }
        newLoanViewModel.setPeriodInDays(loanPeriodInDays);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
