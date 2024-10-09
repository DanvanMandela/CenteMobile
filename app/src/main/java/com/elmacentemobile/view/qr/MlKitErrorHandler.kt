package com.elmacentemobile.view.qr

import com.elmacentemobile.view.activity.ScanActivity


internal object MlKitErrorHandler {
    @Suppress("UNUSED_PARAMETER", "FunctionOnlyReturningConstant")
    fun isResolvableError(activity: ScanActivity, exception: Exception) =
        false // always false when bundled
}