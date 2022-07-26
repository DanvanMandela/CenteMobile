package com.craft.silicon.centemobile.util.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import java.io.*
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@Throws(IllegalArgumentException::class)
fun convert(base64Str: String): Bitmap? {
    val decodedBytes: ByteArray = Base64.decode(
        base64Str.substring(base64Str.indexOf(",") + 1),
        Base64.DEFAULT
    )
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun convert(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return Base64.encodeToString(
        outputStream.toByteArray(),
        Base64.DEFAULT
    )
}

fun writeBitmapToFile(applicationContext: Context, bitmap: Bitmap, id: String, path: String): Uri {
    val name = String.format("${id}.png", UUID.randomUUID().toString())
    val outputDir = File(applicationContext.filesDir, path)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val outputFile = File(outputDir, name)
    var out: FileOutputStream? = null
    try {
        out = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /* ignored for PNG */, out)
    } finally {
        out?.let {
            try {
                it.close()
            } catch (ignore: IOException) {
            }

        }
    }
    return Uri.fromFile(outputFile)
}

fun getBitmap(image: String?): Bitmap? {
    val url =
        if (!TextUtils.isEmpty(image)) URL(image) else URL("https://www.vhv.rs/dpng/d/436-4363443_view-user-icon-png-font-awesome-user-circle.png")
    return BitmapFactory.decodeStream(url.openConnection().getInputStream())
}

fun checkForEncode(string: String?): Boolean {
    val pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$"
    val r: Pattern = Pattern.compile(pattern)
    val m: Matcher = r.matcher(string!!)
    return m.find()
}

fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }
    val bitmap: Bitmap? = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        )
    } else {
        Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = bitmap?.let { Canvas(it) }
    if (canvas != null) {
        drawable.setBounds(0, 0, canvas.width, canvas.height)
    }
    if (canvas != null) {
        drawable.draw(canvas)
    }
    return bitmap
}


fun compressImage(image: Bitmap): Bitmap? {
    val stream = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    var options = 100
    while (stream.toByteArray().size / 1024 > 100) { // 100kb,
        stream.reset()
        image.compress(Bitmap.CompressFormat.JPEG, options, stream)
        options -= 10 // 10
    }
    val isBm = ByteArrayInputStream(
        stream.toByteArray()
    )
    return BitmapFactory.decodeStream(isBm, null, null)
}

 fun bitmapFromUri(selectedPhotoUri: Uri, context: Context): Bitmap {
    val bitmap = when {
        Build.VERSION.SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
            context.contentResolver,
            selectedPhotoUri
        )
        else -> {
            val source = ImageDecoder.createSource(context.contentResolver, selectedPhotoUri)
            ImageDecoder.decodeBitmap(source)
        }
    }
    return bitmap
}