package com.crypto.eltwallet.model

data class SignupModel(

    val is_success: Boolean,
    val is_auth: Boolean,
    val message: String,
    val statuscode: Int,
    val timer_duration_sec: Int,
    val authinfo: String,

    val errMessage: String

)
