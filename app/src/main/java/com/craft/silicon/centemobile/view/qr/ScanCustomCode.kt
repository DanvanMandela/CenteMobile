package com.craft.silicon.centemobile.view.qr

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.craft.silicon.centemobile.view.activity.ScanActivity
import com.craft.silicon.centemobile.view.qr.QRResult.*
import com.craft.silicon.centemobile.view.qr.config.ScannerConfig
import com.craft.silicon.centemobile.view.qr.extensions.*


class ScanCustomCode : ActivityResultContract<ScannerConfig, QRResult>() {

    override fun parseResult(resultCode: Int, intent: Intent?): QRResult {
        return when (resultCode) {
            RESULT_OK -> QRSuccess(intent.toQuickieContentType())
            RESULT_CANCELED -> QRUserCanceled
            RESULT_MISSING_PERMISSION -> QRMissingPermission
            RESULT_ERROR -> QRError(intent.getRootException())
            else -> QRError(IllegalStateException("Unknown activity result code $resultCode"))
        }
    }

    override fun createIntent(context: Context, input: ScannerConfig): Intent {
        return Intent(context, ScanActivity::class.java).apply {
            putExtra(EXTRA_CONFIG, input.toParcelableConfig())
        }
    }
}