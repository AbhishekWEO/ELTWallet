package com.crypto.eltwallet.model


data class GetKycDetailsModel
(
    val is_success: Boolean,
    val is_auth: Boolean,
    val message: String,
    val statuscode: Int,
    val current_limit: Int,
    val kyclimit_details: KyclimitDetailsModel,
    val count_as: String,
    val kyc_status: String,
    val email_id: String,
    val email_status: String,
    val country_code: String,
    val mobile_number: String,
    val mobile_status: String,
    val doc_status: String,
    val doc_reject_msg: String,
    val legal_name: String,
    val country_name: String,
    val is_higherlimit: Boolean
    //val higherLimitDetails: HigherLimitDetails
)