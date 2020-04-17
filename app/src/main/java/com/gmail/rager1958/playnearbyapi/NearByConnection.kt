package com.gmail.rager1958.playnearbyapi

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import java.nio.charset.Charset

class NearByConnection : Application() {
    var connectionsClient: ConnectionsClient? = null
        private set
    val myCodeName by lazy {
        CodeNameGenerator.genCodeName().also { codeName ->
            Log.i(TAG, "Code name: $codeName")
        }
    }
    lateinit var chatHistoryAdapter: MessageAdapter
    private val endpointList = HashSet<String>()

    fun prepareConnectionClient(context: Context) {
        connectionsClient = Nearby.getConnectionsClient(context)
    }

    val chatRoomUserSet = HashSet<String>(3).also {
        it.add(myCodeName)
    }

    fun sendMsg(msg: String) {
        endpointList.map { endPoint ->
            connectionsClient?.sendPayload(
                endPoint,
                Payload.fromBytes(msg.toByteArray(Charsets.UTF_8))
            )
        }
    }

    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.i(TAG, "onEndpointFound: User \"$endpointId\"")
            connectionsClient?.requestConnection(
                myCodeName,
                endpointId,
                connectionLifecycleCallback
            )
        }

        override fun onEndpointLost(endpointId: String) {
            Log.i(TAG, "onEndpointLost: \"$endpointId\"")
        }
    }
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                Log.i(
                    TAG,
                    "onConnectionResult: connection successful"
                )
                endpointList.add(endpointId)
                connectionsClient?.stopDiscovery()
                connectionsClient?.stopAdvertising()
            } else {
                Log.i(
                    TAG,
                    "onConnectionResult: connection failed"
                )
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.i(TAG, "User \"$endpointId\" disconnect")
            endpointList.remove(endpointId)
        }

        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            connectionsClient?.acceptConnection(endpointId, payloadCallback)
        }
    }
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            chatHistoryAdapter.newMessage(endpointId, String(payload.asBytes()!!, Charsets.UTF_8))
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            Log.i(TAG, "onPayloadTransferUpdate: $endpointId\n ${update.totalBytes}")
        }
    }

    fun startDiscovery() {
        if (connectionsClient == null)
            throw RuntimeException("connectionsClient not initiated")

        val discoveryOptions = DiscoveryOptions.Builder().apply {
            setStrategy(Strategy.P2P_CLUSTER)
        }.build()
        Log.i(TAG, "startDiscovery")
        connectionsClient?.startDiscovery(packageName, endpointDiscoveryCallback, discoveryOptions)
    }

    fun startAdvertising() {
        if (connectionsClient == null)
            throw RuntimeException("connectionsClient not initiated")

        val advertisingOptions = AdvertisingOptions.Builder().apply {
            setStrategy(Strategy.P2P_CLUSTER)
        }.build()

        Log.i(TAG, "startAdvertising")
        connectionsClient?.startAdvertising(
            myCodeName,
            packageName,
            connectionLifecycleCallback,
            advertisingOptions
        )
    }

    companion object {
        const val TAG = "NearByConnection"
    }
}