package com.craft.silicon.centemobile.view.activity


import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.util.MyActivityResult


import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class ImagePicker : AppCompatActivity() {

    private val activityLauncher: MyActivityResult<Intent, ActivityResult> =
        MyActivityResult.registerActivityForResult(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_picker)
        val view = window.decorView.rootView

        val intent = intent
        if (intent == null) {
            Toast.makeText(this, getString(R.string.option_missing), Toast.LENGTH_SHORT).show()
            return
        }
        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X)
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y)
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION)
        lockAspectRatio = intent.getBooleanExtra(INTENT_LOCK_ASPECT_RATIO, false)
        setBitmapMaxWidthHeight = intent.getBooleanExtra(INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, false)
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth)
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight)

        val requestCode = intent.getIntExtra(
            INTENT_IMAGE_PICKER_OPTION,
            -1
        )

        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage()
        } else {
            chooseImageFromGallery()
        }
    }

    private fun takeCameraImage() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        fileName =
                            System.currentTimeMillis().toString() + ".jpg"
                        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            getCacheImagePath(fileName!!)
                        )
                        if (takePictureIntent.resolveActivity(packageManager) != null) {
//                            activityLauncher!!.launch(
//                                takePictureIntent
//                            ) {
//                                REQUEST_IMAGE_CAPTURE
//                            }
                            startActivityForResult(
                                takePictureIntent,
                                REQUEST_IMAGE_CAPTURE
                            )


                        }

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    private fun chooseImageFromGallery() {
        Dexter.withContext(this)
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        val pickPhoto = Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )

                        // activityLauncher.launch(pickPhoto)

                        startActivityForResult(
                            pickPhoto,
                            REQUEST_GALLERY_IMAGE
                        )

                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun setResultCancelled() {
        val intent = Intent()
        setResult(RESULT_CANCELED, intent)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> if (resultCode == RESULT_OK) {
                cropImage(getCacheImagePath(fileName!!))
            } else {
                setResultCancelled()
            }
            REQUEST_GALLERY_IMAGE -> if (resultCode == RESULT_OK) {
                val imageUri = data?.data
                cropImage(imageUri!!)
            } else {
                setResultCancelled()
            }
            UCrop.REQUEST_CROP -> if (resultCode == RESULT_OK) {
                handleUCropResult(data)
            } else {
                setResultCancelled()
            }
            UCrop.RESULT_ERROR -> {
                val cropError = data?.let { UCrop.getError(it) }
                Log.e(
                    TAG,
                    "Crop error: $cropError"
                )
                setResultCancelled()
            }
            else -> setResultCancelled()
        }
    }


    private fun cropImage(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(
            File(cacheDir, queryName(contentResolver, sourceUri))
        )
        val options = UCrop.Options()
        options.setCompressionQuality(IMAGE_COMPRESSION)
        options.setToolbarColor(ContextCompat.getColor(this, R.color.dar_color_one))
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.dar_color_two))
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.dar_color_one))
        if (lockAspectRatio) options.withAspectRatio(
            ASPECT_RATIO_X.toFloat(),
            ASPECT_RATIO_Y.toFloat()
        )
        if (setBitmapMaxWidthHeight) options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight)
        UCrop.of(sourceUri, destinationUri)
            .withOptions(options)
            .start(this)
    }

    private fun handleUCropResult(data: Intent?) {
        if (data == null) {
            setResultCancelled()
            return
        }
        val resultUri = UCrop.getOutput(data)
        setResultOk(resultUri)
    }

    private fun setResultOk(imagePath: Uri?) {
        val intent = Intent()
        intent.putExtra("path", imagePath)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun getCacheImagePath(fileName: String): Uri {
        val path = File(externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return getUriForFile(this@ImagePicker, "$packageName.provider", image)
    }

    private fun queryName(resolver: ContentResolver, uri: Uri): String {
        val returnCursor = resolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }


    companion object {
        private val TAG: String = ImagePicker::class.java.simpleName
        const val INTENT_IMAGE_PICKER_OPTION = "image_picker_option"
        const val INTENT_ASPECT_RATIO_X = "aspect_ratio_x"
        const val INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y"
        const val INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio"
        const val INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality"
        const val INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height"
        const val INTENT_BITMAP_MAX_WIDTH = "max_width"
        const val INTENT_BITMAP_MAX_HEIGHT = "max_height"


        const val REQUEST_IMAGE_CAPTURE = 0
        const val REQUEST_GALLERY_IMAGE = 1

        private var lockAspectRatio = false
        private var setBitmapMaxWidthHeight = false
        private var ASPECT_RATIO_X = 16
        private var ASPECT_RATIO_Y = 9
        private var bitmapMaxWidth = 1000
        private var bitmapMaxHeight = 1000
        private var IMAGE_COMPRESSION = 80
        var fileName: String? = null


        /**
         * Calling this will delete the images from cache directory
         * useful to clear some memory
         */
        fun clearCache(context: Context) {
            val path = File(context.externalCacheDir, "camera")
            if (path.exists() && path.isDirectory) {
                for (child in path.listFiles()!!) {
                    child.delete()
                }
            }
        }

        fun showImagePickerOptions(context: Context, listener: PickerOptionListener) {

            val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(context)

            val animals = arrayOf(
                context.getString(R.string.camera_from)
//                context.getString(R.string.select_storage)
            )
            builder.setItems(animals) { _, which ->
                when (which) {
                    0 -> listener.onTakeCameraSelected()
                    1 -> listener.onChooseGallerySelected()
                }
            }
            // create and show the alert dialog
            val dialog: android.app.AlertDialog? = builder.create()
            dialog!!.show()
        }
    }

    interface PickerOptionListener {
        fun onTakeCameraSelected()
        fun onChooseGallerySelected()
    }

}