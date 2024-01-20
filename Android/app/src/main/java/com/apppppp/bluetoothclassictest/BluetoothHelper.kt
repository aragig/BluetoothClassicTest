package com.apppppp.bluetoothclassictest

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import com.apppppp.bluetoothclassictest.model.BluetoothDeviceInfo
import java.io.IOException
import java.util.UUID

class BluetoothHelper {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    // SPPのUUIDを定義
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    val isBluetoothSupported: Boolean
        get() = bluetoothAdapter != null

    val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled ?: false

    val isConnected: Boolean
        get() = socket?.isConnected ?: false

    @SuppressLint("MissingPermission")
    fun connectToDevice(deviceAddress: String) {
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
            ?: throw IllegalArgumentException("No device found with address $deviceAddress")
        socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
        socket?.connect() // IOExceptionがスローされる可能性がある
    }

    fun sendData(data: String) {
        try {
            socket?.outputStream?.write(data.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun receiveData(): String? {
        return try {
            socket?.inputStream?.bufferedReader()?.readLine()
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun closeConnection() {
        try {
            socket?.inputStream?.close()
            socket?.outputStream?.close()
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
