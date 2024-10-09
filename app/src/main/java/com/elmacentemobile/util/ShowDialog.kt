package com.elmacentemobile.util

import android.content.Context
import com.seedlotfi.towerinfodialog.TowerDialog

class ShowDialog(context: Context, title: String, message: String) {
    init {
        val dialog = TowerDialog.Companion.Builder()
            .setContext(context)
            .setTitle(title)
            .setIsSuccess(false)
            .setMessage(message)
            .setTileIsBold(true)
            .setButtonText("Done")
            .build()

        dialog.show {

        }
    }
}