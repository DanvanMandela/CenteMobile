package com.craft.silicon.centemobile.view.qr

import com.craft.silicon.centemobile.view.activity.ScanActivity


internal object MlKitErrorHandler {
    @Suppress("UNUSED_PARAMETER", "FunctionOnlyReturningConstant")
    fun isResolvableError(activity: ScanActivity, exception: Exception) =
        false // always false when bundled
}