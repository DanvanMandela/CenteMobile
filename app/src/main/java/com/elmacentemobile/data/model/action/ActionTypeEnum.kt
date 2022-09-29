package com.elmacentemobile.data.model.action

enum class ActionTypeEnum(val type: String) {
    ACTIVATION_REQ("ACTIVATIONREQ"),
    DB_CALL("DBCALL"),
    PAY_BILL("PAYBILL"),
    VALIDATE("VALIDATE"),
    LOGIN("LOGIN"),
    DEVICE_REGISTER("REGISTERDEVICE"),
    CHANGE_PIN("CHANGEPIN"),
    ACTIVATE("ACTIVATE"),
    REQUEST_BASE("O-GetLatestVersion"),
    GET_MENU("MENU"),
    GET_ACTION_CONTROL("ACTIONS"),
    GET_FORM_CONTROL("FORMS"),
    STATIC_DATA("STATICDATA"),
    CARD("CARD"),


}