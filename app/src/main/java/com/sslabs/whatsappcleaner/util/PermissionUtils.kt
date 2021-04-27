package com.sslabs.whatsappcleaner.util

import android.Manifest
import android.content.pm.PackageManager
import android.util.SparseArray
import androidx.core.content.ContextCompat
import androidx.core.util.contains
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent

const val MAX_INT_16_BIT = 0b1111111111111111

sealed class PermissionHandler(
    val onGranted: () -> Unit,
    val onFailed: (failedPermissions: Array<String>) -> Unit
) {

    abstract val permissions: Array<String>

    fun onPermissionResult(permissions: Array<String>, grantResults: IntArray) {
        val denied =
            grantResults.indices.filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
        if (denied.isEmpty()) {
            onGranted()
        } else {
            onFailed(permissions)
        }
    }
}

class StoragePermissionHandler(
    onGranted: () -> Unit,
    onFailed: (failedPermissions: Array<String>) -> Unit
) : PermissionHandler(onGranted, onFailed) {

    override val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
}

class PermissionManager : FragmentLifecycleObserver {
    private val requestedPermissionHandlers = SparseArray<PermissionHandler>()
    private val pendingResults = SparseArray<PermissionResult>()

    lateinit var permissionRequester: (Array<String>, Int) -> Unit
    lateinit var permissionChecker: (String) -> Boolean

    override fun initWith(owner: Fragment) {
        permissionRequester = { permissions, id -> owner.requestPermissions(permissions, id) }
        permissionChecker = {
            val selfPermission = ContextCompat.checkSelfPermission(owner.requireContext(), it)
            selfPermission == PackageManager.PERMISSION_GRANTED
        }
    }

    @Synchronized
    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestedPermissionHandlers.contains(requestCode)) {
            val permissionHandler = requestedPermissionHandlers.get(requestCode)
            requestedPermissionHandlers.remove(requestCode)
            pendingResults[requestCode] =
                PermissionResult(permissionHandler, permissions, grantResults)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun ready() {
        pendingResults.forEach { _, value -> value.onPermissionResult() }
        pendingResults.clear()
    }

    fun request(permissionHandler: PermissionHandler) {
        val notGrantedPermissions = ArrayList(permissionHandler.permissions
            .filterNot { permissionChecker(it) })
            .toTypedArray()
        if (notGrantedPermissions.isEmpty()) {
            permissionHandler.onGranted()
        } else {
            val id = genPermissionKey()
            requestedPermissionHandlers.put(id, permissionHandler)
            permissionRequester(notGrantedPermissions, id)
        }
    }

    private fun genPermissionKey(maxTries: Int = 3): Int {
        if (maxTries < 0) throw RuntimeException("Could not generate sparse array key")

        val random = (1 until MAX_INT_16_BIT).random()
        return if (requestedPermissionHandlers.contains(random)) {
            genPermissionKey(maxTries - 1) // Key collision
        } else {
            random
        }
    }

    private class PermissionResult (
        private val permissionHandler: PermissionHandler,
        private val resultPermissions: Array<String>,
        private val grantResults: IntArray
    ) {

        fun onPermissionResult() =
            permissionHandler.onPermissionResult(resultPermissions, grantResults)
    }
}
