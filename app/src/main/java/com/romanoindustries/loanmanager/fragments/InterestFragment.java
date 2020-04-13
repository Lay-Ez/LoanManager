package com.romanoindustries.loanmanager.fragments;


import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.romanoindustries.loanmanager.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InterestFragment extends Fragment {
    private static final String TAG = "InterestFragment";

    private TextInputLayout percentageInputLayout;
    private TextInputEditText percentageEditText;


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
    }

    class PercentTextWatcher implements android.text.TextWatcher {

        private static final int DIGITS_BEFORE_ZERO = 3;
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
                percentageInputLayout.setError("Incorrect format");
            } else {
                percentageInputLayout.setError(null);
            }
        }
    }
}
