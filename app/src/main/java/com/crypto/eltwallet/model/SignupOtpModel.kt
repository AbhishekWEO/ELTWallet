package com.crypto.eltwallet.model

data class SignupOtpModel(
    val is_success:Boolean,
    val is_auth:Boolean,
    val message:String,
    val statuscode:Int,
    val authinfo:String,
    val timer_duration_sec:String,

    val errMessage:String
)