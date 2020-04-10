package com.romanoindustries.loanmanager;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.romanoindustries.loanmanager.incomingloans.IncomingLoansFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(navListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_loans_in:
                            selectedFragment = new IncomingLoansFragment();
                            break;

                        case R.id.nav_loans_out:
                            //
                            break;

                        case R.id.nav_loans_history:
                            //
                            break;


                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new IncomingLoansFragment()).commit();

                    return true;
                }
            };
}



















