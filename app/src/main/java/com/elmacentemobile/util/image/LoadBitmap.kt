package com.elmacentemobile.util.image

import android.annotation.TargetApi
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.NonNull
import java.io.IOException


class LoadBitmap {


    companion object {
        private const val TAG = "LoadBitmap"
        private var INSTANCE: LoadBitmap? = null

        @get:Synchronized
        val instance: LoadBitmap
            get() {
                if (INSTANCE == null) {
                    INSTANCE = LoadBitmap()
                }
                return INSTANCE!!
            }
    }

    private fun getBitmapLegacy(
        @NonNull contentResolver: ContentResolver,
        @NonNull fileUri: Uri
    ): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    @TargetApi(Build.VERSION_CODES.P)
    private fun getBitmapImageDecoder(
        @NonNull contentResolver: ContentResolver,
        @NonNull fileUri: Uri
    ): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    fun getBitmap(@NonNull contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        if (fileUri == null) {
            Log.i(TAG, "returning null because URI was null")
            return null
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getBitmapImageDecoder(contentResolver, fileUri)
        } else {
            getBitmapLegacy(contentResolver, fileUri)
        }
    }
}