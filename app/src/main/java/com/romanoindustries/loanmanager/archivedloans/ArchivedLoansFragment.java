package com.romanoindustries.loanmanager.archivedloans;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.sorting.SortModeHelper;
import com.romanoindustries.loanmanager.viewloaninfo.LoanInfoActivity;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.util.ArrayList;
import java.util.List;

public class ArchivedLoansFragment extends Fragment implements ArchivedLoansAdapter.ArchOnLoanListener {
    public ArchivedLoansFragment() {}

    private RecyclerView recyclerView;
    private ArchivedLoansAdapter loansAdapter;
    private LoansViewModel loansViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archived_loans, container, false);
        initViews(view);

        loansViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getActivity().getApplication())
                .create(LoansViewModel.class);

        loansViewModel.getArchivedLoans().observe(this, new Observer<List<Loan>>() {
            @Override
            public void onChanged(List<Loan> loans) {
                loansAdapter.updateLoans(loans);
            }
        });

        return view;
    }

    private void initViews(View view) {
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
            switch (item.getItemId()) {

                case R.id.mnu_sort_item_old_first:
                    SortModeHelper.setSortMode(getContext(), SortModeHelper.SORT_OLD_FIRST);
                    break;

                case R.id.mnu_sort_item_new_first:
                    SortModeHelper.setSortMode(getContext(), SortModeHelper.SORT_NEW_FIRST);
                    break;

                case R.id.mnu_sort_item_big_first:
                    SortModeHelper.setSortMode(getContext(), SortModeHelper.SORT_BIG_FIRST);
                    break;

                case R.id.mnu_sort_item_small_first:
                    SortModeHelper.setSortMode(getContext(), SortModeHelper.SORT_SMALL_FIRST);
                    break;

            }
            return true;
        });
    }
}


















