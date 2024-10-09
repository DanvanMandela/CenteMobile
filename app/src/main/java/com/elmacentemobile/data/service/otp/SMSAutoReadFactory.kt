package com.elmacentemobile.data.service.otp

import android.app.Activity
import android.content.Context
import com.elmacentemobile.util.AppLogger
import com.google.android.gms.auth.api.phone.SmsRetriever
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SMSAutoReadFactory @Inject constructor(val context: Context) : SMSDatasource {
    companion object {
        private const val CREDENTIAL_PICKER_REQUEST = 1
    }

    override fun startF() {
        val client = SmsRetriever.getClient(context)
        client.startSmsUserConsent(null).addOnSuccessListener {
            AppLogger.instance.appLog(
                SMSAutoReadFactory::class.java.simpleName,
                "Auto SMS now active..."
            )
        }.addOnFailureListener { it.printStackTrace() }
    }


    override fun requestHint(activity: Activity) {


    }

}