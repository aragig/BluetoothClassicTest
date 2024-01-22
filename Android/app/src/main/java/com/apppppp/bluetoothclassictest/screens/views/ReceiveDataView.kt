package com.apppppp.bluetoothclassictest.screens.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apppppp.bluetoothclassictest.model.BTRecieveData
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun ReceiveDataView(receivedDataList: List<BTRecieveData>) {
    // リストを逆順にする
    val reversedList = receivedDataList.asReversed()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        itemsIndexed(reversedList) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                val deviceInfo = if (item.deviceName != null) {
                    item.deviceName
                } else {
                    item.deviceAddress
                }
                Text(
                    text = deviceInfo,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Gray
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )
                Text(
                    text = item.data,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .weight(2f)
                )
            }
        }
    }
}
