package com.elmacentemobile.data.source.constants

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
