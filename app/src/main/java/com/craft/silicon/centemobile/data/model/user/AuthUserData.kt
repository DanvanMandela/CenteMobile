package com.craft.silicon.centemobile.data.model.user

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.craft.silicon.centemobile.data.model.converter.BeneficiaryTypeConverter
import com.craft.silicon.centemobile.data.model.converter.FrequentModulesTypeConverter
import com.craft.silicon.centemobile.view.ep.data.Body
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
    var beneficiary: @RawValue List<Beneficiary>?
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
data class Beneficiary(
    @field:SerializedName("MerchantID")
    @field:ColumnInfo(name = "merchantID")
    @field:Expose
    var merchantID: String?,

    @field:SerializedName("AccountID")
    @field:ColumnInfo(name = "accountID")
    @field:Expose
    var accountID: String?,

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
) : Parcelable


data class ActivationData(
    @field:SerializedName("CustomerID")
    @field:Expose
    var id: String?,

    @field:SerializedName("Mobile")
    @field:ColumnInfo(name = "Mobile")
    @field:Expose
    var mobile: String?,
)