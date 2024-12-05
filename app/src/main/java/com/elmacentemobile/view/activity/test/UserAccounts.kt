package com.elmacentemobile.view.activity.test


data class UserAccounts(
    val customerID: String,
    val mobile: String,
    val password: String
)

val users = listOf(
    UserAccounts(customerID = "1479373461", mobile = "254708835301", password = "1234"),//0
    UserAccounts(customerID = "4570670220", mobile = "256782993168", password = "1234"),//1
    UserAccounts(customerID = "1258826801", mobile = "256773932301", password = "1234"),//2
    UserAccounts(customerID = "1877579155", mobile = "254722770495", password = "2020"),//3
    UserAccounts(customerID = "4242346500", mobile = "256775541996", password = "1234"),//4
    UserAccounts(customerID = "1671877299", mobile = "256783657395", password = "1234"),//5
    UserAccounts(customerID = "6619056360", mobile = "256773932301", password = "2020"),//PROD 6
    UserAccounts(customerID = "9163757000", mobile = "256783717976", password = "2020"),//PROD 7
)






