package com.sslabs.whatsappcleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class CleanerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                SharedPreferences preferences = context.getSharedPreferences(
                        MainActivity.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
                if (preferences.contains(MainActivity.SCHEDULE_HOUR_KEY) &&
                        preferences.contains(MainActivity.SCHEDULE_MINUTE_KEY)) {
                    int hour = preferences.getInt(MainActivity.SCHEDULE_HOUR_KEY, -1);
                    int minute = preferences.getInt(MainActivity.SCHEDULE_MINUTE_KEY, -1);
                    Utils.INSTANCE.scheduleCleanup(context, hour, minute);
                }
            }
        }
    }
}
