package com.romanoindustries.loanmanager.newloan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.currency.CurrencyHelper;
import com.romanoindustries.loanmanager.datamodel.Loan;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class NewLoanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    public static final String LOAN_TYPE_KEY = "new_loan_type_key";

    private NewLoanInterestFragment newLoanInterestFragment;
    private NewLoanViewModel newLoanViewModel;
    DialogFragment datePicker = new DatePickerFragment();
    AlertDialog confirmCancelDialog;
    AlertDialog zeroRateErrorDialog;

    private TextInputLayout inputLayoutName;
    private TextInputEditText editTextName;
    private TextInputEditText editTextPhone;
    private TextInputLayout inputLayoutAmount;
    private TextInputEditText editTextAmount;
    private TextInputEditText editTextNote;
    private Button endDateBtn;
    private CheckBox noEndDateCb;
    private CheckBox applyInterestCb;
    private FrameLayout interestFragmentContainer;

    private float initialYPositionOfInterestFragment = 0.0f; /* needed for correct animation */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan);

        Toolbar toolbar = findViewById(R.id.new_loan_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        newLoanInterestFragment = new NewLoanInterestFragment();
        newLoanViewModel = new ViewModelProvider(this).get(NewLoanViewModel.class);
        parseIntent(getIntent());
        buildDialogs();
        initViews();
        handleViewModelChanges(newLoanViewModel);
    }

    private void initViews() {
        inputLayoutName = findViewById(R.id.text_input_name);
        editTextName = findViewById(R.id.edit_text_name);
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { inputLayoutName.setError(null);}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextPhone = findViewById(R.id.edit_text_phone);
        inputLayoutAmount = findViewById(R.id.text_input_amount);
        inputLayoutAmount.setStartIconDrawable(CurrencyHelper.getCurrencyIconId(this));
        editTextAmount = findViewById(R.id.edit_text_amount);
        editTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { inputLayoutAmount.setError(null);}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        inputLayoutName.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, RESULT_FIRST_USER);
        });

        editTextNote = findViewById(R.id.edit_text_note);

        endDateBtn = findViewById(R.id.end_date_btn);
        endDateBtn.setOnClickListener(v -> {
            hideKeyboard();
            if (!datePicker.isAdded()) {
                datePicker.show(getSupportFragmentManager(), "date_picker");
            }
        });

        interestFragmentContainer = findViewById(R.id.new_loan_fragment_container);
        applyInterestCb = findViewById(R.id.enable_interest_cb);
        applyInterestCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (initialYPositionOfInterestFragment == 0.0f) {
                initialYPositionOfInterestFragment = interestFragmentContainer.getY();
                if (initialYPositionOfInterestFragment == 0.0f && isChecked) {
                    /* to avoid view jumping to the top */
                    interestFragmentContainer.setVisibility(View.VISIBLE);
                    return;
                }
            }
            float offset = 160.0f;
            hideKeyboard();
            if (isChecked) {
                interestFragmentContainer.setY(initialYPositionOfInterestFragment + offset);
                interestFragmentContainer.setAlpha(0.0f);
                interestFragmentContainer.setVisibility(View.VISIBLE);
                interestFragmentContainer.animate()
                        .yBy(-offset)
                        .alpha(1.0f)
                        .setDuration(300L)
                        .start();
            } else {
                interestFragmentContainer.animate()
                        .yBy(offset)
                        .setDuration(300L)
                        .alpha(0.0f)
                        .withEndAction(() -> interestFragmentContainer.setVisibility(View.INVISIBLE))
                        .start();

            }
            newLoanViewModel.setApplyInterestRate(isChecked);
        });
        if (applyInterestCb.isChecked()) {
            interestFragmentContainer.setVisibility(View.VISIBLE);
        } else {
            interestFragmentContainer.setVisibility(View.INVISIBLE);
        }
        addInterestFragment();

        noEndDateCb = findViewById(R.id.no_end_date_cb);
        noEndDateCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            hideKeyboard();
            endDateBtn.setEnabled(!isChecked);
            newLoanViewModel.setNoEndDate(isChecked);});
    }

    private void addInterestFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.new_loan_fragment_container, newLoanInterestFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER && resultCode == RESULT_OK) {
            Uri contactUri;
            if (data != null) {
                contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
                Cursor cursor = null;
                if (contactUri != null) {
                    cursor = getContentResolver().query(contactUri,
                            projection,
                            null,
                            null,
                            null);
                }
                if (cursor != null) {
                    parseContactInfo(cursor);
                }
            }
        }
    }

    private void parseContactInfo(Cursor cursor) {
        if (cursor.moveToFirst()) {
            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            String number = cursor.getString(numberColumnIndex);
            String name = cursor.getString(nameColumnIndex);

            editTextPhone.setText(number);
            editTextName.setText(name);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        newLoanViewModel.setPaymentDateInMs(calendar.getTimeInMillis());
        String dateString = DateFormat.getDateInstance().format(calendar.getTime());
        endDateBtn.setText(dateString);

    }
    
    private void handleViewModelChanges(NewLoanViewModel newLoanViewModel) {
        newLoanViewModel.getName().observe(this, s -> editTextName.setText(s));
        newLoanViewModel.getPhone().observe(this, s -> editTextPhone.setText(s));
        newLoanViewModel.getAmount().observe(this, integer -> {
            if (integer != null) {
                editTextAmount.setText(String.valueOf(integer));
            }
        });
        newLoanViewModel.getNote().observe(this, s -> editTextNote.setText(s));

        newLoanViewModel.getPaymentDateInMs().observe(this, aLong -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(aLong);
            String dateString = DateFormat.getDateInstance().format(calendar.getTime());
            endDateBtn.setText(dateString);
        });
        newLoanViewModel.getNoEndDate().observe(this, aBoolean -> noEndDateCb.setChecked(aBoolean));
        newLoanViewModel.getApplyInterestRate().observe(this, aBoolean -> applyInterestCb.setChecked(aBoolean));

    }

    private void onLoanSavePressed() {
        if (checkNameAmountFields() && checkInterestRate()) {
            saveTextFieldsToViewModel();
            NewLoanVmHelper helper = new NewLoanVmHelper();
            Loan loanToSave = helper.composeLoanFromVm(newLoanViewModel);
            LoansViewModel loansViewModel = new ViewModelProvider
                    .AndroidViewModelFactory(getApplication())
                    .create(LoansViewModel.class);
            loansViewModel.insert(loanToSave);
            onBackPressed();
        }
    }

    private void onCancelLoanPressed() {
        showConfirmCancelDialog();
    }

    private void saveTextFieldsToViewModel() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();
        int amount = Integer.parseInt(amountStr);
        String note = editTextNote.getText().toString().trim();
        newLoanViewModel.setName(name);
        newLoanViewModel.setPhone(phone);
        newLoanViewModel.setAmount(amount);
        newLoanViewModel.setNote(note);
    }

    private boolean checkInterestRate() {
        boolean isInterestOk = true;
        if (newLoanViewModel.getApplyInterestRate().getValue()) {
            int wholeInterestPercent = newLoanViewModel.getWholeInterestPercent().getValue();
            int decimalInterestPercent = newLoanViewModel.getDecimalInterestPercent().getValue();
            if (wholeInterestPercent == 0 && decimalInterestPercent == 0) {
                showInterestRateError();
                isInterestOk = false;
            }
        }
        return isInterestOk;
    }

    private boolean checkDateField() {
        boolean isDateOk = true;
        if (!newLoanViewModel.getNoEndDate().getValue()) {
            long setDate = newLoanViewModel.getPaymentDateInMs().getValue();
            long currentTimeInMs = System.currentTimeMillis();
        }
        return true;
    }

    private boolean checkNameAmountFields() {
        boolean isInputOk = true;
        String name = editTextName.getText().toString().trim();
        String amountAsString = editTextAmount.getText().toString();


        if (name.isEmpty()) {
            inputLayoutName.setError(getString(R.string.name_cannot_be_empty_error_msg));
            isInputOk = false;
        }

        if (amountAsString.isEmpty()) {
            inputLayoutAmount.setError(getString(R.string.amount_should_be_specified_error_msg));
            isInputOk = false;
        } else {
            int amount = Integer.parseInt(amountAsString);
            if (amount == 0) {
                inputLayoutAmount.setError(getString(R.string.amount_cannot_be_zero_error_msg));
                isInputOk = false;
            }
        }

        return isInputOk;
    }

    private void buildDialogs() {
        AlertDialog.Builder cancelDialogBuilder = new AlertDialog.Builder(this);
        cancelDialogBuilder.setMessage(R.string.confirm_cancel_dialog_msg)
                .setPositiveButton(R.string.confirm_cancel_dialog_positive, (dialog, which) -> onBackPressed())
                .setNegativeButton(R.string.confirm_cancel_dialog_negative, ((dialog, which) -> {}));
        confirmCancelDialog = cancelDialogBuilder.create();

        AlertDialog.Builder zeroRateDialogBuilder = new AlertDialog.Builder(this);
        zeroRateDialogBuilder.setMessage(R.string.interest_rate_zero_msg)
                .setPositiveButton("OK", (dialog, which) -> {});
        zeroRateErrorDialog = zeroRateDialogBuilder.create();
    }

    private void showConfirmCancelDialog() {
        if (!confirmCancelDialog.isShowing()) {
            confirmCancelDialog.show();
        }
    }

    private void showInterestRateError() {
        if (!zeroRateErrorDialog.isShowing()) {
            zeroRateErrorDialog.show();
        }
    }

    private void parseIntent(Intent intent) {
        int loanType = intent.getIntExtra(LOAN_TYPE_KEY, 0);
        newLoanViewModel.setLoanType(loanType);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_loan_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.mnu_item_save:
                onLoanSavePressed();
                return true;

            case R.id.mnu_item_cancel:
                onCancelLoanPressed();
                return true;

                default:
                    return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}

















