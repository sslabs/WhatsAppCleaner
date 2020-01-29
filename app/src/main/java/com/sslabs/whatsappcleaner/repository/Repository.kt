package com.sslabs.whatsappcleaner.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.sslabs.whatsappcleaner.combineWith
import com.sslabs.whatsappcleaner.getLiveInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime

object Repository {
    fun getScheduling(context: Context) : LiveData<LocalTime?> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val hour = sharedPreferences.getLiveInt(SCHEDULING_HOUR_PREFS_KEY, INVALID_HOUR)
        val minute = sharedPreferences.getLiveInt(SCHEDULING_MINUTE_PREFS_KEY, INVALID_MINUTE)
        return hour.combineWith(minute) { h, m ->
            if (h != null && h != INVALID_HOUR && m!= null && m != INVALID_MINUTE) {
                LocalTime.of(h, m)
            } else null
        }
    }

    suspend fun saveScheduling(context: Context, scheduling: LocalTime) : Boolean {
        return withContext(Dispatchers.IO) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit()
                .putInt(SCHEDULING_HOUR_PREFS_KEY, scheduling.hour)
                .putInt(SCHEDULING_MINUTE_PREFS_KEY, scheduling.minute)
                .commit()
        }
    }

    suspend fun deleteScheduling(context: Context) : Boolean {
        return withContext(Dispatchers.IO) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit()
                .remove(SCHEDULING_HOUR_PREFS_KEY)
                .remove(SCHEDULING_MINUTE_PREFS_KEY)
                .commit()
        }
    }
}
