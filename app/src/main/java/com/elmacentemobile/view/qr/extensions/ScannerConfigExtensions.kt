package com.elmacentemobile.view.qr.extensions

import com.elmacentemobile.view.qr.config.ParcelableScannerConfig
import com.elmacentemobile.view.qr.config.ScannerConfig


internal fun ScannerConfig.toParcelableConfig() =
    ParcelableScannerConfig(
        formats = formats,
        stringRes = stringRes,
        drawableRes = drawableRes,
        hapticFeedback = hapticFeedback,
        showTorchToggle = showTorchToggle,
        horizontalFrameRatio = horizontalFrameRatio,
        useFrontCamera = useFrontCamera,
    )