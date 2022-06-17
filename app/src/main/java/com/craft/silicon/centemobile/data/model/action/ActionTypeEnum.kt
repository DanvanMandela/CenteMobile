package com.craft.silicon.centemobile.data.model.action

enum class ActionTypeEnum(val type: String) {
    ACTIVATION_REQ("ACTIVATIONREQ"),
    DB_CALL("DBCALL"),
    PAY_BILL("PAYBILL"),
    VALIDATE("VALIDATE"),
    LOGIN("LOGIN"),
    CHANGE_PIN("CHANGEPIN"),
    ACTIVATE("ACTIVATE")
}