package com.craft.silicon.centemobile.view.qr

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.craft.silicon.centemobile.view.activity.ScanActivity
import com.craft.silicon.centemobile.view.qr.QRResult.*
import com.craft.silicon.centemobile.view.qr.extensions.RESULT_ERROR
import com.craft.silicon.centemobile.view.qr.extensions.RESULT_MISSING_PERMISSION
import com.craft.silicon.centemobile.view.qr.extensions.getRootException
import com.craft.silicon.centemobile.view.qr.extensions.toQuickieContentType


class ScanQRCode : ActivityResultContract<Nothing?, QRResult>() {

    override fun createIntent(context: Context, input: Nothing?): Intent =
        Intent(context, ScanActivity::class.java)

    override fun parseResult(resultCode: Int, intent: Intent?): QRResult {
        return when (resultCode) {
            RESULT_OK -> QRSuccess(intent.toQuickieContentType())
            RESULT_CANCELED -> QRUserCanceled
            RESULT_MISSING_PERMISSION -> QRMissingPermission
            RESULT_ERROR -> QRError(intent.getRootException())
            else -> QRError(IllegalStateException("Unknown activity result code $resultCode"))
        }
    }
}