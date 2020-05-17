package com.romanoindustries.loanmanager.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import com.romanoindustries.loanmanager.notifications.NotificationPreferencesHelper;
import com.romanoindustries.loanmanager.sorting.SortModeHelper;
import com.romanoindustries.loanmanager.viewloaninfo.LoanInfoActivity;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import org.jetbrains.annotations.NotNull;

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
    private AlertDialog deleteDialog;
    private AlertDialog deleteAllDialog;
    private PopupMenu sortPopupMenu;
    private Toolbar toolbar;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_incoming_loans, container, false);
        initViews(view);

        loansViewModel = ((MainActivity) requireActivity()).loansViewModel;
        loansViewModel.getInLoans().observe(this, this::parseLoans);
        registerSharedPreferencesListener();

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.in_loans_toolbar);
        toolbar.inflateMenu(R.menu.fragment_menu);
        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.mnu_item_sort:
                    showSortMenu(view);
                    return true;

                case R.id.mnu_item_notifications:
                    onShowNotificationClicked(item);
                    return true;

                case R.id.mnu_item_delete_all:
                    showDeleteAllDialog();
                    return true;
            }

            return false;
        });
        checkNotificationsMenuItem();


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
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy){
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

    private void buildDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.in_dialog_delete_msg)
                .setNegativeButton(getString(R.string.in_dialog_delete_negative), (dialog, which) -> {});
        deleteDialog = builder.create();
    }

    private void showDeleteDialog(int position) {
        if (deleteDialog == null) {
            buildDeleteDialog();
        }
        deleteDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                getString(R.string.in_dialog_delete_positive),
                (dialog, which) -> archiveLoan(position));
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    private void archiveAllLoans() {
        List<Loan> loansToArchive = loansAdapter.getLoans();
        loansToArchive.forEach(loan -> loan.setType(Loan.TYPE_ARCHIVED_IN));
        loansToArchive.forEach(loansViewModel::update);
    }

    private void buildDeleteAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.archive_all_incoming_loans_dialog_msg)
                .setNegativeButton(getString(R.string.in_dialog_delete_negative),
                        (dialog, which) -> {})
                .setPositiveButton(getString(R.string.in_dialog_delete_positive),
                        ((dialog, which) -> archiveAllLoans()));
        deleteAllDialog = builder.create();
    }

    private void showDeleteAllDialog() {
        if (deleteAllDialog == null) {
            buildDeleteAllDialog();
        }
        if (!deleteAllDialog.isShowing()) {
            deleteAllDialog.show();
        }
    }

    private void parseLoans(List<Loan> loans) {
        if (loans == null) {
            totalAmountTv.setText(getString(R.string.total_amount_zero));
            return;
        }
        SortModeHelper.sortLoansAccordingly(SortModeHelper.getSortMode(requireContext()), loans);
        loansAdapter.updateLoans(loans);
        long totalAmount = loans.stream().mapToLong(Loan::getCurrentAmount).sum();
        totalAmountTv.setText(MainActivity.formatAmount(totalAmount));
    }

    private void showSortMenu(View view) {
        if (sortPopupMenu == null) {
            View menuItemView = view.findViewById(R.id.mnu_item_sort);
            sortPopupMenu = new PopupMenu(requireContext(), menuItemView);
            sortPopupMenu.getMenuInflater().inflate(R.menu.sort_menu, sortPopupMenu.getMenu());
        }
        SortModeHelper.checkCorrectSortItem(sortPopupMenu.getMenu(), getContext());
        sortPopupMenu.show();
        sortPopupMenu.setOnMenuItemClickListener(item -> {
            if (item.isChecked()) {
                return true;
            }
            item.setChecked(true);
            int sortMode = 1;
            switch (item.getItemId()) {

                case R.id.mnu_sort_item_old_first:
                    SortModeHelper.setSortMode(requireContext(), SortModeHelper.SORT_OLD_FIRST);
                    sortMode = SortModeHelper.SORT_OLD_FIRST;
                    break;

                case R.id.mnu_sort_item_new_first:
                    SortModeHelper.setSortMode(requireContext(), SortModeHelper.SORT_NEW_FIRST);
                    sortMode = SortModeHelper.SORT_NEW_FIRST;
                    break;

                case R.id.mnu_sort_item_big_first:
                    SortModeHelper.setSortMode(requireContext(), SortModeHelper.SORT_BIG_FIRST);
                    sortMode = SortModeHelper.SORT_BIG_FIRST;
                    break;

                case R.id.mnu_sort_item_small_first:
                    SortModeHelper.setSortMode(requireContext(), SortModeHelper.SORT_SMALL_FIRST);
                    sortMode = SortModeHelper.SORT_SMALL_FIRST;
                    break;

            }
            loansAdapter.sortModeChanged(sortMode);
            return true;
        });
    }

    private void registerSharedPreferencesListener() {
        sharedPreferenceChangeListener = (sharedPreferences, key) -> {
            if (SortModeHelper.SORT_MODE_KEY.equals(key)) {
                List<Loan> currentLoans = loansAdapter.getLoans();
                SortModeHelper.sortLoansAccordingly(SortModeHelper.getSortMode(requireContext()), currentLoans);
                loansAdapter.updateLoans(currentLoans);
            } else if (NotificationPreferencesHelper.NOTIFICATION_MODE_KEY.equals(key)) {
                checkNotificationsMenuItem();
            }
        };

        SharedPreferences sharedPreferences = requireContext()
                .getSharedPreferences(SortModeHelper.SORT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        sharedPreferences = requireContext()
                .getSharedPreferences(NotificationPreferencesHelper.NOTIFICATION_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);

    }

    private void checkNotificationsMenuItem() {
        if (NotificationPreferencesHelper
                .getNotificationMode(requireContext()) == NotificationPreferencesHelper.NOTIFICATION_MODE_SHOW_ALL) {
            toolbar.getMenu().findItem(R.id.mnu_item_notifications).setChecked(true);
        } else {
            toolbar.getMenu().findItem(R.id.mnu_item_notifications).setChecked(false);
        }
    }

    private void onShowNotificationClicked(@NotNull MenuItem item) {
        item.setChecked(!item.isChecked());
        if (item.isChecked()) {
            NotificationPreferencesHelper
                    .setNotificationMode(requireContext(),
                            NotificationPreferencesHelper.NOTIFICATION_MODE_SHOW_ALL);
        } else {
            NotificationPreferencesHelper
                    .setNotificationMode(requireContext(),
                            NotificationPreferencesHelper.NOTIFICATION_MODE_NOT_SHOW);
        }
    }
}













