package com.romanoindustries.loanmanager.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
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


public class OutgoingLoansFragment extends Fragment implements LoansAdapter.OnLoanListener{
    private static final String TAG = "OutgoingLoansFragment";

    private FloatingActionButton fab;
    private LoansViewModel loansViewModel;
    private LoansAdapter loansAdapter;
    private TextView totalAmountTv;
    private AlertDialog deleteDialog;

    public OutgoingLoansFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_outgoing_loans, container, false);
        initViews(view);
        buildDeleteDialog();

        loansViewModel = new ViewModelProvider
                .AndroidViewModelFactory(requireActivity().getApplication())
                .create(LoansViewModel.class);

        loansViewModel.getOutLoans().observe(this, this::parseLoans);
        return view;
    }

    private void initViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.out_loans_toolbar);
        toolbar.inflateMenu(R.menu.fragment_menu);
        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.mnu_item_sort:
                    showSortMenu(view);
                    return true;

            }

            return false;
        });

        totalAmountTv = view.findViewById(R.id.out_loans_total_amount_tv);

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
        Loan loanToView = loansAdapter.getLoans().get(position);
        Intent intent = new Intent(getContext(), LoanInfoActivity.class);
        intent.putExtra(LoanInfoActivity.LOAN_ID_KEY, loanToView.getId());
        startActivity(intent);
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
    public void onLoanDeleteClicked(int position) {
        showDeleteDialog(position);
    }

    private void archiveLoan(int position) {
        Loan loanToArchive = loansAdapter.getLoans().get(position);
        loanToArchive.setType(Loan.TYPE_ARCHIVED_OUT);
        loanToArchive.setPaymentDateInMs(Calendar.getInstance().getTimeInMillis());
        loanToArchive.setNextChargingDateInMs(0);
        loansViewModel.update(loanToArchive);
    }

    private void buildDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.in_dialog_delete_title)
                .setMessage(R.string.in_dialog_delete_msg)
                .setNegativeButton(getString(R.string.in_dialog_delete_negative), (dialog, which) -> {});
        deleteDialog = builder.create();
    }

    private void showDeleteDialog(int position) {
        deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getString(R.string.in_dialog_delete_positive),
                (dialog, which) -> archiveLoan(position));
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    @Override
    public void onLoanHighlightClicked(int position) {
        Loan loanToHighlight = loansAdapter.getLoans().get(position);
        boolean loanHighlighted = loanToHighlight.isHighlighted();
        loanToHighlight.setHighlighted(!loanHighlighted);
        loansViewModel.update(loanToHighlight);
    }

    private void parseLoans(List<Loan> loans) {
        if (loans == null) {
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
