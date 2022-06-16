package com.craft.silicon.centemobile.util.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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