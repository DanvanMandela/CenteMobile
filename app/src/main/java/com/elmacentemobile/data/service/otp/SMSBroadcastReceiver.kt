package com.elmacentemobile.data.service.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.elmacentemobile.data.receiver.otpExtract
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.util.AppLogger
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SMSBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var source: StorageDataSource


    override fun onReceive(context: Context?, intent: Intent?) {
        AppLogger().appLog(
            SMSBroadcastReceiver::class.java.simpleName
                    + "SMS" + "OTP", "INIT"
        )
        if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status
            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message =
                        extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                    AppLogger().appLog(
                        SMSBroadcastReceiver::class.java.simpleName
                                + "SMS" + "OTP", "$message"
                    )
                    source.setOtp(message?.otpExtract())
                }
                CommonStatusCodes.TIMEOUT -> AppLogger().appLog(
                    SMSBroadcastReceiver::class.java.simpleName,
                    "OTP: Timeout"
                )
            }
        }
    }

}

