package com.craft.silicon.centemobile.view.qr

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

internal class QRCodeAnalyzer(
    private val barcodeFormats: IntArray,
    private val onSuccess: ((Barcode) -> Unit),
    private val onFailure: ((Exception) -> Unit),
    private val onPassCompleted: ((Boolean) -> Unit)
) : ImageAnalysis.Analyzer {

    private val barcodeScanner by lazy {
        val optionsBuilder = if (barcodeFormats.size > 1) {
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(barcodeFormats.first(), *barcodeFormats.drop(1).toIntArray())
        } else {
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(barcodeFormats.firstOrNull() ?: Barcode.FORMAT_UNKNOWN)
        }
        BarcodeScanning.getClient(optionsBuilder.build())
    }

    @Volatile
    private var failureOccurred = false
    private var failureTimestamp = 0L

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        if (imageProxy.image == null) return

        // throttle analysis if error occurred in previous pass
        if (failureOccurred && System.currentTimeMillis() - failureTimestamp < 1000L) {
            imageProxy.close()
            return
        }

        failureOccurred = false
        barcodeScanner.process(imageProxy.toInputImage())
            .addOnSuccessListener { codes ->
                codes.mapNotNull { it }.firstOrNull()?.let { onSuccess(it) }
            }
            .addOnFailureListener {
                failureOccurred = true
                failureTimestamp = System.currentTimeMillis()
                onFailure(it)
            }
            .addOnCompleteListener {
                onPassCompleted(failureOccurred)
                imageProxy.close()
            }
    }

    @ExperimentalGetImage
    @Suppress("UnsafeCallOnNullableType")
    private fun ImageProxy.toInputImage() =
        InputImage.fromMediaImage(image!!, imageInfo.rotationDegrees)
}

interface ScanCode {
    fun onScan(content: QRContent?) {
        throw Exception("Not implemented")
    }
}