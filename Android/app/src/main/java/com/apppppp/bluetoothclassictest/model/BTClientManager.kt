package com.apppppp.bluetoothclassictest.model


/**
 * デバイスとの接続を複数管理するクラス
 */
class BTClientManager {
    private val clients: MutableMap<String, BTClient> = mutableMapOf()

    fun isConnected(deviceAddress: String): Boolean {
        return clients[deviceAddress]?.isConnected ?: false
    }
    fun connectToDevice(deviceAddress: String) {
        val client = BTClient()
        client.connectToDevice(deviceAddress)
        clients[deviceAddress] = client
    }

    fun deviceName(deviceAddress: String): String? {
        return clients[deviceAddress]?.deviceName ?: "N/A"
    }

    fun sendDataToDevice(deviceAddress: String, data: String) {
        val client = clients[deviceAddress]
        client?.sendData(data)
    }

    fun receiveDataFromDevice(deviceAddress: String): String? {
        val client = clients[deviceAddress]
        return client?.receiveData()
    }

    fun disconnectDevice(deviceAddress: String) {
        val client = clients[deviceAddress]
        client?.closeConnection()
        clients.remove(deviceAddress)
    }

    fun disconnectAllDevices() {
        clients.keys.toList().forEach { disconnectDevice(it) }
    }
}