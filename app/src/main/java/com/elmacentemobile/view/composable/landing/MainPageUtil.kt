package com.elmacentemobile.view.composable.landing

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.util.callbacks.AppCallbacks


data class PageData(
    val storage: StorageDataSource?,
    val callbacks: AppCallbacks?,
    val greetings: Greetings?,
    val viewModel: HashMap<String, ViewModel>? = null
)


data class Greetings(
    val message: String?,
    val color: Palette.Swatch?,
    @DrawableRes val image: Int?
)


