package com.romanoindustries.loanmanager.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.romanoindustries.loanmanager.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OutgoingLoansFragment extends Fragment {


    public OutgoingLoansFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_outgoing_loans, container, false);
    }

}
