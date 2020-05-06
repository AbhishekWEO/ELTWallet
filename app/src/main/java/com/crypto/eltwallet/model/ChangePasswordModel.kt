package com.crypto.eltwallet.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ChangePasswordModel
{
    @SerializedName("is_success")
    @Expose
    private var isSuccess: String? = null
    @SerializedName("is_auth")
    @Expose
    private var isAuth: String? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("statuscode")
    @Expose
    private var statuscode: String? = null
    @SerializedName("usertoken")
    @Expose
    private var usertoken: String? = null

    fun getIsSuccess(): String? {
        return isSuccess
    }

    fun setIsSuccess(isSuccess: String?) {
        this.isSuccess = isSuccess
    }

    fun getIsAuth(): String? {
        return isAuth
    }

    fun setIsAuth(isAuth: String?) {
        this.isAuth = isAuth
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatuscode(): String? {
        return statuscode
    }

    fun setStatuscode(statuscode: String?) {
        this.statuscode = statuscode
    }

    fun getUsertoken(): String? {
        return usertoken
    }

    fun setUsertoken(usertoken: String?) {
        this.usertoken = usertoken
    }
}