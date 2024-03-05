package com.elmacentemobile.view.activity.test


data class UserAccounts(
    val customerID: String,
    val mobile: String,
    val password: String
)

val users = listOf(
    UserAccounts(customerID = "1479373461", mobile = "254708835301", password = "1234"),
    UserAccounts(customerID = "4570670220", mobile = "256782993168", password = "1234"),
    UserAccounts(customerID = "1258826801", mobile = "256773932301", password = "1234"),
    UserAccounts(customerID = "1877579155", mobile = "254722770495", password = "2020"),
    UserAccounts(customerID = "4242346500", mobile = "256775541996", password = "1234"),
    UserAccounts(customerID = "1671877299", mobile = "256783657395", password = "1234"),
    UserAccounts(customerID = "6619056360", mobile = "256773932301", password = "2020"),//PROD
)
