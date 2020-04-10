package com.romanoindustries.loanmanager.incomingloans;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.romanoindustries.loanmanager.R;

public class IncomingLoansFragment extends Fragment {

    public IncomingLoansFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_incoming_loans, container, false);
    }

}
