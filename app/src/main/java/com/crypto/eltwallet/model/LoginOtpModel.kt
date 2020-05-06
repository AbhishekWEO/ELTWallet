package com.crypto.eltwallet.model

data class LoginOtpModel (

    val is_success:Boolean,
    val is_auth:Boolean,
    val message:String,
    val statuscode:Int,
    val usertoken:String,

    val errMessage:String

)