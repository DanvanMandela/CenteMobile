package com.craft.silicon.centemobile.data.source.remote.helper

import com.craft.silicon.centemobile.data.source.constants.Constants

object DynamicURL {
    val status = if (Constants.Data.TEST) "uat" else "app"
    var other = "https://$status.craftsilicon.com/CentemobileWebOtherDynamic/api/elma/"

    var auth = "https://$status.craftsilicon.com/CentemobileWebAuthDynamic/api/elma/"

    var account = "https://$status.craftsilicon.com/CentemobileWebBankDynamic/api/elma/"

    var card = "https://$status.craftsilicon.com/CentemobileWebCardDynamic/api/elma/"

    var purchase = "https://$status.craftsilicon.com/CentemobileWebPurchaseDynamic/api/elma/"

    var validate = "https://$status.craftsilicon.com/CentemobileWebValidateDynamic/api/elma/"
}


object DynamicTestURL {

    var other = "https://uat.craftsilicon.com/ElmaWebOtherDynamic/api/elma/"

    var auth = "https://uat.craftsilicon.com/ElmaWebAuthDynamic/api/elma/"

    var account = "https://uat.craftsilicon.com/ElmaWebBankDynamic/api/elma/"

    var card = "https://uat.craftsilicon.com/ElmaWebCardDynamic/api/elma/"

    var purchase = "https://uat.craftsilicon.com/ElmaWebPurchaseDynamic/api/elma/"

    var validate = "https://uat.craftsilicon.com/ElmaWebValidateDynamic/api/elma/"
}