package com.crypto.eltwallet.model

data class EltCurrentValueModel(

    val is_success:Boolean,
    val is_auth:Boolean,
    val message:String,
    val statuscode:Int,
    val current_value:Double,

    val errMessage:String

)