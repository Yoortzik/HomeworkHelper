package com.example.homeworkhelper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID   = "homework_channel";
    private static final String CHANNEL_NAME = "עוזר שיעורי בית";
    private static final int    NOTIF_ID     = 1001;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context             = context;
        this.notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        createChannel();
    }

    // יוצר את ה-Channel (חובה באנדרואיד 8+)
    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("התראות כשתשובת ה-AI מוכנה");
            notificationManager.createNotificationChannel(channel);
        }
    }

    // שולח התראה עם המקצוע שזוהה
    public void sendAnswerReadyNotification(String subject) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("התשובה מוכנה! 📚")
                .setContentText("קיבלת עזרה ב: " + subject)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); // נעלמת כשלוחצים עליה

        notificationManager.notify(NOTIF_ID, builder.build());
    }
}
