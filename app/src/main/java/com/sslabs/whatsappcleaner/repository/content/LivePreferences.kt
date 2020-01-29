package com.sslabs.whatsappcleaner.repository.content

import android.content.SharedPreferences
import androidx.lifecycle.LiveData

abstract class SharedLivePreference<T> constructor(val preferences: SharedPreferences,
                                                   private val key: String,
                                                   private val defaultValue: T) : LiveData<T>() {

    abstract fun getPreferenceValue(key: String, defaultValue: T): T

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == this.key) {
            value = getPreferenceValue(key, defaultValue)
        }
    }

    override fun onActive() {
        super.onActive()
        value = getPreferenceValue(key, defaultValue)
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }
}

class IntLivePreference(preferences: SharedPreferences, key: String, defaultValue: Int)
    : SharedLivePreference<Int>(preferences, key, defaultValue) {

    override fun getPreferenceValue(key: String, defaultValue: Int) =
        preferences.getInt(key, defaultValue)
}
