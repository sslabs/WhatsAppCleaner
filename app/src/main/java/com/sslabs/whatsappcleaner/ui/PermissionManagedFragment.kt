package com.sslabs.whatsappcleaner.ui

import android.content.Context
import androidx.fragment.app.Fragment
import com.sslabs.whatsappcleaner.util.PermissionHandler
import com.sslabs.whatsappcleaner.util.PermissionManager
import com.sslabs.whatsappcleaner.util.addObserver

open class PermissionManagedFragment : Fragment() {

    private val permissionManager = PermissionManager()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycle.addObserver(permissionManager, this)
    }

    fun doWhenAllowed(permissionHandler: PermissionHandler) {
        permissionManager.request(permissionHandler)
    }
}
