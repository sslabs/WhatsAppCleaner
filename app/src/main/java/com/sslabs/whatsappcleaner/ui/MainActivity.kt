package com.sslabs.whatsappcleaner.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        val navController: NavController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.schedule_fragment -> navController.navigate(
                    HomeFragmentDirections.actionHomeToSchedule())
                else -> navController.popBackStack()
            }
            true
        }
    }
}
