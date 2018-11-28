package com.sslabs.whatsappcleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CleanerReceiver extends BroadcastReceiver {
    public static final String ACTION_NOTIFICATION_DISMISSED =
            "com.sslabs.whatsappcleaner.ACTION_NOTIFICATION_DISMISSED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                CleanerSharedPreferences.ScheduledTime scheduledTime =
                        CleanerSharedPreferences.getScheduledTime(context);
                if (scheduledTime != null) {
                    Utils.INSTANCE.scheduleCleanup(context, scheduledTime.hour, scheduledTime.minute);
                }
            } else if (ACTION_NOTIFICATION_DISMISSED.equals(action)) {
                CleanerSharedPreferences.removeDeletedFileInfo(context);
            }
        }
    }
}
