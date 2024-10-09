package com.elmacentemobile.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SMSMonitor : BroadcastReceiver() {
    private var otpListener: OTPObserver? = null

    fun setOTPListener(otpListener: OTPObserver?) {
        this.otpListener = otpListener
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras
            val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?
            when (status!!.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val sms = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
                    sms?.let {
                        val p = Pattern.compile("\\d+")
                        val m = p.matcher(it)
                        if (m.find()) {
                            val otp = m.group()
                            if (otpListener != null) {
                                otpListener!!.onReceived(otp)
                            }
                        }
                    }
                }
            }
        }

//        if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
//            val extra = intent.extras
//            val status = extra?.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
//            AppLogger.instance.appLog("SMS",Gson().toJson(status))
//            when (status?.statusCode) {
//                CommonStatusCodes.SUCCESS -> {
//
//                }
//                else -> {}
//            }
//        }
    }

    interface OTPObserver {
        fun onReceived(otp: String?)
    }
}