package com.romanoindustries.loanmanager.notifications;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationPreferencesHelper {

    public static final String NOTIFICATION_PREFERENCES_NAME = "notification_preference";
    public static final String NOTIFICATION_MODE_KEY = "notification_mode";

    public static final int NOTIFICATION_MODE_NOT_SHOW = 0;
    public static final int NOTIFICATION_MODE_SHOW_ALL = 1;

    public static int getNotificationMode(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(NOTIFICATION_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(NOTIFICATION_MODE_KEY, NOTIFICATION_MODE_SHOW_ALL);
    }

    public static void setNotificationMode(Context context, int mode) {
        SharedPreferences preferences = context
                .getSharedPreferences(NOTIFICATION_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (mode == 0 || mode == 1) {
            editor.putInt(NOTIFICATION_MODE_KEY, mode);
            editor.apply();
        }
    }
}
