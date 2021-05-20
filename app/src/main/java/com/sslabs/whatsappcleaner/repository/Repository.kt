package com.sslabs.whatsappcleaner.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.sslabs.whatsappcleaner.combineWith
import com.sslabs.whatsappcleaner.getLiveInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalTime

class Repository(private val sharedPreferences: SharedPreferences) {
    fun getScheduling() : LiveData<LocalTime?> {
        val hour = sharedPreferences.getLiveInt(SCHEDULING_HOUR_PREFS_KEY, INVALID_HOUR)
        val minute = sharedPreferences.getLiveInt(SCHEDULING_MINUTE_PREFS_KEY, INVALID_MINUTE)
        return hour.combineWith(minute) { h, m ->
            if (h != null && h != INVALID_HOUR && m!= null && m != INVALID_MINUTE) {
                LocalTime.of(h, m)
            } else null
        }
    }

    suspend fun saveScheduling(scheduling: LocalTime) {
        return withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                putInt(SCHEDULING_HOUR_PREFS_KEY, scheduling.hour)
                putInt(SCHEDULING_MINUTE_PREFS_KEY, scheduling.minute)
            }
        }
    }

    suspend fun deleteScheduling() {
        return withContext(Dispatchers.IO) {
            sharedPreferences.edit {
                remove(SCHEDULING_HOUR_PREFS_KEY)
                remove(SCHEDULING_MINUTE_PREFS_KEY)
            }
        }
    }
}
