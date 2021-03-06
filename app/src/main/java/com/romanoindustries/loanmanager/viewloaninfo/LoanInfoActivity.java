package com.romanoindustries.loanmanager.viewloaninfo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.romanoindustries.loanmanager.MainActivity;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.databinding.ActivityLoanInfoBinding;
import com.romanoindustries.loanmanager.datamodel.Loan;

public class LoanInfoActivity extends AppCompatActivity {
    public static final String LOAN_ID_KEY = "loan_id_key";
    private ActivityLoanInfoBinding binding;
    private LoanInfoViewModel viewModel;
    private AlertDialog deleteDialog;
    private Loan viewedLoan;
    private boolean loanIsArchived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoanInfoBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        setSupportActionBar(binding.loansInfoToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Intent intent = getIntent();
        int loanId = intent.getIntExtra(LOAN_ID_KEY, -1);
        if (loanId == -1) {
            Toast.makeText(this, R.string.info_activity_error_message, Toast.LENGTH_LONG).show();
            onBackPressed();
        }
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication())
                .create(LoanInfoViewModel.class);
        viewModel.getAllLoans().observe(this, loans -> {
            if (loans != null) {
                for (Loan loan : loans) {
                    if (loan.getId() == loanId) {
                        displayLoan(loan);
                        viewedLoan = loan;
                        break;
                    }
                }
            }
        });
    }

    private void displayLoan(Loan loan) {
        String name = loan.getDebtorName();
        if (!name.isEmpty()) {
            binding.infoNameTv.setText(name);
        }

        String phone = loan.getPhoneNumber();
        if (!phone.equals("")) {
            binding.infoPhoneTv.setText(phone);
        } else {
            binding.infoPhoneTv.setText(R.string.not_specified_string);
        }

        binding.infoStartDateTv.setText(LoanInfoHelper.formatDate(loan.getStartDateInMs()));

        if (loan.getPaymentDateInMs() != 0) {
            binding.infoEndDateTv.setText(LoanInfoHelper.formatDate(loan.getPaymentDateInMs()));
        } else {
            binding.infoEndDateTv.setText(R.string.not_specified_string);
        }

        if (loan.getNextChargingDateInMs() != 0) {
            binding.infoNextAccrualTv.setText(LoanInfoHelper.formatDate(loan.getNextChargingDateInMs()));
        } else {
            binding.infoNextAccrualTv.setText(R.string.not_specified_string);
        }

        int startAmount = loan.getStartAmount();
        binding.infoStartAmountTv.setText(MainActivity.formatAmount(startAmount));

        int currentAmount = loan.getCurrentAmount();
        binding.infoCurrentAmountTv.setText(MainActivity.formatAmount(currentAmount));

        double interestRate = loan.getInterestRate();
        if (interestRate > 0.0) {
            String rateStr = interestRate + "%";
            binding.infoInterestRateTv.setText(rateStr);
        } else {
            binding.infoInterestRateTv.setText(R.string.not_specified_string);
        }

        int periodInDays = loan.getPeriodInDays();
        if (periodInDays != 0) {
            binding.infoPeriodTv.setText(String.valueOf(periodInDays));
        } else {
            binding.infoPeriodTv.setText(R.string.not_specified_string);
        }

        binding.infoIdTv.setText(String.valueOf(loan.getId()));

        String note = loan.getSpecialNote();
        if (!note.equals("")) {
            binding.infoNoteTv.setText(note);
        } else {
            binding.infoNoteTv.setText(R.string.not_specified_string);
        }

        String loanType = getString(R.string.not_specified_string);
        switch (loan.getType()) {

            case Loan.TYPE_IN:
                loanType = getString(R.string.loan_type_incoming);
                break;

            case Loan.TYPE_OUT:
                loanType = getString(R.string.loan_type_outgoing);
                break;

            case Loan.TYPE_ARCHIVED_IN:
                loanType = getString(R.string.loan_type_archived_in);
                loanIsArchived = true;
                displayDeleteIcon();
                break;

            case Loan.TYPE_ARCHIVED_OUT:
                loanType = getString(R.string.loan_type_archived_out);
                loanIsArchived = true;
                displayDeleteIcon();
                break;
        }
        binding.infoLoanTypeTv.setText(loanType);
    }

    private void displayDeleteIcon() {
        MenuItem deleteMnuItem = binding.loansInfoToolbar.getMenu().findItem(R.id.info_mnu_delete);
        if (deleteMnuItem != null) {
            deleteMnuItem.setIcon(R.drawable.ic_delete_mnu);
        }
    }


    private void shareLoan() {
        String msgToShare = LoanInfoHelper.composeShareText(viewedLoan);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, msgToShare);
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));
    }

    private void buildDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (viewedLoan.getType() == Loan.TYPE_ARCHIVED_IN || viewedLoan.getType() == Loan.TYPE_ARCHIVED_OUT) {
            builder.setMessage(R.string.arch_dialog_delete_msg)
                    .setPositiveButton(getString(R.string.arch_dialog_delete_positive),
                            (dialog, which) -> deleteLoan())
                    .setNegativeButton(getString(R.string.arch_dialog_delete_negative), (dialog, which) -> {});
        } else {
            builder.setMessage(R.string.in_dialog_delete_msg)
                    .setPositiveButton(getString(R.string.in_dialog_delete_positive),
                            (dialog, which) -> archiveLoan())
                    .setNegativeButton(getString(R.string.in_dialog_delete_negative), (dialog, which) -> {});
        }

        deleteDialog = builder.create();
    }

    private void showDeleteDialog() {
        if (deleteDialog == null) {
            buildDeleteDialog();
        }
        if (!deleteDialog.isShowing()) {
            deleteDialog.show();
        }
    }

    private void archiveLoan() {
        if (viewedLoan.getType() == Loan.TYPE_IN) {
            viewedLoan.setType(Loan.TYPE_ARCHIVED_IN);
        } else if (viewedLoan.getType() == Loan.TYPE_OUT){
            viewedLoan.setType(Loan.TYPE_ARCHIVED_OUT);
        }
        viewModel.update(viewedLoan);
        onBackPressed();
    }

    private void deleteLoan() {
        viewModel.delete(viewedLoan);
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.info_activity_menu, menu);
        if (loanIsArchived) {
            menu.findItem(R.id.info_mnu_delete).setIcon(R.drawable.ic_delete_mnu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.info_mnu_delete:
                showDeleteDialog();
                break;

            case R.id.info_mnu_share:
                shareLoan();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

















