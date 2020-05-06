package com.crypto.eltwallet.network

import com.crypto.eltwallet.model.*
import com.crypto.eltwallet.utilities.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface RetrofitInterface {

    @FormUrlEncoded
    @POST(Constants.SIGNUP)
    abstract fun attemptToSignup(@FieldMap weakHashMap: WeakHashMap<String, String>): Call<SignupModel>

    @FormUrlEncoded
    @POST(Constants.SIGNUP_OTP_VERIFY)
    abstract fun attemptToNewSignUpOtp(@Header("authinfo") input_auth_info: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<SignupOtpModel>


    @FormUrlEncoded
    @POST(Constants.LOGIN)
    abstract fun attemptToLogin(@FieldMap weakHashMap: WeakHashMap<String, String>): Call<LoginModel>

    @FormUrlEncoded
    @POST(Constants.LOGIN_RESEND)
    abstract fun attemptToResendOtp(@FieldMap weakHashMap: WeakHashMap<String, String>): Call<ResendLoginOtpModel>

    @FormUrlEncoded
    @POST(Constants.LOGIN_OTP_VERIFY)
    abstract fun attemptToLoginOtp(@FieldMap weakHashMap: WeakHashMap<String, String>): Call<LoginOtpModel>

    @FormUrlEncoded
    @POST(Constants.FORGET_PASSWORD)
    abstract fun attemptForgotPassword(@FieldMap weakHashMap: WeakHashMap<String, String>): Call<ForgetPasswordModel>



    @GET(Constants.DASHBOARD)
    abstract fun attemptToDashboardData(@Header("authorization") input_auth_info: String): Call<DashboardModel>

    @GET(Constants.JSON_LIST + "/{noOfMonths}")
    abstract fun attemptToJsonList(@Path("noOfMonths") str : Int): Call<JsonListModel>

    @GET(Constants.ELT_CURRENT_VALUE)
    abstract fun attemptToEltCurrentValue(@Header("authorization") input_auth_info: String): Call<EltCurrentValueModel>



    @GET(Constants.WALLET_LIST)
    abstract fun attemptToWalletList(@Header("authorization") input_auth_info: String): Call<WalletListModel>

    @FormUrlEncoded
    @POST(Constants.CREATE_WALLET)
    abstract fun attemptToCreateWallet(@Header("authorization") input_auth_info: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<CreateWalletModel>

    @FormUrlEncoded
    @POST(Constants.IMPORT_WALLET)
    abstract fun attemptToImportWallet(@Header("authorization") input_auth_info: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<ImportWalletModel>

    @GET(Constants.TRANSACTION_LIMIT + "/{walletaddress}")
    abstract fun attemptToGetWalletTransactionLimit(@Header("authorization") str: String, @Path("walletaddress") str2: String): Call<WalletTransactionLimitModel>

    @FormUrlEncoded
    @POST(Constants.RENAME_WALLET)
    abstract fun attemptToRenameWallet(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<RenameWalletModel>

    @FormUrlEncoded
    @POST(Constants.DELETE_WALLET)
    abstract fun attemptToDeleteWallet(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<DeleteWalletModel>



    @GET(Constants.TRANSACTION_LIST + "/{walletaddress}/{numberOfRow}/{pageNumber}/{order}/{todate}")
    abstract fun attemptTransactionList(@Header("authorization") str: String, @Path("walletaddress") str2: String, @Path("numberOfRow") str3: Int, @Path("pageNumber") str4: Int,
                                          @Path("order") str5: String, @Path("todate") str6: String ): Call<WalletTransactionModel>



    @GET(Constants.GASS_VALUE)
    abstract fun attemptToGasValue(@Header("authorization") str: String): Call<GasValueModel>

    @FormUrlEncoded
    @POST(Constants.SEND_ELT_OTP)
    abstract fun attemptToSendEltOtp(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<SendEltOtpModel>

    @FormUrlEncoded
    @POST(Constants.SEND_ELT)
    abstract fun attemptToSendElt(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<SendEltModel>

    @GET(Constants.NOTIFICATION_LIST + "/{numberOfRow}" + "/{pageNumber}")
    abstract fun attemptToNotificationsList(@Header("authorization") str: String,
                                            @Path("numberOfRow") numberOfRow: Int,
                                            @Path("pageNumber") pageNumber: Int): Call<NotificationsModel>

    @FormUrlEncoded
    @POST(Constants.NOTIFICATION_CLEAR)
    abstract fun attemptToClearNotification(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<NotificationClearModel>



    @Multipart
    @POST(Constants.UPDATE_PROFILE_IMAGE)
    abstract fun attemptToUpateProfileImage(@Header("authorization") str: String, @Part file: MultipartBody.Part): Call<UploadProfileImageModel>

    @FormUrlEncoded
    @POST(Constants.UPDATE_PROFILE)
    abstract fun attemptToUpdateProfile(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<UpdateProfileModel>

    @GET(Constants.PROFILE)
    abstract fun attemptToUserProfile(@Header("authorization") str: String): Call<UserProfileModel>

    @FormUrlEncoded
    @POST(Constants.CHANGE_PASSWORD)
    abstract fun attemptToChangePassword(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>):Call<ChangePasswordModel>

    @GET(Constants.LOGOUT)
    abstract fun attemptToLogOut(@Header("authorization") str: String): Call<LogOutModel>

    @FormUrlEncoded
    @POST(Constants.UPDATE_EMAIL)
    abstract fun updateEmail(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<UpdateEmailModel>

    @FormUrlEncoded
    @POST(Constants.UPDATE_MOBILE)
    abstract fun updateMobile(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<UpdateMobileModel>

    @FormUrlEncoded
    @POST(Constants.VERIFY_EMAIL)
    abstract fun verifyEmail(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, String>): Call<UpdateMobileModel>

    @GET(Constants.SEND_MOBILE_VERIFICATION_OTP)
    abstract fun verifyMobileOTP(@Header("authorization") str: String): Call<VerifyMobileOTPModel>

    @FormUrlEncoded
    @POST(Constants.VERIFY_OTP)
    abstract fun verifyOTP(@Header("authorization") str: String, @FieldMap weakHashMap: WeakHashMap<String, Int>): Call<UpdateMobileModel>



    @GET(Constants.GET_KYC_DETAILS)
    abstract fun getKycDetails(@Header("authorization") str: String): Call<GetKycDetailsModel>

    @GET(Constants.DOC_TYPES)
    abstract fun attemptToGetKycDocTypes(@Header("authorization") str: String): Call<GetKycDocTypesModel>

    //
    @Multipart
    @POST(Constants.UPLOAD_DOCS)
    abstract fun uploadsDocs(@Header("authorization") user_token: String,
                             @Part kycdocfront: MultipartBody.Part,
                             @Part kycdocback: MultipartBody.Part,
                             @Part("docid")docid : RequestBody,
                             @Part("countryname")countryname : RequestBody,
                             @Part("legalname")legalname : RequestBody): Call<BaseModel>

    @GET(Constants.URL_NEWS)
    fun getNewsList():Call<NewsList>

    @GET(Constants.LANG)
    fun getLangAuth(@Header("authorization") str: String):Call<GetLangToken>

}