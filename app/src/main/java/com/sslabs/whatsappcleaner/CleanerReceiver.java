package com.sslabs.whatsappcleaner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

public class CleanerReceiver extends BroadcastReceiver {

    public static final String ACTION_FIRE_CLEANUP =
            "com.sslabs.whatsappcleaner.intent.action.FIRE_CLEANUP";
    private static final String DATABASES_PATH = "WhatsApp/Databases";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
                SharedPreferences preferences = context.getSharedPreferences(
                        MainActivity.SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
                if (preferences.contains(MainActivity.SCHEDULE_HOUR_KEY) &&
                        preferences.contains(MainActivity.SCHEDULE_MINUTE_KEY)) {
                    scheduleCleanup(context, preferences);
                }
            } else if (ACTION_FIRE_CLEANUP.equals(action)) {
                new CleanerTask().execute();
            }
        }
    }

    private void scheduleCleanup(Context context, SharedPreferences preferences) {
        int hour = preferences.getInt(MainActivity.SCHEDULE_HOUR_KEY, -1);
        int minute = preferences.getInt(MainActivity.SCHEDULE_MINUTE_KEY, -1);
        Utils.scheduleCleanup(context, hour, minute);
    }

    static class CleanerTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File databasesPath =
                        new File(Environment.getExternalStorageDirectory(), DATABASES_PATH);
                File[] oldBackupDatabases = databasesPath.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return Pattern.matches("^msgstore-.*\\.db\\.crypt12$",
                                pathname.getName());
                    }
                });
                if (oldBackupDatabases != null && oldBackupDatabases.length > 0) {
                    for (File toDelete : oldBackupDatabases) {
                        toDelete.delete();
                    }
                }
            }
            return null;
        }
    }
}
