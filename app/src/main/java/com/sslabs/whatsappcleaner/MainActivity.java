package com.sslabs.whatsappcleaner;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "sslabs";

//    private static final String DATABASES_PATH = "WhatsApp/Databases";
    private PendingIntent mCleanupPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: enable BootReceiver

        init();
        ensurePermissions();
        scheduleCleanup();
    }

    public void onDeleteDatabasesClick(View view) {
        deleteBackupDatabases();
    }

    private void init() {
        Context context = this.getBaseContext();
        Intent intent = new Intent(context, CleanerIntentService.class);
        mCleanupPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void scheduleCleanup() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 06);

        Context context = getBaseContext();
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, mCleanupPendingIntent);
    }

    private void deleteBackupDatabases() {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            File databasesPath =
//                    new File(Environment.getExternalStorageDirectory(), DATABASES_PATH);
//            File[] oldBackupDatabases = databasesPath.listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File pathname) {
//                    return Pattern.matches("^msgstore-.*\\.db\\.crypt12$",
//                            pathname.getName());
//                }
//            });
//            for (File toDelete : oldBackupDatabases) {
//                Log.d(TAG, "File to delete: " + toDelete.getName());
////                toDelete.delete();
//            }
//        }
    }

    private void ensurePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                //TODO: how should it behave?
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            }
        }
    }
}
