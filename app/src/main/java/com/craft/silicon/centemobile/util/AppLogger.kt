package com.craft.silicon.centemobile.util

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.source.constants.Constants
import java.io.File
import java.io.FileOutputStream


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
        if (Constants.Data.TEST) { //TODO CHECK TEST OFF
            Log.e(tag, message)
        }
    }

    fun logTXT(s: String, context: Activity) {
        if (Constants.Data.TEST) {
            val permission =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            if (permission == PackageManager.PERMISSION_GRANTED) {
                val state = Environment.getExternalStorageState()
                if (Environment.MEDIA_MOUNTED == state) {
                    val storage = Environment.getExternalStorageDirectory();
                    val dir =
                        File("${storage.absolutePath}/${context.getString(R.string.app_name)}")
                    dir.mkdir()
                    val file = File(dir, "logs${BaseClass.generateAlphaNumericString(5)}.json")
                    if (file.exists()) file.delete()
                    val os: FileOutputStream?
                    try {
                        os = FileOutputStream(file)
                        os.write(s.toByteArray())
                        os.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            } else {
                context.runOnUiThread {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        101
                    )
                }
            }
        }

    }


    fun writePDF(doc: PdfDocument, name: String, context: Activity) {

        val permission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            val state = Environment.getExternalStorageState()
            if (Environment.MEDIA_MOUNTED == state) {

                val storage = Environment.getExternalStorageDirectory();
                val dir =
                    File("${storage.absolutePath}/${context.getString(R.string.app_name)}")
                dir.mkdir()
                val file = File(dir, "$name.pdf")
                val os: FileOutputStream?
                try {
                    os = FileOutputStream(file)
                    doc.writeTo(os)
                    os.close()
                    doc.close()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (file.exists()) {
                            val uri = Uri.parse(file.path)

                            val fileUri = FileProvider.getUriForFile(
                                context,
                                context.packageName.toString() + ".provider",
                                file
                            )

                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "application/pdf"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                putExtra(Intent.EXTRA_STREAM, fileUri)
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "Transaction..."
                                )
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Sharing transaction details..."
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Via"))
                        }
                    }, 200)
                } catch (e: Exception) {
                    e.printStackTrace()
                    doc.close()
                }
            }
        } else {
            context.runOnUiThread {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        ),
                        101
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        101
                    )
                }
            }
        }
    }

}