package com.crypto.eltwallet.model

data class SendEltOtpModel(
    val is_success:Boolean,
    val is_auth:Boolean,
    val message:String,
    val statuscode:Int,

    val errMessage:String
)