package com.romanoindustries.loanmanager.sorting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

import com.romanoindustries.loanmanager.R;
import com.romanoindustries.loanmanager.datamodel.Loan;

import java.util.List;

public class SortModeHelper {

    // TODO: 29.04.2020 add sorting class for list of loans

    private static final String TAG = "SortModeHelper";

    public static final int SORT_OLD_FIRST = 1;
    public static final int SORT_NEW_FIRST = 2;
    public static final int SORT_BIG_FIRST = 3;
    public static final int SORT_SMALL_FIRST = 4;


    public static int getSortMode(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(
                        context.getString(R.string.sort_preference_key),
                        Context.MODE_PRIVATE);
        return preferences.getInt(context.getString(R.string.sort_mode_key), SORT_OLD_FIRST);
    }

    public static void setSortMode(Context context, int sortMode) {
        SharedPreferences preferences = context
                .getSharedPreferences(
                        context.getString(R.string.sort_preference_key),
                        Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        if (sortMode>=1 && sortMode<=4) {
            editor.putInt(context.getString(R.string.sort_mode_key), sortMode);
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

    public static void sortLoansAccordingly(Context context, List<Loan> loans) {
        int sortMode = getSortMode(context);

        switch (sortMode) {

            case SortModeHelper.SORT_OLD_FIRST:
                loans.sort((loan1, loan2) -> {
                    if (loan1.getStartDateInMs() > loan2.getStartDateInMs()) {
                        return 1;
                    } else if (loan1.getStartDateInMs() < loan2.getStartDateInMs()) {
                        return  -1;
                    } else {
                        return 0;
                    }
                });
                break;

            case SortModeHelper.SORT_NEW_FIRST:
                loans.sort((loan1, loan2) -> {
                    if (loan1.getStartDateInMs() < loan2.getStartDateInMs()) {
                        return 1;
                    } else if (loan1.getStartDateInMs() > loan2.getStartDateInMs()) {
                        return  -1;
                    } else {
                        return 0;
                    }
                });
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
