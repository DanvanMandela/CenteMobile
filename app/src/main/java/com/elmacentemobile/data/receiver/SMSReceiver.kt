package com.elmacentemobile.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.elmacentemobile.data.source.constants.StatusEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class SMSReceiver : BroadcastReceiver() {

    private lateinit var smsData: SMSData

    fun setOnSMS(smsData: SMSData) {
        this.smsData = smsData
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        val status = intent.getStringExtra("STATUS")
        val sms = intent.getStringExtra("SMSbody")
        if ("SMSReceived" == action) {
            if (status == StatusEnum.SUCCESS.type) {
                smsData.onSMS(sms!!.trim())
            }
        }
    }

    companion object {
        private val TAG = SMSReceiver::class.java.simpleName
    }
}

interface SMSData {
    fun onSMS(sms: String)
}