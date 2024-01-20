package com.apppppp.bluetoothclassictest.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.apppppp.bluetoothclassictest.viewmodel.DeviceViewModel

@Composable
fun ReceiveDataScreen(deviceViewModel: DeviceViewModel) {

    // LiveDataをCompose Stateに変換
    val receivedData by deviceViewModel.receivedData.observeAsState("")

    ReceiveDataView(message = receivedData)

}
