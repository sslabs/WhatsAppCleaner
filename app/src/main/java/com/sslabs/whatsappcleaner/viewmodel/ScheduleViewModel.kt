package com.sslabs.whatsappcleaner.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    val scheduling: LiveData<LocalTime?> = Repository.getScheduling(application.baseContext)

    val timePickerVisible = Transformations.map(scheduling) {
        if (it != null) View.VISIBLE else View.INVISIBLE
    }

    val clockImageVisible = Transformations.map(timePickerVisible) {
        if (it == View.VISIBLE) View.GONE else View.VISIBLE
    }

    val scheduleTimeMessage = Transformations.map(scheduling) {
        val timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        scheduling.value?.let {
            val time = scheduling.value!!.format(timeFormat)
            application.getString(R.string.scheduling_time_text, time)
        }
    }

    private val viewModelJob = Job()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun startScheduling(hour: Int, minute: Int) {
        uiScope.launch {
            val time = LocalTime.of(hour, minute)
            Repository.saveScheduling(getApplication<Application>().baseContext, time)
        }
    }

    fun stopScheduling() {
        uiScope.launch {
            Repository.deleteScheduling(getApplication<Application>().baseContext)
        }
    }
}
