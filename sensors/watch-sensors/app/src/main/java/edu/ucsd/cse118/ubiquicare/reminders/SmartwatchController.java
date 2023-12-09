package edu.ucsd.cse118.ubiquicare.reminders;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.app.NotificationChannel;

import edu.ucsd.cse118.ubiquicare.MainActivity;

public class SmartwatchController {
    private final Context context;
    private static final int NOTIFICATION_ID = 1;

    public SmartwatchController(Context context) {
        this.context = context;
    }

    // Method to trigger vibration on the smartwatch
    public void vibrateSmartwatch() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    // Method to display a pop-up notification with text
    public void showNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo and higher
        String channelId = "channel_reminder_1";
        CharSequence channelName = "Channel For Reminders";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder;
        builder = new Notification.Builder(context);
        builder.setVisibility(Notification.VISIBILITY_PUBLIC);


        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("New Reminder")
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}

