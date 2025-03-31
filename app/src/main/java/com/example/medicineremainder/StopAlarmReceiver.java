package com.example.medicineremainder;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Stop and release the media player
        if (NotificationReceiver.mediaPlayer != null) {
            NotificationReceiver.mediaPlayer.stop();
            NotificationReceiver.mediaPlayer.release();
            NotificationReceiver.mediaPlayer = null;
        }

        // Dismiss the notification
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(1);  // Cancel the notification with ID 1
        }
    }
}
