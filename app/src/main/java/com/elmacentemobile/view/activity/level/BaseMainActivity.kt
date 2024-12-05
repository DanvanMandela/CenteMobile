package com.elmacentemobile.view.activity.level

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseMainActivity : AppCompatActivity() {

    var test = 0

    fun add() {
        test += 1
        Log.e("TP", "$test")
    }
}