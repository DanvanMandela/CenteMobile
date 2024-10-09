package com.elmacentemobile.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.elmacentemobile.R
import com.elmacentemobile.databinding.ActivityScanBinding
import com.elmacentemobile.util.callbacks.AppCallbacks
import com.elmacentemobile.view.qr.MlKitErrorHandler
import com.elmacentemobile.view.qr.QRCodeAnalyzer
import com.elmacentemobile.view.qr.ScanCode
import com.elmacentemobile.view.qr.config.ParcelableScannerConfig
import com.elmacentemobile.view.qr.extensions.*
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class ScanActivity : AppCompatActivity(), ScanCode, AppCallbacks {
    private lateinit var binding: ActivityScanBinding
    private lateinit var analysisExecutor: ExecutorService
    private var barcodeFormats = intArrayOf(Barcode.FORMAT_QR_CODE)
    private var hapticFeedback = true
    private var showTorchToggle = false
    private var useFrontCamera = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        setBinding()
    }

    override fun setBinding() {
        val appThemeLayoutInflater = applicationInfo.theme.let { appThemeRes ->
            if (appThemeRes != 0) layoutInflater.cloneInContext(
                ContextThemeWrapper(
                    this,
                    appThemeRes
                )
            ) else layoutInflater
        }
        binding = ActivityScanBinding.inflate(appThemeLayoutInflater)
        setContentView(binding.root)

        setupEdgeToEdgeUI()
        applyScannerConfig()

        analysisExecutor = Executors.newSingleThreadExecutor()

        requestCameraPermissionIfMissing { granted ->
            if (granted) {
                startCamera()
            } else {
                setResult(RESULT_MISSING_PERMISSION, null)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        analysisExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .build()
                .also {
                    it.setAnalyzer(
                        analysisExecutor,
                        QRCodeAnalyzer(
                            barcodeFormats = barcodeFormats,
                            onSuccess = { barcode ->
                                it.clearAnalyzer()
                                onSuccess(barcode)
                            },
                            onFailure = { exception -> onFailure(exception) },
                            onPassCompleted = { failureOccurred -> onPassCompleted(failureOccurred) }
                        )
                    )
                }

            cameraProvider.unbindAll()
            try {
                val cameraSelector = if (useFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                val camera =
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
                binding.overlayView.visibility = View.VISIBLE
                if (showTorchToggle && camera.cameraInfo.hasFlashUnit()) {
                    binding.overlayView.setTorchVisibilityAndOnClick(true) {
                        camera.cameraControl.enableTorch(
                            it
                        )
                    }
                    camera.cameraInfo.torchState.observe(this) {
                        binding.overlayView.setTorchState(
                            it == TorchState.ON
                        )
                    }
                } else {
                    binding.overlayView.setTorchVisibilityAndOnClick(false)
                }
            } catch (e: Exception) {
                binding.overlayView.visibility = View.INVISIBLE
                onFailure(e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun onSuccess(result: Barcode) {
        binding.overlayView.isHighlighted = true
        if (hapticFeedback) {
            binding.overlayView.performHapticFeedback(
                HapticFeedbackConstants.KEYBOARD_TAP,
                HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING or HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            )
        }
        setResult(
            Activity.RESULT_OK,
            Intent().apply {
                putExtra(EXTRA_RESULT_VALUE, result.rawValue)
                putExtra(EXTRA_RESULT_TYPE, result.valueType)
                putExtra(EXTRA_RESULT_PARCELABLE, result.toParcelableContentType())
            }
        )
        finish()
    }

    private fun onFailure(exception: Exception) {
        setResult(RESULT_ERROR, Intent().putExtra(EXTRA_RESULT_EXCEPTION, exception))
        if (!MlKitErrorHandler.isResolvableError(this, exception)) finish()
    }

    private fun onPassCompleted(failureOccurred: Boolean) {
        if (!isFinishing) binding.overlayView.isLoading = failureOccurred
    }

    private fun setupEdgeToEdgeUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(binding.overlayView) { v, insets ->
            insets.getInsets(WindowInsetsCompat.Type.systemBars())
                .let { v.setPadding(it.left, it.top, it.right, it.bottom) }
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun applyScannerConfig() {
        intent?.getParcelableExtra<ParcelableScannerConfig>(EXTRA_CONFIG)?.let {
            barcodeFormats = it.formats
            binding.overlayView.setCustomText(it.stringRes)
            binding.overlayView.setCustomIcon(it.drawableRes)
            binding.overlayView.setHorizontalFrameRatio(it.horizontalFrameRatio)
            hapticFeedback = it.hapticFeedback
            showTorchToggle = it.showTorchToggle
            useFrontCamera = it.useFrontCamera
        }
    }

    private fun requestCameraPermissionIfMissing(onResult: ((Boolean) -> Unit)) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            onResult(true)
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { onResult(it) }.launch(
                Manifest.permission.CAMERA
            )
        }
    }
}