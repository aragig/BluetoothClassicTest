package com.apppppp.bluetoothclassictest.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apppppp.bluetoothclassictest.BluetoothHelper
import com.apppppp.bluetoothclassictest.model.BluetoothDeviceInfo
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

class DeviceViewModel : ViewModel() {

    private val bluetoothHelper: BluetoothHelper = BluetoothHelper()
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _devicesInfo = MutableStateFlow<List<BluetoothDeviceInfo>>(emptyList())
    val devicesInfo: StateFlow<List<BluetoothDeviceInfo>> = _devicesInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _receivedData = MutableLiveData<String>()
    val receivedData: LiveData<String> = _receivedData

    private var periodicLoadJob: Job? = null

    fun loadPairedDevicesPeriodically(repeatInterval: Long) {
        periodicLoadJob = viewModelScope.launch {
            while (isActive) {
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
            val pairedDevices = pairedDevicesInfo
            _devicesInfo.value = pairedDevices
            _devicesInfo.value.map { device -> "${device.name} (${device.address})" }

        }
    }


    val pairedDevicesInfo: List<BluetoothDeviceInfo>
        @SuppressLint("MissingPermission")
        get() = bluetoothAdapter?.bondedDevices?.map { device ->
            BluetoothDeviceInfo(
                name = device.name ?: "N/A",
                address = device.address,
                isPaired = true,
                isConnected = false
            )
        } ?: emptyList()

    fun onDeviceClicked(index: Int) {
        // デバイスがクリックされたときの処理
        viewModelScope.launch {
            _isLoading.value = true // ローディング開始

            // addDummyDevice()

            // 接続処理を行う
            withContext(Dispatchers.IO) { // バックグラウンドスレッドでの実行
                try {
                    bluetoothHelper.connectToDevice(_devicesInfo.value[index].address)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }

            _isLoading.value = false // ローディング終了

            val isConnected = bluetoothHelper.isConnected

            if (isConnected) {
                startReceivingData()
//                _receivedData.value = bluetoothHelper.receiveData()
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
    private fun startReceivingData() {
        viewModelScope.launch(Dispatchers.IO) {
            while (bluetoothHelper.isConnected) {
                try {
                    val data = bluetoothHelper.receiveData()
                    if (data != null) {
                        //NOTE LiveDataの場合はpostValueを使用
                        _receivedData.postValue(data)

                        //NOTE StateFlowの場合は値を直接更新
                        // _receivedData.value = data
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
        val dummyDeviceInfo = BluetoothDeviceInfo(
            name = "Dummy Device",
            address = "FF:FF:FF:FF:FF:FF",
            isPaired = false,
            isConnected = false
        )
        _devicesInfo.value = _devicesInfo.value + dummyDeviceInfo
    }

}