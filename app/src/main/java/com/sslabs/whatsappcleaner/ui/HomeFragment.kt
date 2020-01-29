package com.sslabs.whatsappcleaner.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false)

        binding.homeScheduleCard.scheduleCardAcceptButton.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeToSchedule())
        }
        
        return binding.root
    }
}
