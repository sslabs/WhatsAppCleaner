package com.sslabs.whatsappcleaner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Utils {
    public static void scheduleCleanup(Context context, int hour, int minute) {
        Intent intent = new Intent(context, CleanerReceiver.class);
        intent.setAction(CleanerReceiver.ACTION_FIRE_CLEANUP);
        PendingIntent cleanupPendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, 0);
        scheduleCleanup(context, cleanupPendingIntent, hour, minute);
    }

    public static void scheduleCleanup(Context context,
                                       PendingIntent pendingIntent,
                                       int hour,
                                       int minute) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    public static void cancelCleanup(Context context, PendingIntent pendingIntent) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
