package com.craft.silicon.centemobile.data.model.action

import java.io.Serializable

data class ActionControls(
    var ModuleID: String?,
    var ControlID: String?,
    var ActionID: String?,
    var ActionType: String?,
    var ServiceParamIDs: String?,
    var NextModuleID: String?,
    var DisplayFormID: String?,
    var ActionCommand: String?,
    var ConfirmationModuleID: String?,
    var MerchantID: String?
) : Serializable