package com.elmacentemobile.util

import android.content.Context
import com.elmacentemobile.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ShowAlertDialog {
    fun showDialog(
        context: Context, title: String, message: String,
        callback: OnAlertDialog
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(context.resources.getString(R.string.cancel)) { _, _ ->
                callback.onNegative()
            }
            .setPositiveButton(context.resources.getString(R.string.accept_)) { d, _ ->
                d.dismiss()
                callback.onPositive()
            }
            .show()
        VibrationHelper().vibrate(context)
    }


}

interface OnAlertDialog {
    fun onPositive() {
        throw Exception("Not implemented")
    }

    fun onNegative() {
        throw Exception("Not implemented")
    }
}
