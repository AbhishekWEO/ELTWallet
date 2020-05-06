package com.crypto.eltwallet.model

data class BaseModel(
    val is_success: Boolean,
    val is_auth: Boolean,
    val message: String,
    val statuscode: String
)