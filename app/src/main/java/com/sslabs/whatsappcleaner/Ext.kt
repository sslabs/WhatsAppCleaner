package com.sslabs.whatsappcleaner

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.sslabs.whatsappcleaner.repository.content.IntLivePreference
import com.sslabs.whatsappcleaner.repository.content.SharedLivePreference

/**
 * Combines two LiveData objects. The returned LiveData emits whenever any of its sources emits.
 */
fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block.invoke(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block.invoke(this.value, liveData.value)
    }
    return result
}

/**
 * Retrieve an observable to an int value from the preferences.
 */
fun SharedPreferences.getLiveInt(key: String, defaultValue: Int) : SharedLivePreference<Int> {
    return IntLivePreference(this, key, defaultValue)
}
