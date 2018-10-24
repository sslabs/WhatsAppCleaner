package com.sslabs.whatsappcleaner;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity
        implements CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "sslabs";
    public static final String SCHEDULE_HOUR_KEY = "hour";
    public static final String SCHEDULE_MINUTE_KEY = "minute";
    public static final String SHARED_PREFS_FILE_NAME = "cleaner_prefs";

    private static final int REQUEST_STORAGE_PERMISSION_CODE = 1;

    private AlarmManager mAlarmManager;
    private PendingIntent mCleanupPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION_CODE) {
            Switch enableCleanupSwitch = findViewById(R.id.switch_enable_cleanup);
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCheckedChanged(enableCleanupSwitch, true);
            } else {
                enableCleanupSwitch.setOnCheckedChangeListener(null);
                enableCleanupSwitch.setChecked(false);
                enableCleanupSwitch.setOnCheckedChangeListener(this);
                if (!shouldRequestStoragePermissionRationale()) {
                    // Permission denied forever
                    View view = findViewById(R.id.coordinator_layout);
                    final Snackbar snackbar = Snackbar.make(
                            view,
                            R.string.storage_permission_denied_forever,
                            Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        }
    }

    private void init() {
        Switch enableCleanupSwitch = findViewById(R.id.switch_enable_cleanup);
        if (isScheduled()) {
            enableCleanupSwitch.setChecked(true);
            loadScheduleTime();
        }
        enableCleanupSwitch.setOnCheckedChangeListener(this);

        Context context = getBaseContext();
        Intent intent = new Intent(context, CleanerReceiver.class);
        intent.setAction(CleanerReceiver.ACTION_FIRE_CLEANUP);
        mCleanupPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void onOpenScheduleTimePickerClick(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "time_picker_fragment");
    }

    private void loadScheduleTime() {
        if (!isScheduled()) {
            return;
        }

        View scheduleBox = findViewById(R.id.schedule_box);
        if (scheduleBox.getVisibility() != View.VISIBLE) {
            scheduleBox.setVisibility(View.VISIBLE);
        }

        Resources res = getResources();
        SharedPreferences preferences =
                getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        int hour = preferences.getInt(SCHEDULE_HOUR_KEY, -1);
        int minute = preferences.getInt(SCHEDULE_MINUTE_KEY, -1);
        TextView scheduleTextView = findViewById(R.id.scheduled_time_textview);
        scheduleTextView.setText(res.getString(R.string.text_schedule_time_text, hour, minute));
    }

    private boolean isScheduled() {
        SharedPreferences preferences =
                getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
        return preferences.contains(SCHEDULE_HOUR_KEY) && preferences.contains(SCHEDULE_MINUTE_KEY);
    }

    private void scheduleCleanup(int hour, int minute) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, mCleanupPendingIntent);
    }

    private void removeScheduledCleanup() {
        removeCleanupSharedPreferences();
        mAlarmManager.cancel(mCleanupPendingIntent);
    }

    private void removeCleanupSharedPreferences() {
        SharedPreferences.Editor editor =
                getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }

    private boolean checkStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(
                getBaseContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requireStoragePermission() {
        if (shouldRequestStoragePermissionRationale()) {
            DialogFragment storagePermissionFragment =
                    new ShouldGrantStoragePermissionDialog();
            storagePermissionFragment.show(getSupportFragmentManager(),
                    "storage_permission_fragment");
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION_CODE);
        }
    }

    private boolean shouldRequestStoragePermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!checkStoragePermissionGranted()) {
            requireStoragePermission();
            return;
        }

        ComponentName cn = new ComponentName(getBaseContext(), CleanerReceiver.class);
        int flag = isChecked ?
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        getPackageManager().
                setComponentEnabledSetting(cn, flag, PackageManager.DONT_KILL_APP);

        if (isChecked) {
            View scheduleBox = findViewById(R.id.schedule_box);
            scheduleBox.setVisibility(View.VISIBLE);
        } else {
            View scheduleBox = findViewById(R.id.schedule_box);
            scheduleBox.setVisibility(View.INVISIBLE);

            TextView scheduleTextView = findViewById(R.id.scheduled_time_textview);
            scheduleTextView.setText(R.string.text_not_scheduled_text);

            removeScheduledCleanup();
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
            MainActivity activity = (MainActivity) getActivity();
            SharedPreferences.Editor editor = activity.
                    getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE).edit();
            editor.putInt(SCHEDULE_HOUR_KEY, timePicker.getHour());
            editor.putInt(SCHEDULE_MINUTE_KEY, timePicker.getMinute());
            editor.apply();
            activity.loadScheduleTime();
            activity.scheduleCleanup(timePicker.getHour(), timePicker.getMinute());
        }
    }

    public static class ShouldGrantStoragePermissionDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.should_grant_storage_permission_dialog_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_STORAGE_PERMISSION_CODE);
                            dialog.dismiss();
                        }
                    });
            return builder.create();
        }
    }
}
