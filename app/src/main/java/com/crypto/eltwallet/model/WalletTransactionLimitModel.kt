package com.crypto.eltwallet.model

data class WalletTransactionLimitModel (

    val is_success:Boolean,
    val is_auth:Boolean,
    val statuscode:Int,
    val message:String,
    val usedTransactionLimit:Double,
    val remainingTransactionLimit:Double,
    val transactionLimit:Double,
    val countAsTransactionLimit:String,
    val wallet_name:String,
    var wallet_balance:String,

    val errMessage:String

)