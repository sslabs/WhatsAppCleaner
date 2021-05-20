package com.sslabs.whatsappcleaner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.findNavController
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.databinding.FragmentHomeBinding
import com.sslabs.whatsappcleaner.shortFormat
import com.sslabs.whatsappcleaner.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel by viewModel<HomeViewModel>()

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        binding.lifecycleOwner = this

        initViews()

        return binding.root
    }

    private fun initViews() {
        binding.homeScheduleCard.scheduleCardAcceptButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToSchedule())
        }

        Transformations.map(viewModel.scheduling) {
            if (it == null) {
                getString(R.string.schedule_card_off_body)
            } else {
                getString(R.string.schedule_card_on_body, it.shortFormat())
            }
        }.observe(viewLifecycleOwner, Observer {
            binding.homeScheduleCard.scheduleCardBody.text = it
        })

        Transformations.map(viewModel.scheduling) {
            if (it == null) {
                getString(R.string.schedule_card_set_action_text)
            } else {
                getString(R.string.schedule_card_change_action_text)
            }
        }.observe(viewLifecycleOwner, Observer {
            binding.homeScheduleCard.scheduleCardAcceptButton.text = it
        })
    }
}
