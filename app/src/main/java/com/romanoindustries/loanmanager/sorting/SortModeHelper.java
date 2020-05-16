package com.romanoindustries.loanmanager.sorting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.List;

public class SortModeHelper {

    private static final String TAG = "SortModeHelper";

    public static final int SORT_OLD_FIRST = 1;
    public static final int SORT_NEW_FIRST = 2;
    public static final int SORT_BIG_FIRST = 3;
    public static final int SORT_SMALL_FIRST = 4;

    public static final String SORT_PREFERENCE_KEY = "SORT_PREFERENCE";
    public static final String SORT_MODE_KEY = "SORT_MODE";


    public static int getSortMode(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(
                        SORT_PREFERENCE_KEY,
                        Context.MODE_PRIVATE);
        return preferences.getInt(SORT_MODE_KEY, SORT_OLD_FIRST);
    }

    public static void setSortMode(Context context, int sortMode) {
        SharedPreferences preferences = context
                .getSharedPreferences(
                        SORT_PREFERENCE_KEY,
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        if (sortMode>=1 && sortMode<=4) {
            editor.putInt(SORT_MODE_KEY, sortMode);
            editor.apply();
        } else {
            Log.e(TAG, "setSortMode: ", new IllegalArgumentException());
        }
    }

    public static void checkCorrectSortItem(Menu sortMenu, Context context) {
        int sortMode = SortModeHelper.getSortMode(context);

        switch (sortMode) {

            case SortModeHelper.SORT_OLD_FIRST:
                sortMenu.findItem(R.id.mnu_sort_item_old_first).setChecked(true);
                break;

            case SortModeHelper.SORT_NEW_FIRST:
                sortMenu.findItem(R.id.mnu_sort_item_new_first).setChecked(true);
                break;

            case SortModeHelper.SORT_BIG_FIRST:
                sortMenu.findItem(R.id.mnu_sort_item_big_first).setChecked(true);
                break;

            case SortModeHelper.SORT_SMALL_FIRST:
                sortMenu.findItem(R.id.mnu_sort_item_small_first).setChecked(true);
                break;
        }
    }

    public static void sortLoansAccordingly(int sortMode, List<Loan> loans) {
        if (loans == null || loans.isEmpty()) {
            return;
        }

        switch (sortMode) {

            case SortModeHelper.SORT_OLD_FIRST:
                loans.sort((loan1, loan2) -> Long.compare(loan1.getStartDateInMs(), loan2.getStartDateInMs()));
                break;

            case SortModeHelper.SORT_NEW_FIRST:
                loans.sort((loan1, loan2) -> Long.compare(loan2.getStartDateInMs(), loan1.getStartDateInMs()));
                break;

            case SortModeHelper.SORT_BIG_FIRST:
                loans.sort((loan1, loan2) -> loan2.getCurrentAmount() - loan1.getCurrentAmount());
                break;

            case SortModeHelper.SORT_SMALL_FIRST:
                loans.sort((loan1, loan2) -> loan1.getCurrentAmount() - loan2.getCurrentAmount());
                break;
        }
    }
}
