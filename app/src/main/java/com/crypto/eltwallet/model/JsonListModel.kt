package com.crypto.eltwallet.model

class JsonListModel{

    private var message: String? = null
    private var statuscode: Int = 0
    private var responseMessage: String? = null
    private var data: List<DataBean>? = null
    private var current_value: Float? = null
    private var changeInEltSinceLastMonth: String? = null
    private var changedValue: Float? = null

    fun getCurrent_value(): Float? {
        return current_value
    }

    fun setCurrent_value(current_value: Float) {
        this.current_value = current_value
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getStatuscode(): Int {
        return statuscode
    }

    fun setStatuscode(statuscode: Int) {
        this.statuscode = statuscode
    }

    fun getResponseMessage(): String? {
        return responseMessage
    }

    fun setResponseMessage(responseMessage: String) {
        this.responseMessage = responseMessage
    }

    fun getData(): List<DataBean>? {
        return data
    }

    fun setData(data: List<DataBean>) {
        this.data = data
    }

    fun getChangeInEltSinceLastMonth(): String? {
        return changeInEltSinceLastMonth
    }

    fun getChangedValue(): Float? {
        return changedValue
    }


    class DataBean {
        /**
         * date : 2018-06-30 09:00
         * close : 0.1
         */

        var date: String? = null
        var close: Float = 0.0F
    }

}