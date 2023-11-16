package com.elmacentemobile.view.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.elmacentemobile.data.model.module.Modules
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ModuleViewModel @Inject constructor() : ViewModel() {

    val module = MutableLiveData<Modules?>(null)

}