package com.sslabs.whatsappcleaner;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "sslabs";

    private AlarmManager mAlarmManager;
    private PendingIntent mCleanupPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO: enable BootReceiver

        init();
        ensurePermissions();
    }

    private void init() {
        Switch enableCleanupSwitch = findViewById(R.id.switch_enable_cleanup);
        enableCleanupSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ComponentName cn = new ComponentName(getBaseContext(), CleanerReceiver.class);
                int flag = isChecked ?
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                getPackageManager().
                        setComponentEnabledSetting(cn, flag, PackageManager.DONT_KILL_APP);
                if (isChecked) {
                    scheduleCleanup();
                } else {
                    removeScheduledCleanup();
                }
            }
        });

        Context context = this.getBaseContext();
        Intent intent = new Intent(CleanerReceiver.ACTION_FIRE_CLEANUP);
        mCleanupPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private void scheduleCleanup() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 07);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, mCleanupPendingIntent);
    }

    private void removeScheduledCleanup() {
        mAlarmManager.cancel(mCleanupPendingIntent);
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
