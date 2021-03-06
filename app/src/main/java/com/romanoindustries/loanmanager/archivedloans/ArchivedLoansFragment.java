package com.romanoindustries.loanmanager.archivedloans;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.romanoindustries.loanmanager.MainActivity;
import com.romanoindustries.loanmanager.MyApp;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.currency.CurrencyHelper;
import com.romanoindustries.loanmanager.currency.CurrencyMenuItemClickListener;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.newloan.NewLoanVmHelper;
import com.romanoindustries.loanmanager.notifications.NotificationPreferencesHelper;
import com.romanoindustries.loanmanager.sorting.SortModeHelper;
import com.romanoindustries.loanmanager.viewloaninfo.LoanInfoActivity;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ArchivedLoansFragment extends Fragment implements ArchivedLoansAdapter.ArchOnLoanListener {
    public ArchivedLoansFragment() {}

    private ArchivedLoansAdapter loansAdapter;
    private LoansViewModel loansViewModel;
    private PopupMenu sortPopupMenu;
    private PopupMenu currencyPopupMenu;
    private AlertDialog deleteDialog;
    private AlertDialog deleteAllDialog;
    private AlertDialog unarchiveDialog;

    private TextView inLoansTotalTv;
    private TextView outLoansTotalTv;
    private TextView currencyLabel1;
    private TextView currencyLabel2;
    private Toolbar toolbar;

    private ImageView emptyIv;
    private TextView emptyTv;

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archived_loans, container, false);
        initViews(view);
        loansViewModel = ((MainActivity) requireActivity()).loansViewModel;
        loansViewModel.getArchivedLoans().observe(this, this::parseLoans);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSharedPreferencesListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterSharedPreferencesListener();
    }

    private void initViews(View view) {
        inLoansTotalTv = view.findViewById(R.id.arch_loans_total_amount_in_tv);
        outLoansTotalTv = view.findViewById(R.id.arch_loans_total_amount_out_tv);
        currencyLabel1 = view.findViewById(R.id.arch_loans_total_amount_currency_label);
        currencyLabel2 = view.findViewById(R.id.arch_loans_total_amount_currency_label2);

        toolbar = view.findViewById(R.id.arch_loans_toolbar);
        toolbar.inflateMenu(R.menu.fragment_menu_arch);
        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.mnu_item_sort:
                    showSortMenu(view);
                    return true;

                case R.id.mnu_item_notifications:
                    onShowNotificationClicked(item);
                    return true;

                case R.id.mnu_item_choose_currency:
                    showCurrencyMenu(view);
                    return true;

                case R.id.mnu_item_delete_all:
                    showDeleteAllDialog();
                    return true;
            }

            return false;
        });
        checkNotificationsMenuItem();

        RecyclerView recyclerView = view.findViewById(R.id.arch_loans_recycler_view);
        loansAdapter = new ArchivedLoansAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(loansAdapter);
        displayCurrentCurrency();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        emptyIv = view.findViewById(R.id.empty_list_im);
        emptyTv = view.findViewById(R.id.empty_list_tv);
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
    public void onLoanUnarchiveClicked(int position) {
        showUnarchiveDialog(position);
    }

    private void buildUnarchiveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.unarchive_dialog_msg)
                .setNegativeButton(getString(R.string.unarchive_dialog_negative), (dialog, which) -> {});
        unarchiveDialog = builder.create();
    }

    private void showUnarchiveDialog(int position) {
        if (unarchiveDialog == null) {
            buildUnarchiveDialog();
        }
        unarchiveDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getString(R.string.unarchive_dialog_positive),
                (dialog, which) -> unarchiveLoan(position));
        if (!unarchiveDialog.isShowing()) {
            unarchiveDialog.show();
        }
    }

    private void unarchiveLoan(int position) {
        Loan loanToUnarchive = loansAdapter.getLoans().get(position);

        if (loanToUnarchive.getInterestRate() != 0.0) {
            NewLoanVmHelper helper = new NewLoanVmHelper();
            long nextChargingDate = helper.calculateNextChargingTime(
                    Calendar.getInstance().getTimeInMillis(),
                    loanToUnarchive.getPeriodInDays());
            loanToUnarchive.setNextChargingDateInMs(nextChargingDate);
        }

        if (loanToUnarchive.getType() == Loan.TYPE_ARCHIVED_IN) {
            loanToUnarchive.setType(Loan.TYPE_IN);
        } else if (loanToUnarchive.getType() == Loan.TYPE_ARCHIVED_OUT) {
            loanToUnarchive.setType(Loan.TYPE_OUT);
        }
        loansViewModel.update(loanToUnarchive);
    }

    private void buildDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.arch_dialog_delete_msg)
                .setNegativeButton(getString(R.string.arch_dialog_delete_negative), (dialog, which) -> {});
        deleteDialog = builder.create();
    }

    private void showDeleteDialog(int position) {
        if (deleteDialog == null) {
            buildDeleteDialog();
        }
        deleteDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getString(R.string.arch_dialog_delete_positive),
                (dialog, which) -> deleteLoan(position));

        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    private void deleteLoan(int position) {
        Loan loanToDelete = loansAdapter.getLoans().get(position);
        loansViewModel.delete(loanToDelete);
    }

    private void buildDeleteAllDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.delete_all_archived_loans_dialog_msg)
                .setNegativeButton(getString(R.string.arch_dialog_delete_negative),
                        (dialog, which) -> {})
                .setPositiveButton(getString(R.string.arch_dialog_delete_positive),
                        ((dialog, which) -> deleteAllLoans()));
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

    private void deleteAllLoans() {
        loansAdapter.getLoans().forEach(loansViewModel::delete);
    }

    private void parseLoans(List<Loan> loans) {
        if (loans == null) {
            return;
        }
        if (loans.isEmpty()) {
            emptyIv.setVisibility(View.VISIBLE);
            emptyTv.setVisibility(View.VISIBLE);
            toolbar.getMenu().findItem(R.id.mnu_item_delete_all).setEnabled(false);
        } else {
            emptyIv.setVisibility(View.GONE);
            emptyTv.setVisibility(View.GONE);
            toolbar.getMenu().findItem(R.id.mnu_item_delete_all).setEnabled(true);
        }
        setAmountText(loans);
        SortModeHelper.sortLoansAccordingly(SortModeHelper.getSortMode(requireContext()), loans);
        loansAdapter.updateLoans(loans);
    }

    private void setAmountText(List<Loan> loans) {
        long incomingLoansTotal = loans.stream()
                .filter(loan -> loan.getType() == Loan.TYPE_ARCHIVED_IN)
                .mapToLong(Loan::getCurrentAmount)
                .sum();

        long outgoingLoansTotal = loans.stream()
                .filter(loan -> loan.getType() == Loan.TYPE_ARCHIVED_OUT)
                .mapToLong(Loan::getCurrentAmount)
                .sum();

        inLoansTotalTv.setText(formatNumber(incomingLoansTotal));
        outLoansTotalTv.setText(formatNumber(outgoingLoansTotal));
    }

    private String formatNumber(long amount) {
        if (amount < 1000000) {
            return MainActivity.formatAmount(amount);
        } else {
            long totalThousands = (amount - (amount % 1000)) / 1000;
            return MainActivity.formatAmount(totalThousands) + " " + getString(R.string.arch_thousands);
        }
    }

    private void showCurrencyMenu(View view) {
        if (currencyPopupMenu == null) {
            View menuItemView = view.findViewById(R.id.mnu_item_sort);
            currencyPopupMenu = new PopupMenu(requireContext(), menuItemView);
            currencyPopupMenu.getMenuInflater().inflate(R.menu.currency_menu, currencyPopupMenu.getMenu());
            currencyPopupMenu.setOnMenuItemClickListener(new CurrencyMenuItemClickListener(requireContext()));
        }
        CurrencyHelper.checkCorrectCurrencyItem(currencyPopupMenu.getMenu(), requireContext());
        currencyPopupMenu.show();
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

            switch (item.getItemId()) {

                case R.id.mnu_sort_item_old_first:
                    SortModeHelper.setSortMode(requireContext(), SortModeHelper.SORT_OLD_FIRST);
                    break;

                case R.id.mnu_sort_item_new_first:
                    SortModeHelper.setSortMode(requireContext(), SortModeHelper.SORT_NEW_FIRST);
                    break;

                case R.id.mnu_sort_item_big_first:
                    SortModeHelper.setSortMode(requireContext(), SortModeHelper.SORT_BIG_FIRST);
                    break;

                case R.id.mnu_sort_item_small_first:
                    SortModeHelper.setSortMode(requireContext(), SortModeHelper.SORT_SMALL_FIRST);
                    break;

            }
            return true;
        });
    }

    private void registerSharedPreferencesListener() {
        sharedPreferenceChangeListener = (sharedPreferences, key) -> {
            if (SortModeHelper.SORT_MODE_KEY.equals(key)) {
                List<Loan> currentLoans = new ArrayList<>(loansAdapter.getLoans());
                SortModeHelper.sortLoansAccordingly(SortModeHelper.getSortMode(requireContext()), currentLoans);
                loansAdapter.updateLoans(currentLoans);
            } else if (NotificationPreferencesHelper.NOTIFICATION_MODE_KEY.equals(key)) {
                checkNotificationsMenuItem();
            } else if (CurrencyHelper.CURRENCY_PREFERENCE_KEY.equals(key)) {
                displayCurrentCurrency();
            }
        };

        SharedPreferences sharedPreferences = requireContext()
                .getSharedPreferences(SortModeHelper.SORT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        sharedPreferences = requireContext()
                .getSharedPreferences(NotificationPreferencesHelper.NOTIFICATION_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        sharedPreferences = requireContext()
                .getSharedPreferences(CurrencyHelper.CURRENCY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    private void unregisterSharedPreferencesListener() {
        SharedPreferences sharedPreferences = requireContext()
                .getSharedPreferences(SortModeHelper.SORT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        sharedPreferences = requireContext()
                .getSharedPreferences(NotificationPreferencesHelper.NOTIFICATION_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        sharedPreferences = requireContext()
                .getSharedPreferences(CurrencyHelper.CURRENCY_PREFERENCE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }


    private void displayCurrentCurrency() {
        String newCurrencyLabel = CurrencyHelper.getCurrentCurrencyLabel(MyApp.getContext());
        loansAdapter.setCurrencyLabel(newCurrencyLabel);
        loansAdapter.notifyDataSetChanged();
        currencyLabel1.setText(newCurrencyLabel);
        currencyLabel2.setText(newCurrencyLabel);
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