package com.romanoindustries.loanmanager.archivedloans;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.romanoindustries.loanmanager.MainActivity;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.sorting.SortModeHelper;
import com.romanoindustries.loanmanager.viewloaninfo.LoanInfoActivity;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArchivedLoansFragment extends Fragment implements ArchivedLoansAdapter.ArchOnLoanListener {
    public ArchivedLoansFragment() {}

    private static final String TAG = "ArchivedLoansFragment";
    private RecyclerView recyclerView;
    private ArchivedLoansAdapter loansAdapter;
    private LoansViewModel loansViewModel;
    private PopupMenu sortPopupMenu;

    private TextView inLoansTotalTv;
    private TextView outLoansTotalTv;

    private SharedPreferences.OnSharedPreferenceChangeListener listener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archived_loans, container, false);
        initViews(view);
        loansViewModel = ((MainActivity) requireActivity()).loansViewModel;
        loansViewModel.getArchivedLoans().observe(this, this::parseLoans);
        registerSharedPreferencesListener();
        return view;
    }

    private void initViews(View view) {
        inLoansTotalTv = view.findViewById(R.id.arch_loans_total_amount_in_tv);
        outLoansTotalTv = view.findViewById(R.id.arch_loans_total_amount_out_tv);

        Toolbar toolbar = view.findViewById(R.id.arch_loans_toolbar);
        toolbar.inflateMenu(R.menu.fragment_menu);
        toolbar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()) {

                case R.id.mnu_item_sort:
                    showSortMenu(view);
                    return true;
            }

            return false;
        });

        recyclerView = view.findViewById(R.id.arch_loans_recycler_view);
        loansAdapter = new ArchivedLoansAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(loansAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
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

    private void deleteLoan(int position) {
        Loan loanToDelete = loansAdapter.getLoans().get(position);
        loansViewModel.delete(loanToDelete);
    }

    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.arch_dialog_delete_msg)
                .setPositiveButton(getString(R.string.arch_dialog_delete_positive),
                        (dialog, which) -> deleteLoan(position))
                .setNegativeButton(getString(R.string.arch_dialog_delete_negative), (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void parseLoans(List<Loan> loans) {
        if (loans == null) {
            return;
        }
        setAmountText(loans);
        SortModeHelper.sortLoansAccordingly(SortModeHelper.getSortMode(getContext()), loans);
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

    private void showSortMenu(View view) {
        if (sortPopupMenu == null) {
            View menuItemView = view.findViewById(R.id.mnu_item_sort);
            sortPopupMenu = new PopupMenu(getContext(), menuItemView);
            sortPopupMenu.getMenuInflater().inflate(R.menu.sort_menu, sortPopupMenu.getMenu());
        }
        SortModeHelper.checkCorrectSortItem(sortPopupMenu.getMenu(), getContext());
        sortPopupMenu.show();
        sortPopupMenu.setOnMenuItemClickListener(item -> {
            if (item.isChecked()) {
                return true;
            }
            item.setChecked(true);int sortMode = 1;
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

    private void registerSharedPreferencesListener() {
        listener = (sharedPreferences, key) -> {
            if (SortModeHelper.SORT_MODE_KEY.equals(key)) {
                List<Loan> currentLoans = loansAdapter.getLoans();
                SortModeHelper.sortLoansAccordingly(SortModeHelper.getSortMode(requireContext()), currentLoans);
                loansAdapter.updateLoans(currentLoans);
            }
        };

        SharedPreferences sharedPreferences = requireContext()
                .getSharedPreferences(SortModeHelper.SORT_PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }
}


















