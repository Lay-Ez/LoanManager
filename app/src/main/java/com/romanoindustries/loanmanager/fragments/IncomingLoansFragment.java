package com.romanoindustries.loanmanager.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.InterestChargeEvent;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;

public class IncomingLoansFragment extends Fragment {
    private static final String TAG = "IncomingLoansFragment";

    public IncomingLoansFragment() {}

    private FloatingActionButton fab;
    private LoansViewModel loansViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_incoming_loans, container, false);

        loansViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getActivity().getApplication())
                .create(LoansViewModel.class);

        loansViewModel.getInLoans().observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(List<Loan> loans) {
                loans.forEach(new Consumer<Loan>() {
                    @Override
                    public void accept(Loan loan) {

                    }
                });
            }
        });

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.in_loans_recycler_view);

        fab = view.findViewById(R.id.in_loans_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Loan testLoan = new Loan();
                testLoan.setDebtorName("Test name");
                testLoan.setSpecialNote("Test special note");
                testLoan.setStartAmount(1000);
                testLoan.setCurrentAmount(1010);
                testLoan.setPhoneNumber("88005553535");

                Calendar calendar = Calendar.getInstance();
                testLoan.setStartDateInMs(calendar.getTimeInMillis());

                calendar.add(Calendar.DAY_OF_YEAR, 10);
                testLoan.setPaymentDateInMs(calendar.getTimeInMillis());

                calendar.add(Calendar.WEEK_OF_YEAR, 10);
                testLoan.setNextChargingDateInMs(calendar.getTimeInMillis());
                testLoan.setPeriodInDays(10);
                testLoan.setInterestRate(1.5);
                testLoan.setType(Loan.TYPE_IN);
                testLoan.setChargeEvents(new ArrayList<InterestChargeEvent>());

                loansViewModel.insert(testLoan);
                Log.d(TAG, "onClick: new loan inserted");
            }
        });
    }
}
