package com.apppppp.bluetoothclassictest

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.apppppp.bluetoothclassictest.navigation.NavigationGraph
import com.apppppp.bluetoothclassictest.ui.theme.BluetoothClassicTestTheme
import android.provider.Settings
import com.apppppp.bluetoothclassictest.screens.PermissionDeniedSnackbar


class MainActivity : ComponentActivity() {
    private lateinit var bluetoothPermissionHandler: BluetoothPermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bluetoothPermissionHandler = BluetoothPermissionHandler(
            activity = this,
            onPermissionGranted = {
                loadUI()
            },
            onPermissionDenied = {
                // パーミッションが拒否された時の処理
                showPermissionDeniedView()
            }
        )
        bluetoothPermissionHandler.setupPermissionLauncher()
//        bluetoothPermissionHandler.requestBluetoothPermissions()

    }

    override fun onResume() {
        super.onResume()
        bluetoothPermissionHandler.requestBluetoothPermissions()
    }

    private fun loadUI() {
        setContent {
            val navController = rememberNavController()
            BluetoothClassicTestTheme {
                NavigationGraph(
                    navController = navController
                )
            }
        }
    }

    private fun showPermissionDeniedView() {
        setContent {
            PermissionDeniedSnackbar(onSettingsClick = { openAppSettings() })
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

}

