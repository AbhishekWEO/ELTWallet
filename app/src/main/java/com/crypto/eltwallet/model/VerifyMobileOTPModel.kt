package com.crypto.eltwallet.model



data class VerifyMobileOTPModel(
    val isSuccess: Boolean,
    val isAuth: Boolean,
    val message: String,
    val statuscode: Int,
    val otpReceiver: String,
    val timer_duration_sec: Int
)