package com.elmacentemobile.view.ep.data

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.room.*
import com.elmacentemobile.R
import com.elmacentemobile.data.model.action.ActionControls
import com.elmacentemobile.data.model.control.FormControl
import com.elmacentemobile.data.model.converter.DynamicDataResponseTypeConverter
import com.elmacentemobile.data.model.converter.GroupFormTypeConverter
import com.elmacentemobile.data.model.dynamic.DynamicAPIResponse
import com.elmacentemobile.data.model.dynamic.DynamicDataResponse
import com.elmacentemobile.data.model.dynamic.Notifications
import com.elmacentemobile.data.model.dynamic.ResultsData
import com.elmacentemobile.data.model.input.InputData
import com.elmacentemobile.data.model.module.Modules
import com.elmacentemobile.data.model.user.Accounts
import com.elmacentemobile.data.model.user.FrequentModules
import com.elmacentemobile.data.source.pref.StorageDataSource
import com.elmacentemobile.data.source.remote.callback.ReceiptDetails
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import io.reactivex.annotations.NonNull
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type
import java.util.*

open class AppData

data class AccountData(
    val account: MutableList<Accounts>
) : AppData()

open class Nothing : AppData()


data class BodyData(
    val module: MutableList<Modules>,
    val frequentList: MutableList<FrequentModules>
) : AppData()


@Parcelize
open class DynamicData : Parcelable

@Parcelize
data class GroupModule(
    @field:SerializedName("parent")
    @field:Expose
    val parent: Modules,
    @field:SerializedName("module")
    @field:Expose
    val module: MutableList<Modules>
) : DynamicData()

@Parcelize
data class GroupForm(
    @field:SerializedName("modules")
    @field:Expose
    val module: Modules,
    @field:SerializedName("action")
    @field:Expose
    val action: ActionControls? = null,
    @field:SerializedName("formList")
    @field:Expose
    val form: MutableList<FormControl>?,
    @field:SerializedName("allow")
    @field:Expose
    val allow: Boolean = false
) : DynamicData()


data class FormData(
    @field:SerializedName("modules")
    @field:Expose
    var forms: GroupForm,
    @field:SerializedName("storage")
    @field:Expose
    var storage: StorageDataSource?
) : DynamicData()


@Entity(tableName = "layout_tbl")
data class LayoutData(
    @field:SerializedName("layout")
    @field:ColumnInfo(name = "layout")
    @field:TypeConverters(GroupFormTypeConverter::class)
    @field:Expose
    var layout: GroupForm,

    @field:SerializedName("data")
    @field:ColumnInfo(name = "data")
    @field:TypeConverters(DynamicDataResponseTypeConverter::class)
    @field:Expose
    var data: DynamicDataResponse?,
) {
    @field:SerializedName("id")
    @field:ColumnInfo(name = "id")
    @field:PrimaryKey
    @field:NonNull
    @field:Expose
    var id: Int = 1
}

data class LandingPageItem(
    @field:SerializedName("title")
    @StringRes val title: Int,
    @field:SerializedName("avatar")
    @DrawableRes val avatar: Int,
    @field:SerializedName("enum")
    val enum: LandingPageEnum,
) {
    @field:SerializedName("active")
    @field:Expose
    var visible: Boolean? = null
}

enum class LandingPageEnum {
    BRANCH, LOGIN, ONLINE_BANKING, REGISTRATION, ON_THE_GO
}


data class GroupLanding(val list: MutableList<LandingPageItem>) : AppData()

object LandingData {
    val landingData = mutableListOf(
        LandingPageItem(
            title = R.string.branch_atm,
            avatar = R.drawable.atm_machine,
            enum = LandingPageEnum.BRANCH
        ),

        LandingPageItem(
            title = R.string.online_banking,
            avatar = R.drawable.checking,
            enum = LandingPageEnum.ONLINE_BANKING
        ),
        LandingPageItem(
            title = R.string.cente_on_go,
            avatar = R.drawable.go,
            enum = LandingPageEnum.ON_THE_GO
        )
    )
}


data class ResultDataList(
    val total: Int,
    val list: MutableList<ResultsData>
) : AppData()

data class ControlList(
    val formControl: FormControl,
    val list: MutableList<ControlList>,
    val container: Boolean,
    val linked: Boolean
)


data class DisplayContent(
    val key: String?,
    val value: String?
)

@Parcelize
data class NavigationData(
    var module: Modules?,
    var forms: MutableList<FormControl>?
) : Parcelable


data class DisplayList(val list: MutableList<DisplayContent>) : AppData()


data class ConfirmData(
    val list: MutableList<FormControl>,
    val hashMap: HashMapWrapper
) : AppData()

@Parcelize
data class HashMapWrapper(val hashMap: HashMap<String, String>?) :
    Parcelable

@Parcelize
data class ReceiptList(
    val receipt: MutableList<ReceiptDetails>,
    val notification: MutableList<Notifications>?
) : DynamicData()


@Parcelize
data class InputList(
    @field:SerializedName("data")
    @field:Expose
    val data: MutableList<InputData>?
) : DynamicData()

data class GlobalInput(
    val hint: String,
    val data: String?
)


@Parcelize
data class MiniStatement(
    @field:SerializedName("Narration")
    @field:Expose
    val narration: String?,

    @field:SerializedName("Amount")
    @field:Expose
    val amount: String?,

    @field:SerializedName("Date")
    @field:Expose
    val date: String?
) : Parcelable

@Parcelize
data class MiniList(
    @field:SerializedName("mini")
    @field:Expose
    val miniList: MutableList<MiniStatement>?
) : Parcelable


class MiniTypeConverter {
    @TypeConverter
    fun from(data: String?): List<MiniStatement?>? {
        if (data == null) {
            return Collections.emptyList()
        }
        val listType: Type = object :
            TypeToken<List<MiniStatement?>?>() {}.type
        return gsonBuilder.fromJson<List<MiniStatement?>>(data, listType)
    }

    @TypeConverter
    fun to(someObjects: List<MiniStatement?>?): String? {
        return Gson().toJson(someObjects)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}


@Parcelize
data class NameBaseData(
    @field:SerializedName("text")
    @field:Expose
    val text: String?,

    @field:SerializedName("id")
    @field:Expose
    val id: String?
) : Parcelable {
    override fun toString(): String {
        return text!!
    }
}


@Parcelize
data class ActivateData(
    @field:SerializedName("mobile")
    @field:Expose
    val mobile: String?,
    @field:SerializedName("pin")
    @field:Expose
    val pin: String?
) : Parcelable

@Parcelize
data class BusData(
    @field:SerializedName("data")
    @Expose
    var data: DynamicData?,



    @field:SerializedName("response")
    @Expose
    var res: DynamicAPIResponse? = null,
    @field:SerializedName("inputs")
    @Expose
    var inputs: MutableList<InputData>? = null
) : Parcelable

class BusDataTypeConverter {


    fun from(data: BusData?): String? {
        return if (data == null) {
            null
        } else gsonBuilder.toJson(data, BusData::class.java)
    }

    fun to(data: String?): BusData? {
        return if (data == null) {
            null
        } else gsonBuilder.fromJson(data, BusData::class.java)
    }

    companion object {
        private val gsonBuilder: Gson =
            GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}


@Parcelize
data class ContactData(
    @field:SerializedName("name")
    @field:Expose
    val name: String?,
    @field:SerializedName("uri")
    @field:Expose
    val avatar: String?,
    @field:SerializedName("numbers")
    @field:Expose
    val numbers: MutableList<String>?
) : Parcelable







