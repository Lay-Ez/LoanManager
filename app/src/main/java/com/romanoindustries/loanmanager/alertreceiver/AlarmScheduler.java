package com.romanoindustries.loanmanager.alertreceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmScheduler {

    public static void scheduleAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 20);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
