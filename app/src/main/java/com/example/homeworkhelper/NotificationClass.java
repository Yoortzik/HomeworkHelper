package com.example.homeworkhelper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationClass {

    private static final String TAG = "HomeworkNotifications";

    private static final String CHANNEL_ID = "hw_channel";
    private static final String CHANNEL_NAME = "Homework Helper";

    private static final int ANSWER_NOTIFICATION_ID = 2001;

    private Context appContext;
    private NotificationManager manager;

    public NotificationClass(Context context) {
        appContext = context;
        manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        setupNotificationChannel();
    }

    private void setupNotificationChannel() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel homeworkChannel =
                new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT
                );

        homeworkChannel.setDescription("Shows alerts when homework answers are ready");

        manager.createNotificationChannel(homeworkChannel);

        Log.d(TAG, "Notification channel created");
    }

    public void showFinishedAnswerNotification(String subjectName) {

        String message;

        if (subjectName == null || subjectName.trim().isEmpty()) {
            message = "Your homework answer is ready";
        } else {
            message = "New answer added for " + subjectName;
        }

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(appContext, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Homework Helper")
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);

        manager.notify(ANSWER_NOTIFICATION_ID, notification.build());

        Log.d(TAG, "Notification sent");
    }
}