package com.craft.silicon.centemobile.data.model.user

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.craft.silicon.centemobile.data.model.converter.BeneficiaryTypeConverter
import com.craft.silicon.centemobile.data.model.converter.FrequentModulesTypeConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Entity(tableName = "login_user_data_tbl")
@Parcelize
data class LoginUserData(
    @field:SerializedName("Status")
    @field:ColumnInfo(name = "status")
    @field:Expose
    var status: String?,

    @field:SerializedName("LastLoginDateTime")
    @field:ColumnInfo(name = "LastLoginDateTime")
    @field:Expose
    var loginDate: String?,

    @field:SerializedName("Message")
    @field:ColumnInfo(name = "message")
    @field:Expose
    var message: String?,

    @field:SerializedName("FirstName")
    @field:ColumnInfo(name = "firstName")
    @field:Expose
    var firstName: String?,

    @field:SerializedName("IDNumber")
    @field:ColumnInfo(name = "iDNumber")
    @field:Expose
    var iDNumber: String?,

    @field:SerializedName("EMailID")
    @field:ColumnInfo(name = "emailID")
    @field:Expose
    var emailId: String?,

    @field:SerializedName("ImageURL")
    @field:ColumnInfo(name = "imageURL")
    @field:Expose
    var imageURL: String?,

    @field:SerializedName("LastName")
    @field:ColumnInfo(name = "lastName")
    @field:Expose
    var lastName: String?,

    @field:SerializedName("StaticDataVersion")
    @field:ColumnInfo(name = "version")
    @field:Expose
    var version: String?,

    @field:SerializedName("Accounts")
    @field:ColumnInfo(name = "accounts")
    @field:TypeConverters(Accounts::class)
    @field:Expose
    var accounts: @RawValue List<Accounts>?,
    @field:SerializedName("FrequentAccessedModules")
    @field:ColumnInfo(name = "frequentModules")
    @field:TypeConverters(FrequentModulesTypeConverter::class)
    @field:Expose
    var modules: @RawValue List<FrequentModules>?,

    @field:SerializedName("Beneficiary")
    @field:ColumnInfo(name = "beneficiary")
    @field:TypeConverters(BeneficiaryTypeConverter::class)
    @field:Expose
    var beneficiary: @RawValue List<Beneficiary>?,

    @field:SerializedName("ServiceAlerts")
    @field:ColumnInfo(name = "serviceAlerts")
    @field:Expose
    var serviceAlerts: MutableList<AlertServices>?,
    @field:SerializedName("ModulesToHide")
    @field:ColumnInfo(name = "modulesToHide")
    @field:Expose
    var hideModule: MutableList<ModuleHide>?,

    @field:SerializedName("modulesToDisable")
    @field:ColumnInfo(name = "modulesToDisable")
    @field:Expose
    var disableModule: MutableList<ModuleDisable>?,

    @field:SerializedName("PendingTrxDisplay")
    @field:ColumnInfo(name = "pendingTrxDisplay")
    @field:Expose
    var pendingTrxDisplay: MutableList<HashMap<String, String>>?,

    @field:SerializedName("PendingTrxPayload")
    @field:ColumnInfo(name = "PendingTrxPayload")
    @field:Expose
    var pendingTrxPayload: MutableList<HashMap<String, String>>?,

    @field:SerializedName("PendingTrxFormControls")
    @field:ColumnInfo(name = "PendingTrxFormControls")
    @field:Expose
    var pendingTrxFormControls: MutableList<PendingTrxFormControls>?,

    @field:SerializedName("PendingTrxActionControls")
    @field:ColumnInfo(name = "PendingTrxActionControls")
    @field:Expose
    var PendingTrxActionControls: MutableList<PendingTrxActionControls>?
) : Parcelable

@Parcelize
data class AlertServices(
    @field:SerializedName("ModuleID")
    @field:Expose
    var moduleID: String?,

    @field:SerializedName("AllowPayment")
    @field:Expose
    var payment: String?,

    @field:SerializedName("NoOfDays")
    @field:Expose
    var days: String,

    @field:SerializedName("ContainerID")
    @field:Expose
    var containerID: String,

    @field:SerializedName("ModuleName")
    @field:Expose
    var moduleName: String,

    @field:SerializedName("BankAccountID")
    @field:Expose
    var bankAccountID: String,

    @field:SerializedName("MerchantID")
    @field:Expose
    var merchantID: String,

    @field:SerializedName("ControlFormat")
    @field:Expose
    var controlFormat: String,

    @field:SerializedName("ModuleUrl")
    @field:Expose
    var moduleUrl: String?,

    @field:SerializedName("DueDate")
    @field:Expose
    var dueDate: String,
) : Parcelable

@Parcelize
@Entity(tableName = "account_tbl")
data class Accounts(
    @field:SerializedName("BankAccountID")
    @field:ColumnInfo(name = "bankAccountID")
    @field:PrimaryKey
    @field:NonNull
    @field:Expose
    var id: String,

    @field:SerializedName("AliasName")
    @field:ColumnInfo(name = "aliasName")
    @field:Expose
    var aliasName: String?,

    @field:SerializedName("CurrencyID")
    @field:ColumnInfo(name = "currencyId")
    @field:Expose
    var currencyId: String?,

    @field:SerializedName("AccountType")
    @field:ColumnInfo(name = "accountType")
    @field:Expose
    var accountType: String?
) : Parcelable

@Parcelize
@Entity(tableName = "frequent_tbl")
data class FrequentModules(
    @field:SerializedName("ParentModule")
    @field:ColumnInfo(name = "parentModule")
    @field:Expose
    var parentModule: String?,

    @field:SerializedName("ModuleID")
    @field:ColumnInfo(name = "moduleId")
    @field:PrimaryKey
    @field:NonNull
    @field:Expose
    var moduleId: String,

    @field:SerializedName("ModuleName")
    @field:ColumnInfo(name = "moduleName")
    @field:Expose
    var moduleName: String?,

    @field:SerializedName("ModuleCategory")
    @field:ColumnInfo(name = "moduleCategory")
    @field:Expose
    var moduleCategory: String?,

    @field:SerializedName("ModuleURL")
    @field:ColumnInfo(name = "moduleUrl")
    @field:Expose
    var moduleUrl: String?,

    @field:SerializedName("BadgeColor")
    @field:ColumnInfo(name = "badgeColor")
    @field:Expose
    var badgeColor: String?,

    @field:SerializedName("BadgeText")
    @field:ColumnInfo(name = "badgeText")
    @field:Expose
    var badgeMText: String?,

    @field:SerializedName("MerchantID")
    @field:ColumnInfo(name = "merchantID")
    @field:Expose
    var merchantID: String?,

    @field:SerializedName("DisplayOrder")
    @field:ColumnInfo(name = "displayOrder")
    @field:Expose
    var displayOrder: String?,

    @field:SerializedName("ContainerID")
    @field:ColumnInfo(name = "containerID")
    @field:Expose
    var containerID: String?
) : Parcelable

@Parcelize
@Entity(tableName = "beneficiary_tbl")
data class Beneficiary(
    @field:SerializedName("MerchantID")
    @field:ColumnInfo(name = "merchantID")
    @field:PrimaryKey
    @field:NonNull
    @field:Expose
    var merchantID: String,

    @field:SerializedName("AccountID")
    @field:ColumnInfo(name = "accountID")
    @field:Expose
    var accountID: String?,

    @field:SerializedName("MerchantName")
    @field:ColumnInfo(name = "MerchantName")
    @field:Expose
    var merchantName: String?,

    @field:SerializedName("RowID")
    @field:ColumnInfo(name = "RowID")
    @field:Expose
    var rowID: String?,

    @field:SerializedName("AccountAlias")
    @field:ColumnInfo(name = "accountAlias")
    @field:Expose
    var accountAlias: String?,

    @field:SerializedName("BankID")
    @field:ColumnInfo(name = "bankID")
    @field:Expose
    var bankID: String?,

    @field:SerializedName("BranchID")
    @field:ColumnInfo(name = "branchID")
    @field:Expose
    var branchID: String?,
) : Parcelable {
    override fun toString(): String {
        return accountAlias!!
    }
}


@Parcelize
data class ModuleHide(
    @field:SerializedName("ModuleID")
    @field:Expose
    var id: String?,
) : Parcelable

@Parcelize
data class ModuleDisable(
    @field:SerializedName("ModuleID")
    @field:Expose
    var id: String?,
    @field:SerializedName("Message")
    @field:Expose
    var message: String?,
) : Parcelable


data class ActivationData(
    @field:SerializedName("customerID")
    @field:Expose
    var id: String?,

    @field:SerializedName("mobile")
    @field:Expose
    var mobile: String?
) {
    @field:SerializedName("IDNumber")
    @field:Expose
    var iDNumber: String? = null

    @field:SerializedName("email")
    @field:Expose
    var email: String? = null

    @field:SerializedName("LastLoginDateTime")
    @field:Expose
    var loginDate: String? = null

    @field:SerializedName("lastName")
    @field:Expose
    var lastName: String? = null

    @field:SerializedName("firstName")
    @field:Expose
    var firstName: String? = null

    @field:SerializedName("imageURL")
    @field:Expose
    var imageURL: String? = null

    @field:SerializedName("message")
    @field:Expose
    var message: String? = null
}