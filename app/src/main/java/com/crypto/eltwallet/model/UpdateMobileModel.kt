package com.crypto.eltwallet.model

data class UpdateMobileModel(
    val isSuccess: Boolean,
    val isAuth: Boolean,
    val message: String,
    val statuscode: Int
)