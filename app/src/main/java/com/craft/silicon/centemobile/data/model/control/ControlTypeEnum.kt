package com.craft.silicon.centemobile.data.model.control

enum class ControlTypeEnum(val type: String) {
    TEXT("TEXT"),
    FORM("FORM"),
    TITLE("TITLE"),
    TEXTVIEW("TEXTVIEW"),
    CONTAINER("CONTAINER"),
    LABEL("LABEL"),
    BLOCK("BLOCK"),
    TAB("TAB"),
    LIST("LIST"),
    IMAGE("IMAGE"),
    DROPDOWN("DROPDOWN"),
    HIDDEN("HIDDEN"),
    DATE("DATE"),
    BUTTON("BUTTON"),
    QR_SCANNER("QRSCANNER"),
    R_BUTTON("RBUTTON"),
    HYPERLINK("HYPERLINK"),
    SLIDER_LAYOUT("SLIDERLAYOUT"),
    SELECTED_TEXT("SELECTEDTEXT"),
    PHONE_CONTACTS("PHONECONTACTS"),
    CHECKBOX("CHECKBOX"),
    CONFORM("CONFIRMATIONFORM"),

}

enum class ControlIDEnum(val type: String) {
    RECENT_LIST("RECENTLIST")
}