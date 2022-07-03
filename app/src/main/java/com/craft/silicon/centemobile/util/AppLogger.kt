package com.craft.silicon.centemobile.util

import android.util.Log
import com.craft.silicon.centemobile.data.source.constants.Constants

class AppLogger {

    companion object {
        private var INSTANCE: AppLogger? = null

        @get:Synchronized
        val instance: AppLogger
            get() {
                if (INSTANCE == null) {
                    INSTANCE = AppLogger()
                }
                return INSTANCE!!
            }

    }

    fun appLog(tag: String, message: String) {
        if (Constants.Data.TEST) {
            Log.e(tag, message)
        }
    }

}