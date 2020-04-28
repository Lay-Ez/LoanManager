package com.romanoindustries.loanmanager.alertreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmScheduler {
    private static final String TAG = "AlarmScheduler";

    private static final int ALARM_HOUR_OF_DAY = 21;

    public static void scheduleAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, ALARM_HOUR_OF_DAY);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.getTimeInMillis() < currentTime) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Log.d(TAG, "scheduleAlarm: scheduling alarm tomorrow with time = " + calendar.getTimeInMillis());
        } else {
            Log.d(TAG, "scheduleAlarm: scheduling alarm today with time = " + calendar.getTimeInMillis());
        }
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        }
    }

}
