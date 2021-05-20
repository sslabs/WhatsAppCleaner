package com.sslabs.whatsappcleaner.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.sslabs.whatsappcleaner.repository.Repository
import com.sslabs.whatsappcleaner.viewmodel.HomeViewModel
import com.sslabs.whatsappcleaner.viewmodel.ScheduleViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sharedPreferences = module {
    single { getSharedPreferences(androidContext()) }
}

val repositories = module {
    single { Repository(get()) }
}

val viewModels = module {
    viewModel { HomeViewModel(get()) }
    viewModel { ScheduleViewModel(get()) }
}

val appModules = listOf(sharedPreferences, repositories, viewModels)

private fun getSharedPreferences(context: Context): SharedPreferences =
    PreferenceManager.getDefaultSharedPreferences(context)
