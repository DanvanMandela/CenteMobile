package com.craft.silicon.centemobile.view.qr.config

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Builder for ScannerConfig used in ScanBarcode ActivityResultContract.
 */
class ScannerConfig internal constructor(
    internal val formats: IntArray,
    internal val stringRes: Int,
    internal val drawableRes: Int?,
    internal val hapticFeedback: Boolean,
    internal val showTorchToggle: Boolean,
    internal val horizontalFrameRatio: Float,
    internal val useFrontCamera: Boolean,
) {

    class Builder {
        private var barcodeFormats: List<BarcodeFormat> = listOf(BarcodeFormat.FORMAT_ALL_FORMATS)
        private var overlayStringRes: Int = 0
        private var overlayDrawableRes: Int? = 0
        private var hapticSuccessFeedback: Boolean = true
        private var showTorchToggle: Boolean = false
        private var horizontalFrameRatio: Float = 1f
        private var useFrontCamera: Boolean = false

        /**
         * Set a list of interested barcode formats. List must not be empty.
         * Reducing the number of supported formats will make the barcode scanner faster.
         */
        fun setBarcodeFormats(formats: List<BarcodeFormat>): Builder =
            apply { barcodeFormats = formats }

        /**
         * Set a string resource used for the scanner overlay.
         */
        fun setOverlayStringRes(@StringRes stringRes: Int): Builder =
            apply { overlayStringRes = stringRes }

        /**
         * Set a drawable resource used for the scanner overlay.
         * If null is passed, no icon will be shown.
         */
        fun setOverlayDrawableRes(@DrawableRes drawableRes: Int?): Builder =
            apply { overlayDrawableRes = drawableRes }

        /**
         * Set the horizontal overlay ratio (default is 1 / square frame).
         */
        fun setHorizontalFrameRatio(ratio: Float): Builder = apply { horizontalFrameRatio = ratio }

        /**
         * Enable (default) or disable haptic feedback when a barcode was detected.
         */
        fun setHapticSuccessFeedback(enable: Boolean): Builder =
            apply { hapticSuccessFeedback = enable }

        /**
         * Show or hide (default) torch/flashlight toggle button.
         */
        fun setShowTorchToggle(enable: Boolean): Builder = apply { showTorchToggle = enable }

        /**
         * Use the front camera.
         */
        fun setUseFrontCamera(enable: Boolean): Builder = apply { useFrontCamera = enable }

        /**
         * Build the BarcodeConfig required by the ScanBarcode ActivityResultContract.
         */
        fun build(): ScannerConfig =
            ScannerConfig(
                barcodeFormats.map { it.value }.toIntArray(),
                overlayStringRes,
                overlayDrawableRes,
                hapticSuccessFeedback,
                showTorchToggle,
                horizontalFrameRatio,
                useFrontCamera,
            )
    }

    companion object {
        /**
         * Kotlin friendly method to build the BarcodeConfig required by the ScanBarcode ActivityResultContract.
         */
        fun build(func: Builder.() -> Unit): ScannerConfig = Builder().apply { func() }.build()
    }
}