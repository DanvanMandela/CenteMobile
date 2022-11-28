package com.elmacentemobile.util.callbacks

import com.elmacentemobile.view.dialog.DialogCallback

interface AppCallbacks : BaseView,
    DialogCallback,
    ModuleCallback,
    FormCallback,
    NavigationCallback