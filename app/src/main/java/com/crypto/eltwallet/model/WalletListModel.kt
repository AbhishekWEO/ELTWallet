package com.crypto.eltwallet.model

class WalletListModel {

    private var statuscode: Int = 0
    private var is_success: Boolean = true
    private var is_auth: Boolean = true
    private var message: String? = null

    private var walletList: List<DataBean>? = null

    fun getStatusCode() : Int
    {
        return statuscode
    }

    fun setStatusCode(statuscode : Int)
    {
        this.statuscode = statuscode
    }

    fun getWalletList(): List<DataBean>? {
        return walletList
    }

    fun setWalletList(walletList: List<DataBean>) {
        this.walletList = walletList
    }


    class DataBean {

        var id: String? = null
        var walletName: String? = null
        var walletAddress: String? = null
        var walletBal: String? = null

    }

}