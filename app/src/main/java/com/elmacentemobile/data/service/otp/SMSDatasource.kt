package com.elmacentemobile.data.service.otp

import android.app.Activity

interface SMSDatasource {
    fun startF()
    fun requestHint(activity: Activity)
}