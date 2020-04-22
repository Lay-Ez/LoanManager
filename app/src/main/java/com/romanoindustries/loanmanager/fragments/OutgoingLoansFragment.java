package com.romanoindustries.loanmanager.fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.adapters.LoansAdapter;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.newloan.NewLoanActivity;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.util.ArrayList;


public class OutgoingLoansFragment extends Fragment implements LoansAdapter.OnLoanListener{
    private static final String TAG = "OutgoingLoansFragment";

    private FloatingActionButton fab;
    private LoansViewModel loansViewModel;
    private LoansAdapter loansAdapter;

    public OutgoingLoansFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_outgoing_loans, container, false);
        initViews(view);

        loansViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getActivity().getApplication())
                .create(LoansViewModel.class);

        loansViewModel.getOutLoans().observe(this, loans -> loansAdapter.updateLoans(loans));
        return view;
    }

    private void initViews(View view) {
        fab = view.findViewById(R.id.out_loans_fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewLoanActivity.class);
            intent.putExtra(NewLoanActivity.LOAN_TYPE_KEY, Loan.TYPE_OUT);
            startActivity(intent);
        });

        RecyclerView recyclerView = view.findViewById(R.id.out_loans_recycler_view);
        loansAdapter = new LoansAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(loansAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx,int dy){
                super.onScrolled(recyclerView, dx, dy);

                if (dy >0) {
                    if (fab.isShown()) {
                        fab.hide();
                    }
                }
                else if (dy <0) {
                    if (!fab.isShown()) {
                        fab.show();
                    }
                }
            }
        });
    }

    @Override
    public void onLoanCLicked(int position) {
        Log.d(TAG, "onLoanCLicked: " + position);
    }

    @Override
    public void onLoanDeleteClicked(int position) {
        showDeleteDialog(position);
    }

    private void archiveLoan(int position) {
        Loan loanToArchive = loansAdapter.getLoans().get(position);
        loanToArchive.setType(Loan.TYPE_ARCHIVED_IN);
        loansViewModel.update(loanToArchive);
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.in_dialog_delete_title)
                .setMessage(R.string.in_dialog_delete_msg)
                .setPositiveButton(getString(R.string.in_dialog_delete_positive),
                        (dialog, which) -> archiveLoan(position))
                .setNegativeButton(getString(R.string.in_dialog_delete_negative), (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onLoanEditClicked(int position) {

    }
}
