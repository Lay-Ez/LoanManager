package com.romanoindustries.loanmanager.currency;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;

import com.romanoindustries.loanmanager.R;

public class CurrencyHelper {

    public static final int UNKNOWN = 0;
    public static final int USD = 1;
    public static final int RUB = 2;
    public static final int EUR = 3;
    public static final int CAD = 4;
    public static final int AUD = 5;
    public static final int INR = 6;

    public static final String CURRENCY_PREFERENCE_NAME = "preferred_currency";
    public static final String CURRENCY_PREFERENCE_KEY = "preferred_currency_key";

    public static int getCurrency(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(
                        CURRENCY_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
        return preferences.getInt(CURRENCY_PREFERENCE_KEY, USD);
    }

    public static void setCurrency(Context context, int currency) {
        SharedPreferences preferences = context
                .getSharedPreferences(
                        CURRENCY_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (currency > 0 && currency <=6 ) {
            editor.putInt(CURRENCY_PREFERENCE_KEY, currency);
            editor.apply();
        }
    }

    public static void checkCorrectCurrencyItem(Menu menu, Context context) {
        int currency = getCurrency(context);

        switch (currency) {

            case CurrencyHelper.USD:
                menu.findItem(R.id.usd_mnu_item).setChecked(true);
                break;

            case CurrencyHelper.RUB:
                menu.findItem(R.id.rub_mnu_item).setChecked(true);
                break;

            case CurrencyHelper.EUR:
                menu.findItem(R.id.eur_mnu_item).setChecked(true);
                break;

            case CurrencyHelper.CAD:
                menu.findItem(R.id.cad_mnu_item).setChecked(true);
                break;

            case CurrencyHelper.AUD:
                menu.findItem(R.id.aud_mnu_item).setChecked(true);
                break;

            case CurrencyHelper.INR:
                menu.findItem(R.id.inr_mnu_item).setChecked(true);
                break;

        }

    }

}
