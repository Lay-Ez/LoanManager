package com.romanoindustries.loanmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.romanoindustries.loanmanager.alertreceiver.AlarmScheduler;
import com.romanoindustries.loanmanager.archivedloans.ArchivedLoansFragment;
import com.romanoindustries.loanmanager.currency.CurrencyHelper;
import com.romanoindustries.loanmanager.fragments.IncomingLoansFragment;
import com.romanoindustries.loanmanager.fragments.OutgoingLoansFragment;
import com.romanoindustries.loanmanager.viewmodels.LoansViewModel;

import java.text.NumberFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static final String IN_FRAGMENT_TAG = "in_fragment";

    public static final String OUT_FRAGMENT_TAG = "out_fragment";

    public static final String ARCH_FRAGMENT_TAG = "arch_fragment";

    public static final String CURRENT_FRAGMENT_KEY = "current_fragment";
    private String currentFragmentTag = IN_FRAGMENT_TAG;

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
        setCorrectCurrency();
    }

    private void restoreViewedFragment(Bundle bundle) {
        if (bundle == null) {
            showFragment(IN_FRAGMENT_TAG);
            return;
        }

        String viewFragmentTag = bundle.getString(CURRENT_FRAGMENT_KEY, IN_FRAGMENT_TAG);
        showFragment(viewFragmentTag);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(CURRENT_FRAGMENT_KEY, currentFragmentTag);
        super.onSaveInstanceState(outState);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {

                switch (item.getItemId()) {

                    case R.id.nav_loans_in:
                        showFragment(IN_FRAGMENT_TAG);
                        break;

                    case R.id.nav_loans_out:
                        showFragment(OUT_FRAGMENT_TAG);
                        break;

                    case R.id.nav_loans_history:
                        showFragment(ARCH_FRAGMENT_TAG);
                        break;
                }

                return true;
            };

    private void showFragment(String fragmentTag) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment inFragment;
        Fragment outFragment;
        Fragment archFragment;

        switch (fragmentTag) {

            case IN_FRAGMENT_TAG:

                inFragment = manager.findFragmentByTag(IN_FRAGMENT_TAG);
                if (inFragment != null) {
                    transaction.show(inFragment);
                } else {
                    transaction.add(R.id.fragment_container, new IncomingLoansFragment(), IN_FRAGMENT_TAG);
                }

                outFragment = manager.findFragmentByTag(OUT_FRAGMENT_TAG);
                if (outFragment != null) {
                    transaction.hide(outFragment);
                }

                archFragment = manager.findFragmentByTag(ARCH_FRAGMENT_TAG);
                if (archFragment != null) {
                    transaction.hide(archFragment);
                }
                break;

            case OUT_FRAGMENT_TAG:

                outFragment = manager.findFragmentByTag(OUT_FRAGMENT_TAG);
                if (outFragment != null) {
                    transaction.show(outFragment);
                } else {
                    transaction.add(R.id.fragment_container, new OutgoingLoansFragment(), OUT_FRAGMENT_TAG);
                }

                inFragment = manager.findFragmentByTag(IN_FRAGMENT_TAG);
                if (inFragment != null) {
                    transaction.hide(inFragment);
                }

                archFragment = manager.findFragmentByTag(ARCH_FRAGMENT_TAG);
                if (archFragment != null) {
                    transaction.hide(archFragment);
                }
                break;

            case ARCH_FRAGMENT_TAG:

                archFragment = manager.findFragmentByTag(ARCH_FRAGMENT_TAG);
                if (archFragment != null) {
                    transaction.show(archFragment);
                } else {
                    transaction.add(R.id.fragment_container, new ArchivedLoansFragment(), ARCH_FRAGMENT_TAG);
                }

                inFragment = manager.findFragmentByTag(IN_FRAGMENT_TAG);
                if (inFragment != null) {
                    transaction.hide(inFragment);
                }

                outFragment = manager.findFragmentByTag(OUT_FRAGMENT_TAG);
                if (outFragment != null) {
                    transaction.hide(outFragment);
                }
                break;
        }
        transaction.commit();
        currentFragmentTag = fragmentTag;
    }

    private void setCorrectCurrency() {
        if (currencyNotSet()) {
            String country = Locale.getDefault().getCountry();
            if (country.equals("RU")) {
                CurrencyHelper.setCurrency(this, CurrencyHelper.RUB);
            } else if (country.equals("IN")) {
                CurrencyHelper.setCurrency(this, CurrencyHelper.INR);
            } else {
                CurrencyHelper.setCurrency(this, CurrencyHelper.USD);
            }
        }
    }

    private boolean currencyNotSet() {
        SharedPreferences preferences = getSharedPreferences(
                        CurrencyHelper.CURRENCY_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
        return  (preferences.getInt(CurrencyHelper.CURRENCY_PREFERENCE_KEY, 0) == 0);
    }

    private void startAlarm() {
        AlarmScheduler.scheduleAlarm(this);
    }

    public static String formatAmount(long amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount).trim();
    }
}


