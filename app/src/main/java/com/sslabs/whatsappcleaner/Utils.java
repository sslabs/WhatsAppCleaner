package com.sslabs.whatsappcleaner;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;
import java.util.GregorianCalendar;

public enum Utils {
    INSTANCE;

    private PendingIntent mSchedulePendingIntent;

    public void setSchedulePendingIntent(PendingIntent schedulePendingIntent) {
        mSchedulePendingIntent = schedulePendingIntent;
    }

    public void scheduleCleanup(Context context, int hour, int minute) {
        if (mSchedulePendingIntent == null) throw new IllegalStateException("Pending intent not set yet");

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                mSchedulePendingIntent);
    }

    public void cancelScheduledCleanup(Context context) {
        if (mSchedulePendingIntent == null) throw new IllegalStateException("Pending intent not set yet");

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(mSchedulePendingIntent);
    }

    public boolean checkStoragePermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void sendNotification(Context context, int notificationId, String text) {
        Utils.INSTANCE.sendNotification(context, notificationId,
                text, null, android.R.drawable.stat_notify_sdcard);
    }

    public void sendNotification(Context context, int notificationId, String text, String subText) {
        Utils.INSTANCE.sendNotification(context, notificationId, text,
                subText, android.R.drawable.stat_notify_sdcard);
    }

    public void sendNotification(Context context, int notificationId, String text, int smallIconID) {
        Utils.INSTANCE.sendNotification(context, notificationId, text, null, smallIconID);
    }

    public void sendNotification(Context context,
                                 int notificationId,
                                 String text,
                                 String subText,
                                 int smallIconID) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        Utils.INSTANCE.sendNotification(context, notificationId, intent, text, subText, smallIconID);
    }

    public void sendNotification(Context context,
                                 int notificationId,
                                 Intent notificationIntent,
                                 String text,
                                 String subText,
                                 int smallIconID) {
        if (context == null) throw new IllegalArgumentException("context required");
        if (text == null) throw new IllegalArgumentException("text required");

        Intent deleteNotificationIntent = new Intent(context, CleanerReceiver.class);
        deleteNotificationIntent.setAction(CleanerReceiver.ACTION_NOTIFICATION_DISMISSED);
        PendingIntent deletePendingIntent = PendingIntent.
                getBroadcast(context, 0, deleteNotificationIntent, 0);
        PendingIntent contentPendingIntent = PendingIntent.
                getActivity(context, 0, notificationIntent, 0);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, context.getString(R.string.channel_id));
        builder.setSmallIcon(android.R.drawable.stat_notify_sdcard)
                .setDeleteIntent(deletePendingIntent)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setSmallIcon(smallIconID);
        if (subText != null && subText.length() > 0) builder.setSubText(subText);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());
    }
}
