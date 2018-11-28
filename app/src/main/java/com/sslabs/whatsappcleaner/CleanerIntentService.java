package com.sslabs.whatsappcleaner;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class CleanerIntentService extends IntentService {
    public static final int SUCCESS_NOTIFICATION_ID = 1;
    public static final int FAILED_NOTIFICATION_ID = 2;

    private static final String DATABASES_PATH = "WhatsApp/Databases";

    public CleanerIntentService() {
        super("CleanerIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Context context = getBaseContext();
        Resources res = context.getResources();

        if (!Utils.INSTANCE.checkStoragePermissionGranted(getBaseContext())) {
            String notificationText = res.getString(R.string.notification_cleanup_failed_missing_permission);
            Utils.INSTANCE.sendNotification(context, FAILED_NOTIFICATION_ID,
                    notificationText, null, android.R.drawable.stat_notify_error);
            return;
        }

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String notificationText = res.getString(R.string.notification_cleanup_failed_unmounted);
            Utils.INSTANCE.sendNotification(context, FAILED_NOTIFICATION_ID,
                    notificationText, null, android.R.drawable.stat_notify_error);
            return;
        }

        File[] oldBackupDatabases = filesToDelete();
        if (oldBackupDatabases != null && oldBackupDatabases.length > 0) {
            long totalSize = 0;
            boolean success = true;
            for (File toDelete : oldBackupDatabases) {
                totalSize += getFileSize(toDelete);
                success &= toDelete.delete();
            }

            CleanerSharedPreferences.DeletedFilesInfo savedFilesInfo =
                    CleanerSharedPreferences.getDeletedFilesInfo(context);
            if (savedFilesInfo == null) {
                savedFilesInfo = new CleanerSharedPreferences.DeletedFilesInfo();
                savedFilesInfo.count = 0;
                savedFilesInfo.totalSize = 0L;
            }
            savedFilesInfo.count += oldBackupDatabases.length;
            savedFilesInfo.totalSize += totalSize;
            CleanerSharedPreferences.saveDeletedFilesInfo(context, savedFilesInfo);

            // Send appropriate notification
            if (success) {
                String notificationText = res.getQuantityString(
                        R.plurals.files_deleted_info,
                        savedFilesInfo.count,
                        savedFilesInfo.count,
                        savedFilesInfo.totalSize);
                Utils.INSTANCE.sendNotification(context, SUCCESS_NOTIFICATION_ID,
                        notificationText);
            } else {
                String notificationText = res.getString(R.string.notification_cleanup_failed_delete);
                Utils.INSTANCE.sendNotification(context, FAILED_NOTIFICATION_ID,
                        notificationText, null, android.R.drawable.stat_notify_error);
            }
        }
    }

    private File[] filesToDelete() {
        File databasesPath =
                new File(Environment.getExternalStorageDirectory(), DATABASES_PATH);
        File[] oldBackupDatabases = databasesPath.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return Pattern.matches("^msgstore-.*\\.db\\.crypt12$",
                        pathname.getName());
            }
        });
        return oldBackupDatabases;
    }

    private long getFileSize(File file) {
        return file.length() / 1000000; // file size in MB
    }
}
