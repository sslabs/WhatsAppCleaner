package com.sslabs.whatsappcleaner.viewmodel

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.viewmodel.data.Scheduling
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val _scheduling = MutableLiveData<Scheduling?>()
    val scheduling: LiveData<Scheduling?>
        get() = _scheduling

    var timePickerVisible = Transformations.map(scheduling) {
        if (it != null && it.enabled) View.VISIBLE else View.INVISIBLE
    }

    var clockImageVisible = Transformations.map(timePickerVisible) {
        if (it == View.VISIBLE) View.GONE else View.VISIBLE
    }

    var scheduleTimeMessage = Transformations.map(scheduling) {
        val timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        val time = _scheduling.value!!.localTime.format(timeFormat)
        application.getString(R.string.scheduling_time_text, time)
    }

    init {
        loadScheduling()
    }

    fun enableScheduling(enable: Boolean) {
        _scheduling.value = Scheduling(enable, LocalTime.now())
    }

    fun setSchedulingTime(hour: Int, minute: Int) {
        _scheduling.value = Scheduling(_scheduling.value!!.enabled, LocalTime.of(hour, minute))
    }

    private fun loadScheduling() {
        _scheduling.value = Scheduling(false, LocalTime.now())
    }
}
