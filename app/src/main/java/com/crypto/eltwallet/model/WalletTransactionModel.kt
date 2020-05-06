package com.crypto.eltwallet.model

class WalletTransactionModel {

    private var is_success: Boolean = true
    private var is_auth: Boolean = true
    private var statuscode: Int = 0
    private var message: String? = null
    private var errMessage: String? = null

    private var transactionList: List<DataBean>? = null

    fun getStatusCode() : Int
    {
        return statuscode
    }

    fun setStatusCode(statuscode : Int)
    {
        this.statuscode = statuscode
    }

    fun getTransactionList(): List<DataBean>? {
        return transactionList
    }

    fun setTransactionList(transactionList: List<DataBean>) {
        this.transactionList = transactionList
    }

    class DataBean {

        var _id: String? = null
        var blockHash: String? = null
        var blockNumber: String? = null
        var from: String? = null
        var gas: String? = null
        var gasPrice: GasPriceBean? = null
        var hash: String? = null
        var input: String? = null
        var nonce: String? = null
        var to: String? = null
        var transactionIndex: String? = null
        var value: String? = null
        var v: String? = null
        var r: String? = null
        var s: String? = null
        var timestamp: String? = null
        var is_sender: String? = null


//        private var gasPrice: GasPriceBean? = null

        fun gasPriceBean() : GasPriceBean? {

            return gasPrice
        }

        class GasPriceBean {

            var s: Int = 0
            var e: Int = 0
            var isIsBigNumber: Boolean = false
            var c: List<Long>? = null
//            var c: String? = null

        }

    }

}