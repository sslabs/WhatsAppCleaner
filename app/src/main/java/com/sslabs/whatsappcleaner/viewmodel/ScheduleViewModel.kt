package com.sslabs.whatsappcleaner.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.repository.Repository
import com.sslabs.whatsappcleaner.shortFormat
import com.sslabs.whatsappcleaner.work.DeleteDatabasesWorker
import com.sslabs.whatsappcleaner.work.DeleteDatabasesWorker.Companion.DELETE_WORK_NAME
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private var cleanupRequest: PeriodicWorkRequest?

    val scheduling: LiveData<LocalTime?> = Repository.getScheduling(application.baseContext)

    val timePickerVisible = Transformations.map(scheduling) {
        if (it != null) View.VISIBLE else View.INVISIBLE
    }

    val clockImageVisible = Transformations.map(timePickerVisible) {
        if (it == View.VISIBLE) View.GONE else View.VISIBLE
    }

    val scheduleTimeMessage = Transformations.map(scheduling) {
        scheduling.value?.let {
            application.getString(R.string.scheduling_time_text, it.shortFormat())
        }
    }

    init {
        cleanupRequest = null
    }

    fun startScheduling(hour: Int, minute: Int) {
        viewModelScope.launch {
            val time = LocalTime.of(hour, minute)
            Repository.saveScheduling(getApplication<Application>().baseContext, time)
            scheduleCleanup(time)
        }
    }

    fun stopScheduling() {
        viewModelScope.launch {
            Repository.deleteScheduling(getApplication<Application>().baseContext)
        }
    }

    private fun scheduleCleanup(time: LocalTime) {
        cleanupRequest = PeriodicWorkRequestBuilder<DeleteDatabasesWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(Duration.between(LocalTime.now(), time))
            .build()
        cleanupRequest?.let {
            Timber.i("The next databases cleanup is scheduled to ${time.shortFormat()}")
            WorkManager.getInstance(getApplication())
                .enqueueUniquePeriodicWork(DELETE_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, it)
        }
    }
}
