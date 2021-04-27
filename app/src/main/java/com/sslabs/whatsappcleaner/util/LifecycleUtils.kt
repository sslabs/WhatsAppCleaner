package com.sslabs.whatsappcleaner.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver

interface FragmentLifecycleObserver : LifecycleObserver {
    fun initWith(owner: Fragment)
}

fun Lifecycle.addObserver(observer: FragmentLifecycleObserver, fragment: Fragment) {
    observer.initWith(fragment)
    addObserver(observer)
}
