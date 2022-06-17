package com.craft.silicon.centemobile.data.model.module

import java.io.Serializable

class Modules(
    var ParentModule: String?,
    var ModuleID: String?,
    var ModuleName: String?,
    var ModuleCategory: String?,
    var ModuleURL: String?,
    var MerchantID: String?,
    var isClubbed: Boolean?,
    var isChecked: Boolean?,
    var AllowConfirmationPopup: Boolean?,
    var isOtpRequired: Boolean?,
    var BadgeColor: String?,
    var BadgeText: String?,
    var ValidationAvailable: Boolean?,
    var DisplayOrder: String,
    var IsDisabled: Boolean,
) : Serializable