package com.romanoindustries.loanmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.romanoindustries.loanmanager.fragments.DatePickerFragment;
import com.romanoindustries.loanmanager.fragments.InterestFragment;
import com.romanoindustries.loanmanager.viewmodels.NewLoanViewModel;

import java.text.DateFormat;
import java.util.Calendar;

public class NewLoanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private static final String TAG = "NewLoanActivity";

    private InterestFragment interestFragment;
    private NewLoanViewModel newLoanViewModel;

    private TextInputLayout inputLayoutName;
    private TextInputEditText editTextName;
    private TextInputLayout inputLayoutPhone;
    private TextInputEditText editTextPhone;
    private TextInputLayout inputLayoutAmount;
    private TextInputEditText editTextAmount;
    private TextInputLayout inputLayoutNote;
    private TextInputEditText editTextNote;
    private Button endDateBtn;
    private CheckBox noEndDateCb;
    private CheckBox applyInterestCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan);

        Toolbar toolbar = findViewById(R.id.new_loan_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        interestFragment = new InterestFragment();
        newLoanViewModel = new ViewModelProvider(this).get(NewLoanViewModel.class);
        handleViewModelChanges(newLoanViewModel);
        initViews();
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

        inputLayoutPhone = findViewById(R.id.text_input_phone);
        editTextPhone = findViewById(R.id.edit_text_phone);
        inputLayoutAmount = findViewById(R.id.text_input_amount);
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
            startActivityForResult(intent, RESULT_FIRST_USER);
        });

        inputLayoutNote = findViewById(R.id.text_input_note);
        editTextNote = findViewById(R.id.edit_text_note);

        endDateBtn = findViewById(R.id.end_date_btn);
        endDateBtn.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date_picker");
        });

        applyInterestCb = findViewById(R.id.enable_interest_cb);
        applyInterestCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (isChecked) {
                fragmentTransaction.replace(R.id.new_loan_fragment_container, interestFragment);
                fragmentTransaction.commit();
            } else {
                fragmentTransaction.remove(interestFragment);
                fragmentTransaction.commit();
            }
            newLoanViewModel.setApplyInterestRate(isChecked);
        });

        noEndDateCb = findViewById(R.id.no_end_date_cb);
        noEndDateCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                endDateBtn.setEnabled(!isChecked);
                newLoanViewModel.setNoEndDate(isChecked);});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(contactUri,
                    projection,
                    null,
                    null,
                    null);
            parseContactInfo(cursor);
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
        if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            newLoanViewModel.setPaymentDateInMs(calendar.getTimeInMillis());
            String dateString = DateFormat.getDateInstance().format(calendar.getTime());
            endDateBtn.setText(dateString);
        } else {
            showWrongDateDialog();
        }
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

    private void saveLoan() {
        if (checkNameAmountFields() && checkDateField() && checkInterestRate()) {
            saveTextFieldsToViewModel();
        }
    }

    private void cancelLoan() {

    }

    private void saveTextFieldsToViewModel() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();
        int amount = Integer.parseInt(amountStr);
        String note = editTextNote.getText().toString().trim();
        Log.d(TAG, "saveTextFieldsToViewModel: saving fields name=" + name +
                " phone=" + phone +
                " amount=" + amount +
                " note=" + note);

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
            if (setDate < currentTimeInMs) {
                showWrongDateDialog();
                isDateOk = false;
            }
        }
        return isDateOk;
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

    private void showWrongDateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.date_error_dialog_msg)
                .setPositiveButton("OK", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showInterestRateError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.interest_rate_zero_msg)
                .setPositiveButton("OK", (dialog, which) -> {});
        AlertDialog dialog = builder.create();
        dialog.show();
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
                saveLoan();
                return true;

            case R.id.mnu_item_cancel:
                cancelLoan();
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
}

















