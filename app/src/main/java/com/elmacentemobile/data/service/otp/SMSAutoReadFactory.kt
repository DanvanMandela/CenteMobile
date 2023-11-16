package com.elmacentemobile.data.service.otp

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.MyActivityResult.registerForActivityResult
import com.google.android.gms.auth.api.credentials.Credentials
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
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