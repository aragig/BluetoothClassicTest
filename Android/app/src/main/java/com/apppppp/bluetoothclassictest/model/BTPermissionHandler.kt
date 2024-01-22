package com.apppppp.bluetoothclassictest.model

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.activity.ComponentActivity

/**
 * AndroidのBluetoothパーミッションをリクエストするクラス
 */
class BTPermissionHandler(
    private val activity: ComponentActivity,
    private val onPermissionGranted: () -> Unit,
    private val onPermissionDenied: () -> Unit
) {

    private lateinit var requestBluetoothPermissionLauncher: ActivityResultLauncher<Array<String>>

    private val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        // Manifest.permission.BLUETOOTH_SCAN,
        // Manifest.permission.ACCESS_FINE_LOCATION,
    )
    fun setupPermissionLauncher() {
        requestBluetoothPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    }

    fun requestBluetoothPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestBluetoothPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // すでに全てのパーミッションが付与されている場合
            onPermissionGranted()
        }
    }
//
//    fun isPermissionGranted(): Boolean {
//        // 必要なパーミッションを確認
//        return requiredPermissions.all {
//            ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
//        }
//    }
}