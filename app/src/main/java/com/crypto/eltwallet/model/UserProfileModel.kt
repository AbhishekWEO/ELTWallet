package com.crypto.eltwallet.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserProfileModel {

    @SerializedName("is_success")
    @Expose
    private var isSuccess: Boolean? = null
    @SerializedName("is_auth")
    @Expose
    private var isAuth: Boolean? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("statuscode")
    @Expose
    private var statuscode: String? = null
    @SerializedName("firstname")
    @Expose
    private var firstname: String? = null
    @SerializedName("lastname")
    @Expose
    private var lastname: String? = null
    @SerializedName("legalname")
    @Expose
    private var legalname: String? = null
    @SerializedName("email")
    @Expose
    private var email: String? = null
    @SerializedName("dob")
    @Expose
    private var dob: String? = null
    @SerializedName("address1")
    @Expose
    private var address1: String? = null
    @SerializedName("address2")
    @Expose
    private var address2: String? = null
    @SerializedName("city")
    @Expose
    private var city: String? = null
    @SerializedName("state")
    @Expose
    private var state: String? = null
    @SerializedName("postalcode")
    @Expose
    private var postalcode: String? = null
    @SerializedName("country")
    @Expose
    private var country: String? = null
    @SerializedName("country_code")
    @Expose
    private var countryCode: String? = null
    @SerializedName("phonenumber")
    @Expose
    private var phonenumber: String? = null
    @SerializedName("isverifyemail")
    @Expose
    private var isverifyemail: String? = null
    @SerializedName("isverifymobile")
    @Expose
    private var isverifymobile: String? = null
    @SerializedName("user_image")
    @Expose
    private var userImage: String? = null
    @SerializedName("zone_name")
    @Expose
    private var zoneName: String? = null
    @SerializedName("zone_description")
    @Expose
    private var zoneDescription: String? = null
    @SerializedName("zone_gmt")
    @Expose
    private var zoneGmt: String? = null
    @SerializedName("currency_name")
    @Expose
    private var currencyName: String? = null
    @SerializedName("currency_code")
    @Expose
    private var currencyCode: String? = null
    @SerializedName("currency_symbol")
    @Expose
    private var currencySymbol: String? = null
    @SerializedName("current_value")
    @Expose
    private var currentValue: String? = null
    @SerializedName("kyc_details")
    @Expose
    private var kycDetails: KycDetailsModel? = null

    fun getIsSuccess(): Boolean? {
        return isSuccess
    }

    fun setIsSuccess(isSuccess: Boolean?) {
        this.isSuccess = isSuccess
    }

    fun getIsAuth(): Boolean? {
        return isAuth
    }

    fun setIsAuth(isAuth: Boolean?) {
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

    fun getFirstname(): String? {
        return firstname
    }

    fun setFirstname(firstname: String?) {
        this.firstname = firstname
    }

    fun getLastname(): String? {
        return lastname
    }

    fun setLastname(lastname: String?) {
        this.lastname = lastname
    }

    fun getLegalname(): String? {
        return legalname
    }

    fun setLegalname(legalname: String?) {
        this.legalname = legalname
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getDob(): String? {
        return dob
    }

    fun setDob(dob: String?) {
        this.dob = dob
    }

    fun getAddress1(): String? {
        return address1
    }

    fun setAddress1(address1: String?) {
        this.address1 = address1
    }

    fun getAddress2(): String? {
        return address2
    }

    fun setAddress2(address2: String?) {
        this.address2 = address2
    }

    fun getCity(): String? {
        return city
    }

    fun setCity(city: String?) {
        this.city = city
    }

    fun getState(): String? {
        return state
    }

    fun setState(state: String?) {
        this.state = state
    }

    fun getPostalcode(): String? {
        return postalcode
    }

    fun setPostalcode(postalcode: String?) {
        this.postalcode = postalcode
    }

    fun getCountry(): String? {
        return country
    }

    fun setCountry(country: String?) {
        this.country = country
    }

    fun getCountryCode(): String? {
        return countryCode
    }

    fun setCountryCode(countryCode: String?) {
        this.countryCode = countryCode
    }

    fun getPhonenumber(): String? {
        return phonenumber
    }

    fun setPhonenumber(phonenumber: String?) {
        this.phonenumber = phonenumber
    }

    fun getIsverifyemail(): String? {
        return isverifyemail
    }

    fun setIsverifyemail(isverifyemail: String?) {
        this.isverifyemail = isverifyemail
    }

    fun getIsverifymobile(): String? {
        return isverifymobile
    }

    fun setIsverifymobile(isverifymobile: String?) {
        this.isverifymobile = isverifymobile
    }

    fun getUserImage(): String? {
        return userImage
    }

    fun setUserImage(userImage: String?) {
        this.userImage = userImage
    }

    fun getZoneName(): String? {
        return zoneName
    }

    fun setZoneName(zoneName: String?) {
        this.zoneName = zoneName
    }

    fun getZoneDescription(): String? {
        return zoneDescription
    }

    fun setZoneDescription(zoneDescription: String?) {
        this.zoneDescription = zoneDescription
    }

    fun getZoneGmt(): String? {
        return zoneGmt
    }

    fun setZoneGmt(zoneGmt: String?) {
        this.zoneGmt = zoneGmt
    }

    fun getCurrencyName(): String? {
        return currencyName
    }

    fun setCurrencyName(currencyName: String?) {
        this.currencyName = currencyName
    }

    fun getCurrencyCode(): String? {
        return currencyCode
    }

    fun setCurrencyCode(currencyCode: String?) {
        this.currencyCode = currencyCode
    }

    fun getCurrencySymbol(): String? {
        return currencySymbol
    }

    fun setCurrencySymbol(currencySymbol: String?) {
        this.currencySymbol = currencySymbol
    }

    fun getCurrentValue(): String? {
        return currentValue
    }

    fun setCurrentValue(currentValue: String?) {
        this.currentValue = currentValue
    }

    fun getKycDetails(): KycDetailsModel? {
        return kycDetails
    }

    fun setKycDetails(kycDetails: KycDetailsModel?) {
        this.kycDetails = kycDetails
    }
}