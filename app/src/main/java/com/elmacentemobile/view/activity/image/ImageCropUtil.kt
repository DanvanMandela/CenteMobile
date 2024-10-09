package com.elmacentemobile.view.activity.image


import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.canhub.cropper.CropImageView
import com.elmacentemobile.R
import com.elmacentemobile.databinding.ActivityImageCropUtilBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.callbacks.AppCallbacks
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ImageCropUtil : AppCompatActivity(), AppCallbacks, CropImageView.OnCropImageCompleteListener {

    private var mCropImageView: CropImageView? = null
    private var croppedImage: Bitmap? = null

    private lateinit var binding: ActivityImageCropUtilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        if (!intent.hasExtra(EXTRA_IMAGE_URI)) {
            cropFailed()
            return
        }
        isFixedAspectRatio = intent.getBooleanExtra(FIXED_ASPECT_RATIO, false)
        mAspectRatioX = intent.getIntExtra(EXTRA_ASPECT_RATIO_X, DEFAULT_ASPECT_RATIO_VALUES)
        mAspectRatioY = intent.getIntExtra(EXTRA_ASPECT_RATIO_Y, DEFAULT_ASPECT_RATIO_VALUES)

        mCropImageView = binding.CropImageView

        mCropImageView?.setFixedAspectRatio(isFixedAspectRatio)
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        if (savedInstanceState == null) {
            mCropImageView?.setImageUriAsync(imageUri)
        }
        setToolbar()
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            this.setNavigationOnClickListener {
                cropFailed()
            }
            this.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.cropAction -> {
                        mCropImageView?.croppedImageAsync()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_crop_util)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(ASPECT_RATIO_X, mAspectRatioX)
        outState.putInt(ASPECT_RATIO_Y, mAspectRatioY)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mAspectRatioX = savedInstanceState.getInt(ASPECT_RATIO_X)
        mAspectRatioY = savedInstanceState.getInt(ASPECT_RATIO_Y)
    }

    private fun cropFailed() {
        AppLogger.instance.appLog(
            ImageCropUtil::class.java.simpleName,
            getString(R.string.crop_failed)
        )
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onStart() {
        super.onStart()
        mCropImageView?.setOnCropImageCompleteListener(this)

    }

    override fun onStop() {
        super.onStop()
        mCropImageView?.setOnSetImageUriCompleteListener(null)
    }

    companion object {
        private const val DEFAULT_ASPECT_RATIO_VALUES = 100
        const val CROPPED_IMAGE_PATH = "cropped_image_path"
        const val EXTRA_IMAGE_URI = "cropped_image_path"
        const val FIXED_ASPECT_RATIO = "extra_fixed_aspect_ratio"
        const val EXTRA_ASPECT_RATIO_X = "extra_aspect_ratio_x"
        const val EXTRA_ASPECT_RATIO_Y = "extra_aspect_ratio_y"
        private const val ASPECT_RATIO_X = "ASPECT_RATIO_X"
        private const val ASPECT_RATIO_Y = "ASPECT_RATIO_Y"
        private var mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES
        private var mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES
        private var isFixedAspectRatio = false
    }


    private fun createFileInInternalFolder(bitmap: Bitmap?): String {
        val internalFolderPath = this.getDir("images", MODE_PRIVATE)
        val fileName = File(internalFolderPath, "image.png")
        if (!fileName.exists()) {
            fileName.delete()
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(fileName)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 70, fos)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            fos?.close()
        }
        return internalFolderPath.absolutePath
    }

    override fun onCropImageComplete(
        view: CropImageView,
        result: CropImageView.CropResult
    ) {
        if (result.error == null) {
            croppedImage = view.croppedImage
            try {
                val path: String = createFileInInternalFolder(croppedImage)
                AppLogger.instance.appLog("CROPPER:Path", path)
                val resultIntent = Intent()
                resultIntent.putExtra(CROPPED_IMAGE_PATH, path)
                setResult(RESULT_OK, resultIntent)
                finish()
            } catch (e: IOException) {
                e.printStackTrace()
                cropFailed()
            }
        } else {
            cropFailed()
        }
    }


}