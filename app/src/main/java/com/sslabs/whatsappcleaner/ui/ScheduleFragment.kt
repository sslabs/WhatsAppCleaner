package com.sslabs.whatsappcleaner.ui


import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.databinding.FragmentScheduleBinding
import com.sslabs.whatsappcleaner.viewmodel.ScheduleViewModel

class ScheduleFragment : Fragment() {

    private lateinit var binding: FragmentScheduleBinding

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false)
        binding.lifecycleOwner = this

        viewModel = ViewModelProviders.of(this).get(ScheduleViewModel::class.java)
        binding.scheduleViewModel = viewModel

        initViews()

        return binding.root
    }

    private fun initViews() {
        initSwitcher()
        initTimePicker()
    }

    private fun initSwitcher() {
        binding.scheduleCleanupSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.enableScheduling(isChecked)
        }
    }

    private fun initTimePicker() {
        binding.scheduleTimePicker.setIs24HourView(DateFormat.is24HourFormat(context))
        binding.scheduleTimePicker.setOnTimeChangedListener { timePicker, _, _ ->
            viewModel.setSchedulingTime(timePicker.hour, timePicker.minute)
        }
    }
}