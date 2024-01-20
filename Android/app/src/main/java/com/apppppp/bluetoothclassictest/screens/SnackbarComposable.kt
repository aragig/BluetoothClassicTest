package com.apppppp.bluetoothclassictest.screens


import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text


@Composable
fun PermissionDeniedSnackbar(onSettingsClick: () -> Unit) {
    Snackbar(
        action = {
            Button(onClick = onSettingsClick) {
                Text("設定へ")
            }
        }
    ) {
        Text("アプリのアクセス許可 が必要です。設定画面から許可してください。")
    }
}

@Composable
fun PairedDeviceNotFoundSnackbar() {
    Snackbar() {
        Text("Bluetoothデバイスとペアリングしてください。")
    }
}