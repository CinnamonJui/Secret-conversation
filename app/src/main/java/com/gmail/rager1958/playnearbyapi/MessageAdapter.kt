package com.gmail.rager1958.playnearbyapi

import android.app.Service
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.ArrayList

class MessageAdapter(context: Context, private val myCodeName: String) : BaseAdapter() {
    private val messageListArray = ArrayList<MsgEntry>()
    private val inflater =
        context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    public fun newMessage(who: String, msg: String) {
        messageListArray.add(MsgEntry(who, msg))
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val theMsg = messageListArray[position]
        val isMe = theMsg.who == myCodeName
        lateinit var ret: View
        val whichLayout = if (isMe) R.layout.outgoing_msg_view else R.layout.incoming_msg_view
        ret = inflater.inflate(whichLayout, parent, false)
        ret.findViewById<TextView>(R.id.msg).text = theMsg.msg

        return ret
    }

    override fun getItem(position: Int) = messageListArray[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = messageListArray.size
}

data class MsgEntry(val who: String, val msg: String)