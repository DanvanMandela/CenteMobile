package com.craft.silicon.centemobile.data.source.remote.helper

import com.craft.silicon.centemobile.data.source.constants.Constants

object DynamicURL {
    val status = if (Constants.Data.TEST) "uat" else "app"
    var other = "https://$status.craftsilicon.com/ElmaWebOtherDynamic/api/elma/"
}