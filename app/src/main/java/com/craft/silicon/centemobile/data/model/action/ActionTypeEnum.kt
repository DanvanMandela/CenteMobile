package com.craft.silicon.centemobile.data.model.action

enum class ActionTypeEnum(val type: String) {
    ACTIVATION_REQ("ACTIVATIONREQ"),
    DB_CALL("DBCALL"),
    PAY_BILL("PAYBILL"),
    VALIDATE("VALIDATE"),
    LOGIN("LOGIN"),
    CHANGE_PIN("CHANGEPIN"),
    ACTIVATE("ACTIVATE"),
    REQUEST_BASE("O-GetLatestVersion"),
    GET_MENU("GETALLMENU_v2"),
    GET_ACTION_CONTROL("GETACTIONCONTROLS_v2"),
    GET_FORM_CONTROL("GETFORMCONTROLS_v2")
}