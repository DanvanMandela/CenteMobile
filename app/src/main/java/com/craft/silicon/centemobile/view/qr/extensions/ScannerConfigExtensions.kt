package com.craft.silicon.centemobile.view.qr.extensions

import com.craft.silicon.centemobile.view.qr.config.ParcelableScannerConfig
import com.craft.silicon.centemobile.view.qr.config.ScannerConfig


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