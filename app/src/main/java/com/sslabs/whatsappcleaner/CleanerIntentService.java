package com.sslabs.whatsappcleaner;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class CleanerIntentService extends IntentService {

    private static final String DATABASES_PATH = "WhatsApp/Databases";

    public CleanerIntentService() {
        super("CleanerIntentServiceThread");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //TODO: check preference if can delete files
        Log.d(MainActivity.TAG, "CleanerIntentService.onHandleIntent");
        deleteBackupDatabases();
    }

    private void deleteBackupDatabases() {
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
                Log.i(MainActivity.TAG, "Going to delete old database: " + toDelete.getName());
//                toDelete.delete();
            }
        }
    }
}
