package com.crypto.eltwallet.model

class GasValueModel {

    private var is_success: Boolean = true
    private var is_auth: Boolean = true
    private var message: String? = null

    private var gass_data: List<DataBean>? = null

    fun getGasValue(): List<DataBean>? {
        return gass_data
    }

    fun setGasValue(gass_data: List<DataBean>) {
        this.gass_data = gass_data
    }

    class DataBean {

        var gid: String? = null
        var name: String? = null
        var gass_value: String? = null
        var timestamp: String? = null

    }

}