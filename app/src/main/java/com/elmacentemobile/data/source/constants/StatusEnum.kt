package com.elmacentemobile.data.source.constants

import android.content.Context
import androidx.core.content.pm.PackageInfoCompat
import okhttp3.MultipartBody

enum class StatusEnum(val type: String) {
    FAILED("091"),
    SUCCESS("000"),
    OCR_SUCCESS("44"),
    ERROR("ok"),
    TOKEN("099"),
    OTP("093"),
    PHONE_REG("105"),
    DYNAMIC_FORM("106"),
    DYNAMIC_FORM_DIALOG("094"),
    PHONE_CHANGE("102"),
    PIN_CHANGE("101"),
}

val s = MultipartBody



fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName
        val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
       return  versionName
    } catch (e: Exception) {
        e.printStackTrace()
       ""
    }
}


