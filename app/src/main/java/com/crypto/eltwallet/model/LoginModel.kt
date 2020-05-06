package com.crypto.eltwallet.model

data class LoginModel(
    val is_success:Boolean,
    val is_auth:Boolean,
    val message:String,
    val statuscode:Int,
    val otp_receiver:String,

    val errMessage:String
)