package com.romanoindustries.loanmanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.romanoindustries.loanmanager.fragments.DatePickerFragment;
import com.romanoindustries.loanmanager.fragments.InterestFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class NewLoanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "NewLoanActivity";

    private InterestFragment interestFragment = new InterestFragment();

    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutPhone;
    private TextInputEditText editTextName;
    private TextInputEditText editTextPhone;
    private Button endDateBtn;
    private CheckBox enableInterestCb;
    private CheckBox noEndDateCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan);

        initViews();
    }

    private void initViews() {
        inputLayoutName = findViewById(R.id.text_input_name);
        inputLayoutPhone = findViewById(R.id.text_input_phone);
        editTextName = findViewById(R.id.edit_text_name);
        editTextPhone = findViewById(R.id.edit_text_phone);

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

        enableInterestCb = findViewById(R.id.enable_interest_cb);
        enableInterestCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            if (isChecked) {
                fragmentTransaction.add(R.id.new_loan_fragment_container, interestFragment);
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
}

















