package com.apppppp.bluetoothclassictest.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.UUID

/**
 * Bluetoothのデバイスとの接続を管理するクラス
 */
class BTClient {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    // SPPのUUIDを定義
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var _isConnected = false

    val isConnected: Boolean
        get() = _isConnected


    val deviceName: String?
        @SuppressLint("MissingPermission")
        get() = socket?.remoteDevice?.name

    val deviceAddress: String?
        @SuppressLint("MissingPermission")
        get() = socket?.remoteDevice?.address

    @SuppressLint("MissingPermission")
    fun connectToDevice(deviceAddress: String) {
        if(_isConnected) {
            closeConnection()
            return
        }
        val device = bluetoothAdapter?.getRemoteDevice(deviceAddress)
            ?: throw IllegalArgumentException("No device found with address $deviceAddress")
        socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
        try {
            socket?.connect() // IOExceptionがスローされる可能性がある
            _isConnected = true
        } catch (e: IOException) {
            e.printStackTrace()
            _isConnected = false
        }
    }

    fun receiveData(): String? {
        return try {
            socket?.inputStream?.bufferedReader()?.readLine()
        } catch (e: IOException) {
            e.printStackTrace()
            _isConnected = false
            null
        }
    }

    fun sendData(data: String) {
        try {
            socket?.outputStream?.write(data.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            _isConnected = false
        }
    }

    fun closeConnection() {
        try {
            socket?.inputStream?.close()
            socket?.outputStream?.close()
            socket?.close()
            _isConnected = false
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
