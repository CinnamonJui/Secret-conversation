package com.gmail.rager1958.playnearbyapi

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_chat_room.*

class ChatRoom : AppCompatActivity() {

    private lateinit var mApplication: NearByConnection
    private lateinit var myCodeName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        mApplication = application as NearByConnection
        myCodeName = mApplication.myCodeName
        mApplication.chatHistoryAdapter = MessageAdapter(this, myCodeName)
        chatRoom.adapter = mApplication.chatHistoryAdapter

        with(mApplication) {
            startAdvertising()
            startDiscovery()
        }

        sendMsgBtn.setOnClickListener {
            val msg = messageBox.text.toString().trimEnd()
            if (msg.isEmpty()) return@setOnClickListener

            mApplication.chatHistoryAdapter.newMessage(myCodeName, msg)
            mApplication.sendMsg(msg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        chatRoom.adapter = null
        mApplication.connectionsClient?.stopAllEndpoints()
    }
}
