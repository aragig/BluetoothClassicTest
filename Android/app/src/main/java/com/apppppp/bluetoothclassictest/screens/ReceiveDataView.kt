package com.apppppp.bluetoothclassictest.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun ReceiveDataView(message: String) {

    Text(
        text = message,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )

}