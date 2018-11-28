package com.sslabs.whatsappcleaner;

import android.content.Context;
import android.content.SharedPreferences;

public class CleanerSharedPreferences {
    private static final String SCHEDULE_HOUR_KEY = "hour";
    private static final String SCHEDULE_MINUTE_KEY = "minute";
    private static final String DELETED_FILES_COUNT_KEY = "del_files_count";
    private static final String TOTAL_FREE_SPACE_KEY = "total_free_space";
    private static final String SHARED_PREFS_FILE_NAME = "cleaner_prefs";

    public static ScheduledTime getScheduledTime(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        ScheduledTime scheduledTime = new ScheduledTime();
        scheduledTime.hour = preferences.getInt(SCHEDULE_HOUR_KEY, -1);
        scheduledTime.minute = preferences.getInt(SCHEDULE_MINUTE_KEY, -1);
        return (scheduledTime.hour != -1 && scheduledTime.minute != -1) ? scheduledTime : null;
    }

    public static void saveScheduleTime(Context context, ScheduledTime scheduledTime) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(SCHEDULE_HOUR_KEY, scheduledTime.hour);
        editor.putInt(SCHEDULE_MINUTE_KEY, scheduledTime.minute);
        editor.apply();
    }

    public static void removeScheduledTime(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(SCHEDULE_HOUR_KEY);
        editor.remove(SCHEDULE_MINUTE_KEY);
        editor.apply();
    }

    public static DeletedFilesInfo getDeletedFilesInfo(Context context) {
        SharedPreferences preferences = getSharedPreferences(context);
        DeletedFilesInfo deletedFiles = new DeletedFilesInfo();
        deletedFiles.count = preferences.getInt(DELETED_FILES_COUNT_KEY, -1);
        deletedFiles.totalSize = preferences.getLong(TOTAL_FREE_SPACE_KEY, -1L);
        if (deletedFiles.count != -1 && deletedFiles.totalSize > 0) return deletedFiles;
        else return null;
    }

    public static void saveDeletedFilesInfo(Context context, DeletedFilesInfo info) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(DELETED_FILES_COUNT_KEY, info.count);
        editor.putLong(TOTAL_FREE_SPACE_KEY, info.totalSize);
        editor.apply();
    }

    public static void removeDeletedFileInfo(Context context) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(DELETED_FILES_COUNT_KEY);
        editor.remove(TOTAL_FREE_SPACE_KEY);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    static class ScheduledTime {
        int hour;
        int minute;
    }

    static class DeletedFilesInfo {
        int count;
        long totalSize;
    }
}
