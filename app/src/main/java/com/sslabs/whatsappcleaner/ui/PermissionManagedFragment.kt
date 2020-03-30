package com.sslabs.whatsappcleaner.ui

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.sslabs.whatsappcleaner.R
import com.sslabs.whatsappcleaner.util.PermissionManager
import com.sslabs.whatsappcleaner.util.StoragePermissionHandler
import com.sslabs.whatsappcleaner.util.addObserver

open class PermissionManagedFragment : Fragment() {

    val permissionManager = PermissionManager()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(permissionManager, this)
    }

    fun whenStorageAvailable(
        onGranted: () -> Unit ,
        onFailed: (failedPermissions: Array<String>) -> Unit
    ) {
        permissionManager.request(StoragePermissionHandler(onGranted, onFailed))
    }

    fun defaultRequestStorageDenied() {
        view?.let {
            Snackbar
                .make(it, R.string.storage_permission_denied_message, Snackbar.LENGTH_LONG)
                .show()
        }
    }
}
