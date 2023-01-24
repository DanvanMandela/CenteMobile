package com.elmacentemobile.view.activity.test


data class UserAccounts(
    val customerID: String,
    val mobile: String,
    val password: String
)

val users = listOf(
    UserAccounts(customerID = "1479373461", mobile = "254708835301", password = "8800"),
    UserAccounts(customerID = "4570670220", mobile = "256782993168", password = "1234")
)
