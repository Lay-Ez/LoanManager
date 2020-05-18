package com.romanoindustries.loanmanager;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.romanoindustries.loanmanager.alertreceiver.AlarmScheduler;
import com.romanoindustries.loanmanager.archivedloans.ArchivedLoansFragment;
import com.romanoindustries.loanmanager.fragments.IncomingLoansFragment;
import com.romanoindustries.loanmanager.fragments.OutgoingLoansFragment;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int IN_FRAGMENT_ID = 1;
    public static final String IN_FRAGMENT_TAG = "in_fragment";

    private static final int OUT_FRAGMENT_ID = 2;
    public static final String OUT_FRAGMENT_TAG = "out_fragment";

    private static final int ARCH_FRAGMENT_ID = 3;
    public static final String ARCH_FRAGMENT_TAG = "arch_fragment";

    public static final String CURRENT_FRAGMENT_KEY = "current_fragment";
    private int currentFragmentId = 1;

    public LoansViewModel loansViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loansViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getApplication())
                .create(LoansViewModel.class);

        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(navListener);

        restoreViewedFragment(savedInstanceState);
        startAlarm();
    }

    private void restoreViewedFragment(Bundle bundle) {
        if (bundle == null) {
            showFragment(IN_FRAGMENT_ID);
            return;
        }

        int viewFragmentId = bundle.getInt(CURRENT_FRAGMENT_KEY, IN_FRAGMENT_ID);
        showFragment(viewFragmentId);
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

                    switch (item.getItemId()) {

                        case R.id.nav_loans_in:
                            showFragment(IN_FRAGMENT_ID);
                            break;

                        case R.id.nav_loans_out:
                            showFragment(OUT_FRAGMENT_ID);
                            break;

                        case R.id.nav_loans_history:
                            showFragment(ARCH_FRAGMENT_ID);
                            break;
                    }

                    return true;
                }
            };

    private void showFragment(int fragmentId) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        switch (fragmentId) {

            case IN_FRAGMENT_ID:

                if (manager.findFragmentByTag(IN_FRAGMENT_TAG) != null) {
                    transaction.show(manager.findFragmentByTag(IN_FRAGMENT_TAG));
                } else {
                    transaction.add(R.id.fragment_container, new IncomingLoansFragment(), IN_FRAGMENT_TAG);
                }

                if (manager.findFragmentByTag(OUT_FRAGMENT_TAG) != null) {
                    transaction.hide(manager.findFragmentByTag(OUT_FRAGMENT_TAG));
                }
                if (manager.findFragmentByTag(ARCH_FRAGMENT_TAG) != null) {
                    transaction.hide(manager.findFragmentByTag(ARCH_FRAGMENT_TAG));
                }
                break;

            case OUT_FRAGMENT_ID:

                if (manager.findFragmentByTag(OUT_FRAGMENT_TAG) != null) {
                    transaction.show(manager.findFragmentByTag(OUT_FRAGMENT_TAG));
                } else {
                    transaction.add(R.id.fragment_container, new OutgoingLoansFragment(), OUT_FRAGMENT_TAG);
                }

                if (manager.findFragmentByTag(IN_FRAGMENT_TAG) != null) {
                    transaction.hide(manager.findFragmentByTag(IN_FRAGMENT_TAG));
                }
                if (manager.findFragmentByTag(ARCH_FRAGMENT_TAG) != null) {
                    transaction.hide(manager.findFragmentByTag(ARCH_FRAGMENT_TAG));
                }
                break;

            case ARCH_FRAGMENT_ID:

                if (manager.findFragmentByTag(ARCH_FRAGMENT_TAG) != null) {
                    transaction.show(manager.findFragmentByTag(ARCH_FRAGMENT_TAG));
                } else {
                    transaction.add(R.id.fragment_container, new ArchivedLoansFragment(), ARCH_FRAGMENT_TAG);
                }

                if (manager.findFragmentByTag(IN_FRAGMENT_TAG) != null) {
                    transaction.hide(manager.findFragmentByTag(IN_FRAGMENT_TAG));
                }
                if (manager.findFragmentByTag(OUT_FRAGMENT_TAG) != null) {
                    transaction.hide(manager.findFragmentByTag(OUT_FRAGMENT_TAG));
                }
                break;
        }
        transaction.commit();
        currentFragmentId = fragmentId;
    }

    private void startAlarm() {
        AlarmScheduler.scheduleAlarm(this);
    }

    public static String formatAmount(long amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount).trim();
    }
}


