package com.elmacentemobile.view.activity.image

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.elmacentemobile.R
import com.elmacentemobile.data.source.constants.Constants
import com.elmacentemobile.databinding.ActivityImagePickerUtilBinding
import com.elmacentemobile.util.AppLogger
import com.elmacentemobile.util.MyActivityResult
import com.elmacentemobile.util.callbacks.AppCallbacks
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class ImagePickerUtil : AppCompatActivity(), AppCallbacks {

    private var currentPhotoUri: Uri = Uri.EMPTY
    private val activityLauncher: MyActivityResult<Intent, ActivityResult> =
        MyActivityResult.registerActivityForResult(this)

    private lateinit var binding: ActivityImagePickerUtilBinding
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBinding()
        pickImage()
    }

    private fun pickImage() {
        activityLauncher.launch(openIntentChooserForImageSources()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, ImageCropUtil::class.java)
                val imageUri = getPickImageResultUri(it.data)
                AppLogger.instance.appLog(
                    ImagePickerUtil::class.java.simpleName,
                    "$imageUri"
                )
                intent.putExtra(ImageCropUtil.EXTRA_IMAGE_URI, imageUri.toString())
                cropImage(intent)
            } else {
                AppLogger.instance.appLog(
                    ImagePickerUtil::class.java.simpleName,
                    "Error:${it.resultCode}"
                )
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun cropImage(intent: Intent) {
        activityLauncher.launch(intent) {
            if (it.resultCode == Activity.RESULT_OK) {
                val imagePath: String = File(
                    it.data?.getStringExtra(ImageCropUtil.CROPPED_IMAGE_PATH),
                    "image.png"
                ).absolutePath
                val result = Intent()
                result.putExtra("image_path", imagePath)
                setResult(RESULT_OK, result)
                finish()
            } else {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image_picker_util)
    }


    private fun imagePickerLauncher(): Intent {

        val outputFileUri = getCaptureImageOutputUri()
        val intents = mutableListOf<Intent>()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        val cameras = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                captureIntent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            packageManager.queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }

        AppLogger.instance.appLog("URI", outputFileUri.toString())

        for (r in cameras) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(r.activityInfo.packageName, r.activityInfo.name)
            intent.setPackage(r.activityInfo.packageName)

            if (outputFileUri != null) {
                intent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    outputFileUri
                )
            }
            intents.add(intent)
        }

        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        val galleries = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                galleryIntent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            packageManager.queryIntentActivities(galleryIntent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        for (g in galleries) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(g.activityInfo.packageName, g.activityInfo.name)
            intent.setPackage(g.activityInfo.packageName)
            intents.add(intent)
        }
        var mainIntent: Intent = intents[intents.size - 1]

        for (i in intents) {
            if (intent.component?.className.equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent
                break
            }
        }
        intents.remove(mainIntent)

        val chooserIntent = Intent.createChooser(mainIntent, "Select source")

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())


        return chooserIntent

    }

    private fun openIntentChooserForImageSources(): Intent {
        fileName = System.currentTimeMillis().toString() + ".jpg"

        // creating gallery intent
        val galleryIntent =
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
            )

        // creating camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        cameraIntent.also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.packageManager)?.also {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    getCacheImagePath(fileName!!)
                )
            }
        }

        val intentChooser = Intent.createChooser(
            if (Constants.Data.TEST) galleryIntent else cameraIntent,
            "Select an app"
        )
        intentChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))
        return intentChooser
    }


    private fun getCaptureImageOutputUri(): Uri? {
        var outputFileUri: Uri? = null
        val getImage: File? = externalCacheDir
        if (getImage != null) {
            outputFileUri = Uri.fromFile(File(getImage.path, "pickImageResult.jpeg"))
        }
        return outputFileUri
    }

    private fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCacheImagePath(fileName!!) else data!!.data
    }


    private fun getCacheImagePath(fileName: String): Uri {
        val path = File(externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return FileProvider.getUriForFile(
            this@ImagePickerUtil,
            "$packageName.provider", image
        )

    }


}