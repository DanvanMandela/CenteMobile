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