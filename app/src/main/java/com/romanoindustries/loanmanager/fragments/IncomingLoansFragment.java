package com.romanoindustries.loanmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.util.List;

public class IncomingLoansFragment extends Fragment {

    private LoansViewModel loansViewModel;

    public IncomingLoansFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loansViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getActivity().getApplication())
                .create(LoansViewModel.class);

        loansViewModel.getInLoans().observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(List<Loan> loans) {

            }
        });

        return inflater.inflate(R.layout.fragment_incoming_loans, container, false);
    }

}
