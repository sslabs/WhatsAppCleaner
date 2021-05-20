package com.sslabs.whatsappcleaner.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sslabs.whatsappcleaner.repository.Repository
import com.sslabs.whatsappcleaner.shortFormat
import com.sslabs.whatsappcleaner.work.DeleteDatabasesWorker
import com.sslabs.whatsappcleaner.work.DeleteDatabasesWorker.Companion.DELETE_WORK_NAME
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class ScheduleViewModel(private val repository: Repository, private val app: Application) :
    ViewModel() {

    private var cleanupRequest: PeriodicWorkRequest?

    val scheduling: LiveData<LocalTime?> = repository.getScheduling()

    init {
        cleanupRequest = null
    }

    fun startScheduling(hour: Int, minute: Int) {
        viewModelScope.launch {
            val time = LocalTime.of(hour, minute)
            repository.saveScheduling(time)
            scheduleCleanup(time)
        }
    }

    fun stopScheduling() {
        viewModelScope.launch {
            repository.deleteScheduling()
        }
    }

    private fun scheduleCleanup(time: LocalTime) {
        cleanupRequest = PeriodicWorkRequestBuilder<DeleteDatabasesWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(Duration.between(LocalTime.now(), time))
            .build()
        cleanupRequest?.let {
            Timber.i("The next databases cleanup is scheduled to ${time.shortFormat()}")
            WorkManager.getInstance(app.applicationContext)
                .enqueueUniquePeriodicWork(DELETE_WORK_NAME, ExistingPeriodicWorkPolicy.REPLACE, it)
        }
    }
}
