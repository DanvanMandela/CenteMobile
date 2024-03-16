package com.elmacentemobile.data.source.remote.helper

import android.util.Base64
import com.elmacentemobile.data.source.constants.Constants
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.GZIPOutputStream

object DynamicURL {
    const val LIVE = "https://app.craftsilicon.com/CentemobileAuthDynamic/"
}


object DynamicTestURL {
    const val UAT = "https://elmaauthserver.azurewebsites.net/"
}


fun liveTest(): String {
    return if (Constants.Data.TEST) "https://elmaauthserver.azurewebsites.net/"
    else "https://app.craftsilicon.com/"
}


var ROUTE_BASE_URL = if (!Constants.Data.TEST) DynamicURL.LIVE else DynamicTestURL.UAT

fun String.compressString(): String? {
    val compressed: ByteArray = compress(this)!!
    return Base64.encodeToString(compressed, Base64.NO_WRAP)
}

fun compress(string: String): ByteArray? {
    return try {
        val os = ByteArrayOutputStream(string.length)
        val gos = GZIPOutputStream(os)
        gos.write(string.toByteArray())
        gos.close()
        val compressed: ByteArray = os.toByteArray()
        os.close()
        compressed
    } catch (ex: IOException) {
        null
    }
}