package com.apppppp.bluetoothclassictest.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.apppppp.bluetoothclassictest.screens.views.ReceiveDataView
import com.apppppp.bluetoothclassictest.viewmodel.DeviceViewModel

@Composable
fun ReceiveDataScreen(deviceViewModel: DeviceViewModel) {

    val receivedDataList = deviceViewModel.receivedDataList.collectAsState()
    ReceiveDataView(receivedDataList = receivedDataList.value)

}
