package com.sslabs.whatsappcleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
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
                //TODO: post pending intent, if necessary
            } else if (ACTION_FIRE_CLEANUP.equals(action)) {
                new CleanerTask().execute();
            }
        }
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
                for (File toDelete : oldBackupDatabases) {
                    Log.d(MainActivity.TAG, "File to delete: " + toDelete.getName());
                    toDelete.delete();
                }
            }
            return null;
        }
    }
}
