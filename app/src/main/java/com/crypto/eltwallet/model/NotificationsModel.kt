package com.crypto.eltwallet.model

class NotificationsModel {

    private var statuscode: Int = 0
    private var is_success: Boolean = true
    private var is_auth: Boolean = true
    private var message: String? = null

    private var notification_list: List<DataBean>? = null

    fun getStatusCode() : Int
    {
        return statuscode
    }

    fun setStatusCode(statuscode : Int)
    {
        this.statuscode = statuscode
    }

    fun getNotificationtList(): List<DataBean>? {
        return notification_list
    }

    fun setNotificationtList(notification_list: List<DataBean>) {
        this.notification_list = notification_list
    }

    class DataBean {

        var id: String? = null
        var userid_s: String? = null
        var userid_r: String? = null
        var address_from: String? = null
        var address_to: String? = null
        var amt: String? = null
        var status: String? = null
        var transaction_response: String? = null
        var created: String? = null

    }

}