package com.crypto.eltwallet.model

class CreateWalletModel {

    private var is_success: Boolean = true
    private var is_auth: Boolean = true
    private var statuscode: Int = 0
    private var message: String? = null

    private var response_data: ResponseDataBean? = null

    private var errMessage: String? = null

    fun getMessage(): String? {
        return message
    }

    fun getResponse_data(): ResponseDataBean? {
        return response_data
    }

    class ResponseDataBean {
        /**
         * address : 0x4c372ef73be2362a9712bce724279f495fc9be
         * privateKey : 0xfaf2d2bb8c10383bd970db422bbea9eb4c550807025326fbc52cbd33dbf06
         */

        var address: String? = null
        var privateKey: String? = null
    }

}