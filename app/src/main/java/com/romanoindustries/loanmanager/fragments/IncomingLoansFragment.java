package com.romanoindustries.loanmanager.fragments;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.romanoindustries.loanmanager.MainActivity;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.adapters.LoansAdapter;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.editloan.EditLoanActivity;
import com.romanoindustries.loanmanager.editloan.EditLoanActivityKt;
import com.romanoindustries.loanmanager.newloan.NewLoanActivity;
import com.romanoindustries.loanmanager.sorting.SortModeHelper;
import com.romanoindustries.loanmanager.viewloaninfo.LoanInfoActivity;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class IncomingLoansFragment extends Fragment implements LoansAdapter.OnLoanListener {
    private static final String TAG = "IncomingLoansFragment";

    public IncomingLoansFragment() {}

    private FloatingActionButton fab;
    private LoansViewModel loansViewModel;
    private LoansAdapter loansAdapter;
    private TextView totalAmountTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_incoming_loans, container, false);
        initViews(view);

        loansViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getActivity().getApplication())
                .create(LoansViewModel.class);

        loansViewModel.getInLoans().observe(this, this::parseLoans);

        return view;
    }

    private void initViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.in_loans_toolbar);
        toolbar.inflateMenu(R.menu.fragment_menu);
        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.mnu_item_sort:
                    showSortMenu(view);
                    return true;

            }

            return false;
        });

        totalAmountTv = view.findViewById(R.id.in_loans_total_amount_tv);
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
    public void onLoanEditClicked(int position) {
        Loan loanToEdit = loansAdapter.getLoans().get(position);
        startEditLoanActivity(loanToEdit.getId());
    }

    private void startEditLoanActivity(int loanId) {
        Intent intent = new Intent(getContext(), EditLoanActivity.class);
        intent.putExtra(EditLoanActivityKt.LOAN_ID_KEY, loanId);
        startActivity(intent);
    }

    @Override
    public void onLoanHighlightClicked(int position) {
        Loan loanToHighlight = loansAdapter.getLoans().get(position);
        boolean loanHighlighted = loanToHighlight.isHighlighted();
        loanToHighlight.setHighlighted(!loanHighlighted);
        loansViewModel.update(loanToHighlight);
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

    private void parseLoans(List<Loan> loans) {
        if (loans == null || loans.isEmpty()) {
            totalAmountTv.setText(getString(R.string.total_amount_zero));
            return;
        }
        SortModeHelper.sortLoansAccordingly(SortModeHelper.getSortMode(getContext()), loans);
        loansAdapter.updateLoans(loans);
        long totalAmount = loans.stream().mapToLong(Loan::getCurrentAmount).sum();
        totalAmountTv.setText(MainActivity.formatAmount(totalAmount));
    }

    private void showSortMenu(View view) {
        View menuItemView = view.findViewById(R.id.mnu_item_sort);
        PopupMenu popupMenu = new PopupMenu(getContext(), menuItemView);
        popupMenu.getMenuInflater().inflate(R.menu.sort_menu, popupMenu.getMenu());
        SortModeHelper.checkCorrectSortItem(popupMenu.getMenu(), getContext());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.isChecked()) {
                return true;
            }
            item.setChecked(true);
            int sortMode = 1;
            switch (item.getItemId()) {

                case R.id.mnu_sort_item_old_first:
                    SortModeHelper.setSortMode(getContext(), SortModeHelper.SORT_OLD_FIRST);
                    sortMode = SortModeHelper.SORT_OLD_FIRST;
                    break;

                case R.id.mnu_sort_item_new_first:
                    SortModeHelper.setSortMode(getContext(), SortModeHelper.SORT_NEW_FIRST);
                    sortMode = SortModeHelper.SORT_NEW_FIRST;
                    break;

                case R.id.mnu_sort_item_big_first:
                    SortModeHelper.setSortMode(getContext(), SortModeHelper.SORT_BIG_FIRST);
                    sortMode = SortModeHelper.SORT_BIG_FIRST;
                    break;

                case R.id.mnu_sort_item_small_first:
                    SortModeHelper.setSortMode(getContext(), SortModeHelper.SORT_SMALL_FIRST);
                    sortMode = SortModeHelper.SORT_SMALL_FIRST;
                    break;

            }
            loansAdapter.sortModeChanged(sortMode);
            return true;
        });
    }
}













