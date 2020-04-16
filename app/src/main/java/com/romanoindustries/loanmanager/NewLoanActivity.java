package com.romanoindustries.loanmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

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
    private Button endDateBtn;
    private CheckBox noEndDateCb;
    private CheckBox applyInterestCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan);

        Toolbar toolbar = findViewById(R.id.new_loan_toolbar);
        setSupportActionBar(toolbar);

        interestFragment = new InterestFragment();
        newLoanViewModel = new ViewModelProvider(this).get(NewLoanViewModel.class);
        handleViewModelChanges(newLoanViewModel);
        initViews();
    }

    private void initViews() {
        inputLayoutName = findViewById(R.id.text_input_name);
        editTextName = findViewById(R.id.edit_text_name);
        inputLayoutPhone = findViewById(R.id.text_input_phone);
        editTextPhone = findViewById(R.id.edit_text_phone);
        inputLayoutAmount = findViewById(R.id.text_input_amount);
        editTextAmount = findViewById(R.id.edit_text_amount);

        inputLayoutName.setEndIconOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(intent, RESULT_FIRST_USER);
        });

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
        });

        noEndDateCb = findViewById(R.id.no_end_date_cb);
        noEndDateCb.setOnCheckedChangeListener((buttonView, isChecked) -> endDateBtn.setEnabled(!isChecked));
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
        newLoanViewModel.getPaymentDateInMs().observe(this, aLong -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(aLong);
            String dateString = DateFormat.getDateInstance().format(calendar.getTime());
            endDateBtn.setText(dateString);
        });
        newLoanViewModel.getNoEndDate().observe(this, aBoolean -> noEndDateCb.setChecked(aBoolean));
        newLoanViewModel.getApplyInterestRate().observe(this, aBoolean -> applyInterestCb.setChecked(aBoolean));
        newLoanViewModel.getWholeInterestPercent().observe(this, integer -> interestFragment.setWholePercent(integer));
        newLoanViewModel.getDecimalInterestPercent().observe(this, integer -> interestFragment.setDecimalPercent(integer));
        newLoanViewModel.getPeriodInDays().observe(this, integer -> interestFragment.setLoanPeriodInDays(integer));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_loan_activity_menu, menu);
        return true;
    }
}

















