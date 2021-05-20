package com.sslabs.whatsappcleaner

import android.app.Application
import com.sslabs.whatsappcleaner.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.KoinExperimentalAPI
import org.koin.core.context.GlobalContext.startKoin
import timber.log.Timber

@KoinExperimentalAPI
class WhatsAppCleanerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initTimber()
        initKoin()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@WhatsAppCleanerApplication)
            workManagerFactory()
            modules(appModules)
        }
    }
}
