package com.sslabs.whatsappcleaner.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.databinding.FragmentScheduleBinding
import com.sslabs.whatsappcleaner.viewmodel.ScheduleViewModel
import java.time.LocalTime

class ScheduleFragment : PermissionManagedFragment() {

    private lateinit var binding: FragmentScheduleBinding

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        binding.scheduleViewModel = viewModel

        initViews()

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initViews() {
        initSwitcher()
        initTimePicker()
    }

    private fun initSwitcher() {
        binding.scheduleCleanupSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                whenStorageAvailable({
                    val now = LocalTime.now()
                    // Starts with one hour ahead and give user some time to play with the time picker
                    viewModel.startScheduling(now.hour + 1, now.minute)
                }, {
                    binding.scheduleCleanupSwitcher.isChecked = false
                    defaultRequestStorageDenied()
                })
            } else {
                viewModel.stopScheduling()
            }
        }
    }

    private fun initTimePicker() {
        binding.scheduleTimePicker.setIs24HourView(DateFormat.is24HourFormat(context))
        binding.scheduleTimePicker.setOnTimeChangedListener { timePicker, _, _ ->
            viewModel.scheduling.value?.let {
                viewModel.startScheduling(timePicker.hour, timePicker.minute)
            }
        }
    }
}
