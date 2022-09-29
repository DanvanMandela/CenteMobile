package com.elmacentemobile.data.source.remote.helper

import com.elmacentemobile.data.source.constants.Constants

object DynamicURL {
    const val live = "https://app.craftsilicon.com/CentemobileAuthDynamic/"
}


object DynamicTestURL {
    const val uat = "https://uat.craftsilicon.com/ElmaAuthDynamic/"
}


fun liveTest(): String {
    return if (Constants.Data.TEST) "https://uat.craftsilicon.com/"
    else "https://app.craftsilicon.com/"
}


var ROUTE_BASE_URL = if (!Constants.Data.TEST) DynamicURL.live else DynamicTestURL.uat