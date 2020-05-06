package com.crypto.eltwallet.model



data class UpdateEmailModel (
    val isSuccess: Boolean,
    val isAuth: Boolean,
    val message: String,
    val statuscode: Int,
    val usertoken: String
)