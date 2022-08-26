package com.craft.silicon.centemobile.data.model.control


enum class ControlFormatEnum(var type: String) {
    OTP("OTP"),
    SIMPLE("SIMPLE"),
    ENVELOPE("ENVELOPE"),
    JSON("JSON"),
    TAB_LAYOUT("TABLAYOUT"),
    TITLE("TITLE"),
    CENTER_VERTICAL("CENTERVERTICAL"),
    NUMERIC("NUMERIC"),
    GRID("GRID"),
    HORIZONTAL_GRID("HorizontalScroll"),
    TEXT("TEXT"),
    STRIPE("STRIPE"),
    SELECT_BANK_ACCOUNT("SELECTBANKACCOUNT"),
    IMAGE_PANEL("imagepanel"),
    PIN_NUMBER("PinNumber"),
    DATE("DATE"),
    HORIZONTAL_CONSTRAINT_LAYOUT("HORIZONTALCONSTRAINTLAYOUT"),
    AMOUNT("Amount"),
    JOKE_CATEGORY("JokeCategory"),
    RADIO_GROUPS("RADIOGROUPS"),
    TO_BANK("TOBANK"),
    DROP_DOWN("DROPDOWN"),
    PHONE("PHONE"),
    PIN("PIN"),
    VERTICAL("Vertical"),
    LIST_BUTTON("ListButton"),
    LIST_DATA("LISTDATA"),
    HORIZONTAL("HORIZONTAL"),
    HORIZONTAL_SCROLL("HORIZONTALSCROLL"),
    CATEGORY("CATEGORY"),
    NUMBER("NUMBER"),
    PRIMARY("PRIMARY"),
    HTML("HTML"),
    MY_NUMBER("MYNUMBER"),
    COUNTRY_CODE("COUNTRYCODE"),
    PROFILE_PICTURE("PROFILEPICTURE"),
    SUBJECT("SUBJECT"),
    MEDIUM("MEDIUM"),

    ACCOUNT_BANK("SELECTBANKACCOUNT"),
    BENEFICIARY("SELECTBENEFICIARY"),
    END("END"),
    NEXT("NEXT"),
    LIST_WITH_OPTIONS("ListWithOptions"),
    STANDING_ORDER("STANDINGORDER")
}