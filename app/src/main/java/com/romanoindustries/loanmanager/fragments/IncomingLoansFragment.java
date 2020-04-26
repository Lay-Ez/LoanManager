package com.romanoindustries.loanmanager.fragments;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.romanoindustries.loanmanager.alertreceiver.AlertReceiver;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.newloan.NewLoanActivity;
import com.romanoindustries.loanmanager.viewloaninfo.LoanInfoActivity;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.util.ArrayList;
import java.util.Calendar;

public class IncomingLoansFragment extends Fragment implements LoansAdapter.OnLoanListener {
    private static final String TAG = "IncomingLoansFragment";

    public IncomingLoansFragment() {}

    private FloatingActionButton fab;
    private LoansViewModel loansViewModel;
    private LoansAdapter loansAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_incoming_loans, container, false);
        initViews(view);

        loansViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getActivity().getApplication())
                .create(LoansViewModel.class);

        loansViewModel.getInLoans().observe(this, loans -> loansAdapter.updateLoans(loans));

        return view;
    }

    private void initViews(View view) {
        fab = view.findViewById(R.id.in_loans_fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NewLoanActivity.class);
            intent.putExtra(NewLoanActivity.LOAN_TYPE_KEY, Loan.TYPE_IN);
            startActivity(intent);
        });

        RecyclerView recyclerView = view.findViewById(R.id.in_loans_recycler_view);
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
        Loan loanToView = loansAdapter.getLoans().get(position);
        Intent intent = new Intent(getContext(), LoanInfoActivity.class);
        intent.putExtra(LoanInfoActivity.LOAN_ID_KEY, loanToView.getId());
        startActivity(intent);
    }

    @Override
    public void onLoanDeleteClicked(int position) {
        showDeleteDialog(position);
    }

    @Override
    public void onLoanHighlightClicked(int position) {
        Loan loanToHighlight = loansAdapter.getLoans().get(position);
        boolean loanHighlighted = loanToHighlight.isHighlighted();
        loanToHighlight.setHighlighted(!loanHighlighted);
        loansViewModel.update(loanToHighlight);
        startAlarm();
    }

    private void startAlarm() {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 20);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void archiveLoan(int position) {
        Loan loanToArchive = loansAdapter.getLoans().get(position);
        loanToArchive.setType(Loan.TYPE_ARCHIVED_IN);
        loanToArchive.setNextChargingDateInMs(0);
        loanToArchive.setPaymentDateInMs(Calendar.getInstance().getTimeInMillis());
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
}
