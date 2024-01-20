package com.apppppp.bluetoothclassictest.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.apppppp.bluetoothclassictest.model.BluetoothDeviceInfo

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)), // 半透明の黒背景
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun DeviceListView(devicesInfo: List<BluetoothDeviceInfo>, onDeviceClick: (Int) -> Unit, isLoading: Boolean) {
    Box {
        if(devicesInfo.isEmpty()) {
            PairedDeviceNotFoundSnackbar()
        }

        LazyColumn {
            itemsIndexed(devicesInfo) { index, deviceInfo ->
                Text(
                    text = "${deviceInfo.name} (${deviceInfo.address})",
                    modifier = Modifier
                        .fillMaxWidth()
                        .background((deviceInfo.isConnected).let { if (it) Color.Green else Color.White })
                        .padding(16.dp)
                        .clickable(enabled = !isLoading) { onDeviceClick(index) }
                )
            }
        }

        if (isLoading) {
            LoadingView(Modifier.fillMaxSize()) // ローディング表示を最前面に
        }
    }
}