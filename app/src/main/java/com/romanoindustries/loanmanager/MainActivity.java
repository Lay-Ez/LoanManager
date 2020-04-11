package com.romanoindustries.loanmanager;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.romanoindustries.loanmanager.fragments.ArchivedLoansFragment;
import com.romanoindustries.loanmanager.fragments.IncomingLoansFragment;
import com.romanoindustries.loanmanager.fragments.OutgoingLoansFragment;

public class MainActivity extends AppCompatActivity {

    private IncomingLoansFragment incomingLoansFragment;
    private OutgoingLoansFragment outgoingLoansFragment;
    private ArchivedLoansFragment archivedLoansFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        incomingLoansFragment = new IncomingLoansFragment();
        outgoingLoansFragment = new OutgoingLoansFragment();
        archivedLoansFragment = new ArchivedLoansFragment();

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, incomingLoansFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_loans_in:
                            selectedFragment = incomingLoansFragment;
                            break;

                        case R.id.nav_loans_out:
                            selectedFragment = outgoingLoansFragment;
                            break;

                        case R.id.nav_loans_history:
                            selectedFragment = archivedLoansFragment;
                            break;


                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };
}



















