package com.romanoindustries.loanmanager;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.romanoindustries.loanmanager.alertreceiver.AlarmScheduler;
import com.romanoindustries.loanmanager.archivedloans.ArchivedLoansFragment;
import com.romanoindustries.loanmanager.fragments.IncomingLoansFragment;
import com.romanoindustries.loanmanager.fragments.OutgoingLoansFragment;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private IncomingLoansFragment incomingLoansFragment;
    private static final int IN_FRAGMENT_ID = 1;

    private OutgoingLoansFragment outgoingLoansFragment;
    private static final int OUT_FRAGMENT_ID = 2;

    private ArchivedLoansFragment archivedLoansFragment;
    private static final int ARCH_FRAGMENT_ID = 3;

    public static final String CURRENT_FRAGMENT_KEY = "current_fragment";
    private int currentFragmentId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        incomingLoansFragment = new IncomingLoansFragment();
        outgoingLoansFragment = new OutgoingLoansFragment();
        archivedLoansFragment = new ArchivedLoansFragment();

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(navListener);

        restoreViewedFragment(savedInstanceState);
        startAlarm();
    }

    private void restoreViewedFragment(Bundle bundle) {
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, incomingLoansFragment).commit();
            return;
        }

        int viewFragmentId = bundle.getInt(CURRENT_FRAGMENT_KEY, IN_FRAGMENT_ID);

        if (viewFragmentId == IN_FRAGMENT_ID) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, incomingLoansFragment).commit();
        } else if (viewFragmentId == OUT_FRAGMENT_ID) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, outgoingLoansFragment).commit();
        } else if (viewFragmentId == ARCH_FRAGMENT_ID){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, archivedLoansFragment).commit();
        }
        currentFragmentId = viewFragmentId;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(CURRENT_FRAGMENT_KEY, currentFragmentId);
        super.onSaveInstanceState(outState);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {

                        case R.id.nav_loans_in:
                            selectedFragment = incomingLoansFragment;
                            currentFragmentId = IN_FRAGMENT_ID;
                            break;

                        case R.id.nav_loans_out:
                            selectedFragment = outgoingLoansFragment;
                            currentFragmentId = OUT_FRAGMENT_ID;
                            break;

                        case R.id.nav_loans_history:
                            selectedFragment = archivedLoansFragment;
                            currentFragmentId = ARCH_FRAGMENT_ID;
                            break;
                    }
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };

    private void startAlarm() {
        AlarmScheduler.scheduleAlarm(this);
    }

    public static String formatAmount(long amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount).trim();
    }
}


