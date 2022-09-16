package com.craft.silicon.centemobile.data.source.remote.helper

import com.craft.silicon.centemobile.data.source.constants.Constants

object DynamicURL {

    const val live = "https://app.craftsilicon.com/CentemobileAuthDynamic/"

    const val other = "https://app.craftsilicon.com/CentemobileWebOtherDynamic/api/elma/"

    const val auth = "https://app.craftsilicon.com/CentemobileWebAuthDynamic/api/elma/"

    const val account = "https://app.craftsilicon.com/CentemobileWebBankDynamic/api/elma/"

    const val card = "https://app.craftsilicon.com/CentemobileWebCardDynamic/api/elma/"

    const val purchase = "https://app.craftsilicon.com/CentemobileWebPurchaseDynamic/api/elma/"

    const val validate = "https://app.com/CentemobileWebValidateDynamic/api/elma/"

    const val static = "https://app.craftsilicon.com/CentemobileWebDataDynamic/api/elma/"


}

fun liveTest(): String {
    return if (Constants.Data.TEST) "https://uat.craftsilicon.com/" else "https://app.craftsilicon.com/"
}


object DynamicTestURL {

    const val static = "https://uat.craftsilicon.com/ElmaWebDataDynamic/api/elma/"

    const val uat = "https://uat.craftsilicon.com/ElmaAuthDynamic/"


    const val other = "https://uat.craftsilicon.com/ElmaWebOtherDynamic/api/elma/"

    const val auth = "https://uat.craftsilicon.com/ElmaWebAuthDynamic/api/elma/"

    const val account = "https://uat.craftsilicon.com/ElmaWebBankDynamic/api/elma/"

    const val card = "https://uat.craftsilicon.com/ElmaWebCardDynamic/api/elma/"

    const val purchase = "https://uat.craftsilicon.com/ElmaWebPurchaseDynamic/api/elma/"

    const val validate = "https://uat.craftsilicon.com/ElmaWebValidateDynamic/api/elma/"
}


var OTHER_BASE_URL = if (!Constants.Data.TEST) DynamicURL.other else DynamicTestURL.other

var ROUTE_BASE_URL = if (!Constants.Data.TEST) DynamicURL.live else DynamicTestURL.uat