package com.romanoindustries.loanmanager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class NewLoanActivity extends AppCompatActivity {
    private static final String TAG = "NewLoanActivity";

    private TextInputLayout inputLayoutName;
    private TextInputLayout inputLayoutPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_loan);

        initViews();
    }

    private void initViews() {
        inputLayoutName = findViewById(R.id.text_input_name);
        inputLayoutPhone = findViewById(R.id.text_input_phone);

        inputLayoutName.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, RESULT_FIRST_USER);
            }
        });
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

            Log.d(TAG, "parseContactInfo: name=" + name + " phone=" + number);
        }
    }
}

















