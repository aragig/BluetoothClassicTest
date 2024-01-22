package com.apppppp.bluetoothclassictest.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apppppp.bluetoothclassictest.model.BTClientManager
import com.apppppp.bluetoothclassictest.model.BTDeviceInfo
import com.apppppp.bluetoothclassictest.model.BTRecieveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.sql.Timestamp

/**
 * デバイス一覧画面およびデータ受信画面に共通のViewModel
 */
class DeviceViewModel : ViewModel() {

    private var _btClientManager: BTClientManager = BTClientManager()
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val _devicesInfo = MutableStateFlow<List<BTDeviceInfo>>(emptyList())
    val devicesInfo: StateFlow<List<BTDeviceInfo>> = _devicesInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _receivedDataList = MutableStateFlow<List<BTRecieveData>>(emptyList())
    val receivedDataList: StateFlow<List<BTRecieveData>> = _receivedDataList.asStateFlow()

    private var periodicLoadJob: Job? = null


    fun loadPairedDevicesPeriodically(repeatInterval: Long) {
        periodicLoadJob = viewModelScope.launch {
            while (isActive) {
                Log.d("mopi", "loadPairedDevicesPeriodically")
                loadPairedDevices()
                delay(repeatInterval)
            }
        }
    }

    fun cancelPeriodicLoading() {
        periodicLoadJob?.cancel()
    }


    private fun loadPairedDevices() {
        viewModelScope.launch {
            _devicesInfo.value = pairedDevicesInfo
        }
    }


    val pairedDevicesInfo: List<BTDeviceInfo>
        @SuppressLint("MissingPermission")
        get() = bluetoothAdapter?.bondedDevices?.map { device ->
            val isConnected = _btClientManager.isConnected(device.address)
            Log.d("mopi", "device: ${device.address}")
            Log.d("mopi", "isConnected: $isConnected")

            BTDeviceInfo(
                name = device.name ?: "N/A",
                address = device.address,
                isPaired = true,
                isConnected = isConnected
            )
        } ?: emptyList()

    fun onDeviceClicked(index: Int) {
        // デバイスがクリックされたときの処理
        viewModelScope.launch {
            _isLoading.value = true // ローディング開始

            // addDummyDevice()
            val deviceAddress = _devicesInfo.value[index].address

            // 接続処理を行う
            withContext(Dispatchers.IO) { // バックグラウンドスレッドでの実行
                try {
                    _btClientManager.connectToDevice(deviceAddress)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

            _isLoading.value = false // ローディング終了

            val isConnected = _btClientManager.isConnected(deviceAddress)

            if (isConnected) {
                startReceivingData(deviceAddress)
            }


            // 以下のUI更新処理はメインスレッドで行う
            val currentDevices = _devicesInfo.value.toMutableList()
            val updatedDevice = currentDevices[index].copy(isConnected = isConnected)
            currentDevices[index] = updatedDevice
            _devicesInfo.value = currentDevices
        }
    }

    /**
     * 端末からデータ受信開始
     */
    private fun startReceivingData(deviceAddress: String) {
        viewModelScope.launch(Dispatchers.IO) {
            while (_btClientManager.isConnected(deviceAddress)) {
                try {
                    val rawData = _btClientManager.receiveDataFromDevice(deviceAddress)
                    val deviceName: String? = _btClientManager.deviceName(deviceAddress)

                    if (rawData != null) {
                        /** NOTE
                         * LiveDataの場合はpostValueを使用
                         * StateFlowの場合は値を直接更新
                         */

                        val recieveData = BTRecieveData(
                            deviceName,
                            deviceAddress,
                            rawData,
                            Timestamp(System.currentTimeMillis())
                        )
                        _receivedDataList.value += listOf(recieveData)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    break
                }
            }
        }
    }

    // StateFlowで更新されるかどうかのテスト用
    private fun addDummyDevice() {
        val dummyDeviceInfo = BTDeviceInfo(
            name = "Dummy Device",
            address = "FF:FF:FF:FF:FF:FF",
            isPaired = false,
            isConnected = false
        )
        _devicesInfo.value = _devicesInfo.value + dummyDeviceInfo
    }

}