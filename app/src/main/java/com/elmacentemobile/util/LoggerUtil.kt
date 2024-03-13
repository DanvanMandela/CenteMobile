package com.elmacentemobile.util

import android.content.Context
import java.io.IOException

import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter


class LoggerUtil(context: Context) {

    companion object {
        private const val LOG_FILE_NAME = "cente_logs.txt"
        private const val LOG_FILE_SIZE_LIMIT = 500000000
        private const val LOG_FILE_COUNT = 1
    }

    init {
        try {
            val logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
            logger.level = Level.ALL
            val internalStoragePath = context.filesDir.path
            val logFilePath = "$internalStoragePath/$LOG_FILE_NAME"
            val fileHandler = FileHandler(logFilePath, LOG_FILE_SIZE_LIMIT, LOG_FILE_COUNT, true)
            fileHandler.formatter = SimpleFormatter()
            logger.addHandler(fileHandler)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun logInfo(message: String?) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).info(message)
    }

    fun logWarning(message: String?) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).warning(message)
    }

    fun logError(message: String?) {
        Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).severe(message)
    }

}