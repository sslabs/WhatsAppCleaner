package com.sslabs.whatsappcleaner.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.android.material.snackbar.Snackbar
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.databinding.FragmentScheduleBinding
import com.sslabs.whatsappcleaner.shortFormat
import com.sslabs.whatsappcleaner.util.StoragePermissionHandler
import com.sslabs.whatsappcleaner.viewmodel.ScheduleViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalTime

class ScheduleFragment : PermissionManagedFragment() {

    private lateinit var binding: FragmentScheduleBinding

    private val viewModel by viewModel<ScheduleViewModel>()

    private lateinit var timePickerVisible: LiveData<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false)
        binding.lifecycleOwner = this

        binding.scheduleViewModel = viewModel

        initViews()

        return binding.root
    }

    private fun initViews() {
        initTimePicker()
        initSwitcher()
        initSchedulingOffViews()
    }

    private fun initSchedulingOffViews() {
        Transformations.map(viewModel.scheduling) {
            it?.let {
                getString(R.string.scheduling_time_text, it.shortFormat())
            }
        }.observe(viewLifecycleOwner, {
            binding.scheduleTimeText.text = it
        })

        Transformations.map(timePickerVisible) {
            if (it == View.VISIBLE) View.GONE else View.VISIBLE
        }.observe(viewLifecycleOwner, {
            binding.scheduleImage.visibility = it
        })
    }

    private fun initSwitcher() {
        binding.scheduleCleanupSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                doWhenAllowed(StoragePermissionHandler({
                    val now = LocalTime.now()
                    // Starts with one hour ahead and give user some time to play with the time picker
                    viewModel.startScheduling(now.hour + 1, now.minute)
                }, {
                    binding.scheduleCleanupSwitcher.isChecked = false
                    defaultRequestStorageDenied()
                }))
            } else {
                viewModel.stopScheduling()
            }
        }
    }

    private fun initTimePicker() {
        timePickerVisible = Transformations.map(viewModel.scheduling) {
            if (it != null) View.VISIBLE else View.INVISIBLE
        }

        timePickerVisible.observe(viewLifecycleOwner, {
            binding.scheduleTimeContainer.visibility = it
        })

        binding.scheduleTimePicker.setIs24HourView(DateFormat.is24HourFormat(context))
        binding.scheduleTimePicker.setOnTimeChangedListener { timePicker, _, _ ->
            viewModel.scheduling.value?.let {
                viewModel.startScheduling(timePicker.hour, timePicker.minute)
            }
        }
    }

    private fun defaultRequestStorageDenied() {
        view?.let {
            Snackbar
                .make(it, R.string.storage_permission_denied_message, Snackbar.LENGTH_LONG)
                .show()
        }
    }
}
