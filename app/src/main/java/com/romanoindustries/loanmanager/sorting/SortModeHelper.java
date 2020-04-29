package com.romanoindustries.loanmanager.sorting;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;

import com.romanoindustries.loanmanager.R;

public class SortModeHelper {
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
}
