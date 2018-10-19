package com.sslabs.whatsappcleaner;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "sslabs";
//    public static final String SCHEDULE_TIME_KEY = "schedule_time";
    public static final String SCHEDULE_HOUR_KEY = "hour";
    public static final String SCHEDULE_MINUTE_KEY = "minute";

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
//        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
//        String scheduled_time = preferences.getString(SCHEDULE_TIME_KEY, null);
//        TextView scheduledTimeTextView = findViewById(R.id.scheduled_time_textview);
//        if (scheduled_time != null) {
//            scheduledTimeTextView.setText(scheduled_time);
//        } else {
//            scheduledTimeTextView.setText(getResources().getString(R.string.text_not_scheduled_text));
//        }

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

        Context context = getBaseContext();
        Intent intent = new Intent(this, CleanerReceiver.class);
        intent.setAction(CleanerReceiver.ACTION_FIRE_CLEANUP);
        mCleanupPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void onOpenScheduleTimePickerClick(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void scheduleCleanup() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 5);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
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

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            FragmentActivity activity = getActivity();
            SharedPreferences.Editor editor = activity.getPreferences(Context.MODE_PRIVATE).edit();
            editor.putInt(SCHEDULE_HOUR_KEY, timePicker.getHour());
            editor.putInt(SCHEDULE_MINUTE_KEY, timePicker.getMinute());
            editor.apply();

            View scheduleBox = activity.findViewById(R.id.schedule_box);
            if (scheduleBox.getVisibility() != View.VISIBLE) {
                scheduleBox.setVisibility(View.VISIBLE);
            }

            Resources res = activity.getResources();
            TextView scheduleTextView = activity.findViewById(R.id.scheduled_time_textview);
            scheduleTextView.setText(res.getString(R.string.text_schedule_time_text,
                    timePicker.getHour(), timePicker.getMinute()));
        }
    }
}
