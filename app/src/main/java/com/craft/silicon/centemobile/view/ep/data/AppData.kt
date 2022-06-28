package com.craft.silicon.centemobile.view.ep.data

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.craft.silicon.centemobile.R
import com.craft.silicon.centemobile.data.model.control.FormControl
import com.craft.silicon.centemobile.data.model.module.Modules
import com.craft.silicon.centemobile.data.model.static_data.StaticDataDetails
import com.craft.silicon.centemobile.data.model.user.Accounts
import com.craft.silicon.centemobile.data.model.user.FrequentModules
import kotlinx.parcelize.Parcelize

open class AppData

data class HeaderData(
    val account: MutableList<Accounts>
) : AppData()


data class BodyData(
    val module: MutableList<Modules>,
    val frequentList: MutableList<FrequentModules>
) : AppData()


@Parcelize
open class DynamicData : Parcelable

@Parcelize
data class GroupModule(val parent: Modules, val module: MutableList<Modules>) : DynamicData()

@Parcelize
data class GroupForm(
    val module: Modules,
    val form: MutableList<FormControl>,
    val data: MutableList<StaticDataDetails>
) : DynamicData()

data class LandingPageItem(
    @StringRes val title: Int,
    @DrawableRes val avatar: Int,
    val enum: LandingPageEnum
)

enum class LandingPageEnum {
    BRANCH, LOGIN, ONLINE_BANKING, REGISTRATION, ON_THE_GO
}


data class GroupLanding(val list: MutableList<LandingPageItem>) : AppData()

object LandingData {
    val landingData = mutableListOf(
        LandingPageItem(
            title = R.string.branch_atm,
            avatar = R.drawable.atm,
            enum = LandingPageEnum.BRANCH
        ),
        LandingPageItem(
            title = R.string.cente_login,
            avatar = R.drawable.login_icon,
            enum = LandingPageEnum.LOGIN
        ),
        LandingPageItem(
            title = R.string.online_banking,
            avatar = R.drawable.piggy_bank,
            enum = LandingPageEnum.LOGIN
        ),
        LandingPageItem(
            title = R.string.cente_on_go,
            avatar = R.drawable.photos,
            enum = LandingPageEnum.LOGIN
        )
    )
}




