package com.crypto.eltwallet.model

class GetKycDocTypesModel {

    private var statuscode: Int = 0
    private var is_success: Boolean = true
    private var is_auth: Boolean = true
    private var message: String? = null

    private var kyc_doc: List<DataBean>? = null


    fun getKycDoc(): List<DataBean>? {
        return kyc_doc
    }

    fun setKycDoc(kyc_doc: List<DataBean>) {
        this.kyc_doc = kyc_doc
    }

    class DataBean {

        var id: String? = null
        var name: String? = null
        var doc_msg: String? = null
        var no_of_attachment: String? = null
        var doc_icon: String? = null

    }
}