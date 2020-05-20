package com.romanoindustries.loanmanager.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.romanoindustries.loanmanager.MainActivity;
import com.romanoindustries.loanmanager.R;

public class NotificationHelper extends ContextWrapper {

    public static final String MAIN_CHANNEL_ID = "channel_main";
    public static final String MAIN_CHANNEL_NAME = "Main Channel";

    private NotificationManager notificationManager;

    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {
        NotificationChannel mainChannel = new NotificationChannel(
                MAIN_CHANNEL_ID,
                MAIN_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        mainChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(mainChannel);
    }

    public NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getMainChannelNotification(String title, String message) {

        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0);

        return new NotificationCompat.Builder(getApplicationContext(), MAIN_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setColor(getColor(R.color.colorPrimary))
                .setStyle(new NotificationCompat.BigTextStyle())
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_loan_notification);
    }
}
















