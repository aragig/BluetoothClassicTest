package com.apppppp.bluetoothclassictest.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.apppppp.bluetoothclassictest.screens.DeviceScreen
import com.apppppp.bluetoothclassictest.screens.MainScreen
import com.apppppp.bluetoothclassictest.screens.ReceiveDataScreen
import com.apppppp.bluetoothclassictest.viewmodel.DeviceViewModel


@Composable
fun NavigationGraph(
    navController: NavHostController) {
    val deviceViewModel: DeviceViewModel = viewModel()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(navController)
        }
        composable("deviceList") {
            DeviceScreen(deviceViewModel)
        }
        composable("epcReader") {
            ReceiveDataScreen(deviceViewModel)
//            Toast.makeText(navController.context, "この機能は未実装です", Toast.LENGTH_SHORT).show()
        }
    }

}

