package com.crypto.eltwallet.utilities

class Constants {
    companion object {

        const val BASE_URL = "https://apiwalletdev.webexpertsonline.org/api/"
        const val VER = "v2.0/"
        const val URL = BASE_URL + VER;

        const val USERS = "user/"
        const val WALLET = "wallet/"
        const val NOTIFICATION = "notification/"
        const val KYC = "kyc/"

        const val SIGNUP = URL + USERS + "signup"
        const val SIGNUP_OTP_VERIFY = URL + USERS + "signup-otp-verify"
        const val LOGIN = URL + USERS + "login"
        const val LOGIN_RESEND = URL + USERS + "resend-login-otp"
        const val LOGIN_OTP_VERIFY = URL + USERS + "login-otp-verify"
        const val FORGET_PASSWORD = URL + USERS + "forget-password"

        const val DASHBOARD = URL + USERS + "dashboard"
        const val JSON_LIST = BASE_URL + "/json-list"

        const val ELT_CURRENT_VALUE = URL + WALLET + "elt-current-value"
        const val WALLET_LIST = URL + WALLET + "list"
        const val CREATE_WALLET = URL + WALLET + "create"
        const val IMPORT_WALLET = URL + WALLET + "import"
        const val TRANSACTION_LIMIT = URL + WALLET + "transaction-limit"
        const val RENAME_WALLET = URL + WALLET + "rename"
        const val DELETE_WALLET = URL + WALLET + "delete"
        const val TRANSACTION_LIST = URL + WALLET + "transactions"

        const val GASS_VALUE = URL + WALLET + "gass-value"
        const val SEND_ELT_OTP = URL + WALLET + "send-elt-otp"
        const val SEND_ELT = URL + WALLET + "send-elt"

        const val NOTIFICATION_LIST = URL + NOTIFICATION + "list"
        const val NOTIFICATION_CLEAR = URL + NOTIFICATION + "clear"

        const val PROFILE = URL + USERS + "profile"
        const val UPDATE_PROFILE_IMAGE = URL + USERS + "update-profile-image"
        const val UPDATE_PROFILE = URL + USERS + "update-profile"
        const val CHANGE_PASSWORD = URL + USERS + "change-password"
        const val LOGOUT = URL + USERS + "logout"
        const val UPDATE_EMAIL = URL + USERS + "update-email"
        const val VERIFY_EMAIL = URL + USERS + "send-email-verification-link"
        const val UPDATE_MOBILE = URL + USERS + "update-mobile"
        const val SEND_MOBILE_VERIFICATION_OTP = URL + USERS + "send-mobile-verification-otp"
        const val VERIFY_OTP = URL + USERS + "verify-otp"

        const val GET_KYC_DETAILS = URL + KYC + "details"
        const val DOC_TYPES = URL + KYC + "doc-types"
        const val UPLOAD_DOCS = URL + KYC +"doc-upload"
        const val URL_NEWS = "https://min-api.cryptocompare.com/data/v2/news/?lang=EN"
        const val LANG = URL + USERS + "language-token"

        const val EMAIL_PATREN = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z.]+"
        const val PASSWORD_PATREN = "((?=.*\\d)(?=.*[@#$%]).{8,20})"

    }
}