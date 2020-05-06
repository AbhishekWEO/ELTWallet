package com.crypto.eltwallet.model

data class DashboardModel(
    val is_success:Boolean,
    val is_auth:Boolean,
    val statuscode:Int,
    val message:String,
    val firstname:String,
    val lastname:String,
    val user_image:String,
    val totalWallets:String,
    val totalBalance:Double,
    val sentTransactions:Int,
    val receivedTransactions:Int,
    val totalTransactions:Int,
    val userTransactionLimit:Double,
    val countAsTransactionLimit:String,
    val dollarPrice : Double,

    val errMessage:String
)