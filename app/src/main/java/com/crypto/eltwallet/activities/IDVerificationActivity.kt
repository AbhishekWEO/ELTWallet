package com.crypto.eltwallet.activities

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.*
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class IDVerificationActivity : AppCompatActivity(), View.OnClickListener {

    var rl_main : RelativeLayout?=null
    var img_back : ImageView?=null
    var tv_email : TextView?=null
    var tv_very : TextView?=null
    var ll_emailVerified : LinearLayout?=null
    var tv_change_email : TextView?=null
    var progressEmailVerify : ProgressBar?=null
    var progressPhnVerify : ProgressBar?=null

    var tv_phNum : TextView?=null
    var tv_phNumVery : TextView?=null
    var ll_phnVerified : LinearLayout?=null
    var tv_change_phn : TextView?=null
    var rl_kycVerification : RelativeLayout?=null

    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    lateinit var user_token : String
    var isEmailVerified : Int = 0
    var phn_num : String?=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idverification)
        supportActionBar?.hide()

        sharedPreferenceUtil = SharedPreferenceUtil(this)
        user_token = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        initXml()
        setOnClickListener()
    }

    fun initXml()
    {
        rl_main = findViewById(R.id.rl_main)
        img_back = findViewById(R.id.img_back)
        tv_email = findViewById(R.id.tv_email)
        tv_very = findViewById(R.id.tv_very)
        ll_emailVerified = findViewById(R.id.ll_emailVerified)
        tv_change_email = findViewById(R.id.tv_change_email)
        progressEmailVerify = findViewById(R.id.progressEmailVerify)
        progressPhnVerify = findViewById(R.id.progressPhnVerify)

        tv_phNum = findViewById(R.id.tv_phNum)
        tv_phNumVery = findViewById(R.id.tv_phNumVery)
        ll_phnVerified = findViewById(R.id.ll_phnVerified)
        tv_change_phn = findViewById(R.id.tv_change_phn)
        rl_kycVerification = findViewById(R.id.rl_kycVerification)


        tv_very!!.visibility=View.GONE
        ll_emailVerified!!.visibility=View.GONE

        tv_phNumVery!!.visibility=View.GONE
        ll_phnVerified!!.visibility=View.GONE
    }

    fun setOnClickListener()
    {
        img_back!!.setOnClickListener(this)
        tv_very!!.setOnClickListener(this)
        tv_phNumVery!!.setOnClickListener(this)
        tv_change_email!!.setOnClickListener(this)
        tv_change_phn!!.setOnClickListener(this)
        rl_kycVerification!!.setOnClickListener(this)
    }

    override fun onClick(v: View?)
    {
        when (v!!.id)
        {
            R.id.img_back->
            {
                finish()
            }

            R.id.tv_very->
            {
                verifyEmail()
            }

            R.id.tv_phNumVery->
            {
                verifyMobileOTP()
            }
            R.id.tv_change_email->
            {
                DialogBox.showUpdateEmail(this,user_token)

            }
            R.id.tv_change_phn->
            {
                DialogBox.updateMobile(this)
            }

            R.id.rl_kycVerification->
            {
                getKycDetails()
            }

        }
    }



    override fun onResume() {
        super.onResume()

        if (UtilityPermissions.isNetworkAvailable(this))
        {
            getUserDetails()
        }
        else
        {
            Snackbar.make(rl_main!!, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
        }
    }

    fun getUserDetails()
    {
        DialogBox.showLoader(this)
        (RetrofitClient.getClient.attemptToUserProfile(user_token).enqueue(object :
            Callback<UserProfileModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<UserProfileModel>, response: Response<UserProfileModel>) {
                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@IDVerificationActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@IDVerificationActivity, ((response.body() as UserProfileModel).getMessage()).toString())
                }
                else if(response.code() == 200)
                {
                    phn_num = (response.body() as UserProfileModel).getPhonenumber()
                    tv_email!!.setText(((response.body() as UserProfileModel).getEmail()).toString())
                    tv_phNum!!.setText("+"+((response.body() as UserProfileModel).getCountryCode()).toString()+" "+((response.body() as UserProfileModel).getPhonenumber()).toString())

                    if (response.body()!!.getIsverifyemail().equals("0"))
                    {
                        isEmailVerified=0
                        tv_very!!.visibility=View.VISIBLE
                        ll_emailVerified!!.visibility=View.GONE
                    }
                    else
                    {
                        isEmailVerified=1
                        tv_very!!.visibility=View.GONE
                        ll_emailVerified!!.visibility=View.VISIBLE
                    }

                    if (response.body()!!.getIsverifymobile().equals("0"))
                    {
                        tv_phNumVery!!.visibility=View.VISIBLE
                        ll_phnVerified!!.visibility=View.GONE
                    }
                    else
                    {
                        tv_phNumVery!!.visibility=View.GONE
                        ll_phnVerified!!.visibility=View.VISIBLE
                    }
                }

            }

            override fun onFailure(call: Call<UserProfileModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(rl_main!!, th.message!!, Snackbar.LENGTH_LONG).show()

            }
        }))
    }


    public fun updateEmail(img_close:ImageView, progressBarUpdate: ProgressBar, tv_update:TextView, edt_email:String,
                           dialogE: Dialog
    )
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            DialogBox.showLoader(this)
            img_close.isEnabled=false
            progressBarUpdate.visibility=View.VISIBLE
            tv_update.setText("")

            var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
            weakHashMap.put(ConstantsRequest.EMAIL, edt_email)


            (RetrofitClient.getClient.updateEmail(user_token, weakHashMap).enqueue(
                object :Callback<UpdateEmailModel> {
                    @RequiresApi(api = 16)
                    override fun onResponse(call: Call<UpdateEmailModel>, response: Response<UpdateEmailModel>) {
                        dialogE.dismiss()
                        DialogBox.closeDialogE()
                        if(response == null)
                        {
                            Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                        }
                        else if(response.code() == 401)
                        {
                            DialogBox.showError(this@IDVerificationActivity, (response.errorBody()!!.string()).toString())
                        }
                        else if(response.code() == 202)
                        {
                            DialogBox.showError(this@IDVerificationActivity, ((response.body() as UpdateEmailModel).message))
                        }
                        else if(response.code() == 200)
                        {
                            tv_email!!.setText(edt_email)
                            tv_very!!.visibility=View.VISIBLE
                            ll_emailVerified!!.visibility=View.GONE

                            user_token=response.body()!!.usertoken
                            sharedPreferenceUtil!!.setString(SharedPreferencesConstants.USER_TOKEN,response.body()!!.usertoken)
                            sharedPreferenceUtil!!.save()
                            Snackbar.make(rl_main!!, resources.getString(R.string.your_email_updated_successfully), Snackbar.LENGTH_LONG).show()
                        }


                        img_close.isEnabled=true
                        progressBarUpdate.visibility=View.GONE
                        tv_update.setText(R.string.update)
                    }

                    override fun onFailure(call: Call<UpdateEmailModel>, th: Throwable) {
                        DialogBox.closeDialogE()
                        img_close.isEnabled=true
                        progressBarUpdate.visibility=View.GONE
                        tv_update.setText(R.string.update)
                        Snackbar.make(rl_main!!, th.message!!, Snackbar.LENGTH_LONG).show()

                    }
                }))

        }
        else
        {
            Snackbar.make(rl_main!!, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
        }

    }


    public fun updatePhn(img_close:ImageView, progressBarUpdate: ProgressBar, tv_update:TextView, countrycode:String,
                         mobileno:String, dialogE: Dialog)
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {

            img_close.isEnabled=false
            progressBarUpdate.visibility=View.VISIBLE
            tv_update.setText("")

            var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
            weakHashMap.put(ConstantsRequest.MOBILE_NO, mobileno)
            weakHashMap.put(ConstantsRequest.COUNTRYCODE, countrycode)


            (RetrofitClient.getClient.updateMobile(user_token, weakHashMap).enqueue(
                object :Callback<UpdateMobileModel> {
                    @RequiresApi(api = 16)
                    override fun onResponse(call: Call<UpdateMobileModel>, response: Response<UpdateMobileModel>) {
                        img_close.isEnabled=true
                        progressBarUpdate.visibility=View.GONE
                        tv_update.setText(R.string.update)

                        dialogE.dismiss()

                        if(response == null)
                        {
                            Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                        }
                        else if(response.code() == 401)
                        {
                            DialogBox.showError(this@IDVerificationActivity, (response.errorBody()!!.string()).toString())
                        }
                        else if(response.code() == 202)
                        {
                            DialogBox.showError(this@IDVerificationActivity, ((response.body() as UpdateMobileModel).message))
                        }
                        else if(response.code() == 200)
                        {
                            tv_phNumVery!!.visibility=View.VISIBLE
                            ll_phnVerified!!.visibility=View.GONE

                            phn_num = mobileno
                            tv_phNum!!.setText("+"+countrycode+" "+mobileno)
                            Snackbar.make(rl_main!!, ((response.body() as UpdateMobileModel).message), Snackbar.LENGTH_LONG).show()
                        }



                    }

                    override fun onFailure(call: Call<UpdateMobileModel>, th: Throwable) {

                        img_close.isEnabled=true
                        progressBarUpdate.visibility=View.GONE
                        tv_update.setText(R.string.update)
                        Snackbar.make(rl_main!!, th.message!!, Snackbar.LENGTH_LONG).show()

                    }
                }))

        }
        else
        {
            Snackbar.make(rl_main!!, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
        }

    }

    fun verifyEmail()
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            DialogBox.showLoader(this)

            img_back!!.isEnabled=false
            tv_change_email!!.isEnabled=false
            //progressEmailVerify!!.visibility=View.VISIBLE
            //tv_very!!.visibility=View.GONE

            var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
            weakHashMap.put(ConstantsRequest.EMAIL, tv_email!!.text.toString().trim())


            (RetrofitClient.getClient.verifyEmail(user_token, weakHashMap).enqueue(
                object :Callback<UpdateMobileModel> {
                    @RequiresApi(api = 16)
                    override fun onResponse(call: Call<UpdateMobileModel>, response: Response<UpdateMobileModel>) {

                        DialogBox.closeDialogE()
                        img_back!!.isEnabled=true
                        tv_change_email!!.isEnabled=true
                        //progressEmailVerify!!.visibility=View.GONE
                        //tv_very!!.visibility=View.VISIBLE

                        if(response == null)
                        {
                            Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                        }
                        else if(response.code() == 401)
                        {
                            DialogBox.showError(this@IDVerificationActivity, (response.errorBody()!!.string()).toString())
                        }
                        else if(response.code() == 202)
                        {
                            DialogBox.showError(this@IDVerificationActivity, ((response.body() as UpdateMobileModel).message))
                        }
                        else if(response.code() == 200)
                        {
                            Snackbar.make(rl_main!!, ((response.body() as UpdateMobileModel).message), Snackbar.LENGTH_LONG).show()
                        }



                    }

                    override fun onFailure(call: Call<UpdateMobileModel>, th: Throwable) {
                        DialogBox.closeDialogE()
                        img_back!!.isEnabled=true
                        tv_change_email!!.isEnabled=true
                        //progressEmailVerify!!.visibility=View.GONE
                        //tv_very!!.visibility=View.VISIBLE
                        Snackbar.make(rl_main!!, th.message!!, Snackbar.LENGTH_LONG).show()

                    }
                }))

        }
        else
        {
            Snackbar.make(rl_main!!, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
        }

    }
    fun verifyMobileOTP()
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            DialogBox.showLoader(this)

            img_back!!.isEnabled=false
            tv_change_phn!!.isEnabled=false
           //progressPhnVerify!!.visibility=View.VISIBLE
            //tv_phNumVery!!.visibility=View.GONE

            RetrofitClient.getClient.verifyMobileOTP(user_token).enqueue(object :
                Callback<VerifyMobileOTPModel> {
                @RequiresApi(api = 16)
                override fun onResponse(call: Call<VerifyMobileOTPModel>, response: Response<VerifyMobileOTPModel>) {

                    DialogBox.closeDialogE()
                    img_back!!.isEnabled=true
                    tv_change_phn!!.isEnabled=true
                    //progressPhnVerify!!.visibility=View.GONE
                    //tv_phNumVery!!.visibility=View.VISIBLE
                    if(response == null)
                    {
                        Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code() == 401)
                    {
                        DialogBox.showError(this@IDVerificationActivity, (response.errorBody()!!.string()).toString())
                    }
                    else if(response.code() == 202)
                    {
                        DialogBox.showError(this@IDVerificationActivity, ((response.body() as VerifyMobileOTPModel).message))
                    }
                    else if(response.code() == 200)
                    {
                        val intent = Intent(this@IDVerificationActivity, OtpSignupActivity::class.java)
                        intent.putExtra(ConstantsRequest.ISVERIFYMOBILEOTP, "YES")
                        intent.putExtra(ConstantsRequest.AUTHINFO,user_token)
                        intent.putExtra(ConstantsRequest.PHONENUMBER, phn_num)//tv_phNum!!.text.toString().trim()
                        intent.putExtra(ConstantsRequest.TIMER_DURATION_SEC,
                            (response.body() as VerifyMobileOTPModel).timer_duration_sec.toString())
                        startActivity(intent)
                    }

                }

                override fun onFailure(call: Call<VerifyMobileOTPModel>, th: Throwable) {
                    DialogBox.closeDialogE()
                    img_back!!.isEnabled=true
                    tv_change_phn!!.isEnabled=true
                    //progressPhnVerify!!.visibility=View.GONE
                    //tv_phNumVery!!.visibility=View.VISIBLE

                    Snackbar.make(rl_main!!, th.message!!, Snackbar.LENGTH_LONG).show()

                }
            })
        }
        else
        {

            Snackbar.make(rl_main!!, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
        }

    }

    private fun getKycDetails()
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            DialogBox.showLoader(this)
            img_back!!.isEnabled=false
            tv_change_email!!.isEnabled=false
            tv_change_phn!!.isEnabled=false

            RetrofitClient.getClient.getKycDetails(user_token).enqueue(object :
                Callback<GetKycDetailsModel> {
                @RequiresApi(api = 16)
                override fun onResponse(call: Call<GetKycDetailsModel>, response: Response<GetKycDetailsModel>) {

                    DialogBox.closeDialogE()

                    img_back!!.isEnabled=true
                    tv_change_email!!.isEnabled=true
                    tv_change_phn!!.isEnabled=true

                    if(response == null)
                    {
                        Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code() == 401)
                    {
                        DialogBox.showError(this@IDVerificationActivity, (response.errorBody()!!.string()).toString())
                    }
                    else if(response.code() == 202)
                    {
                        DialogBox.showError(this@IDVerificationActivity, ((response.body() as GetKycDetailsModel).message))
                    }
                    else if(response.code() == 200)
                    {
                        if ((response.body() as GetKycDetailsModel).kyc_status.equals("0"))
                        {
                            Snackbar.make(rl_main!!, resources.getString(R.string.please_verify_your_phone_number_and_email_address), Snackbar.LENGTH_LONG).show()
                        }
                        else if ((response.body() as GetKycDetailsModel).kyc_status.equals("1"))
                        {
                            if ((response.body() as GetKycDetailsModel).doc_status.equals("0"))
                            {
                                Snackbar.make(rl_main!!, resources.getString(R.string.kyc_document_already_uploaded_msg), Snackbar.LENGTH_LONG).show()
                            }
                            else if ((response.body() as GetKycDetailsModel).doc_status.equals("1"))
                            {
                                Snackbar.make(rl_main!!, resources.getString(R.string.kyc_documents_accepted_msg), Snackbar.LENGTH_LONG).show()
                            }
                            else if ((response.body() as GetKycDetailsModel).doc_status.equals("2"))
                            {
                                Snackbar.make(rl_main!!, resources.getString(R.string.kyc_documents_rejected_msg), Snackbar.LENGTH_LONG).show()
                                sharedPreferenceUtil!!.setString(SharedPreferencesConstants.LEGAL_NAME,(response.body() as GetKycDetailsModel).legal_name)
                                sharedPreferenceUtil!!.save()
                                var intent = Intent(this@IDVerificationActivity,KycVerificationActivity::class.java)
                                intent.putExtra(ConstantsRequest.COUNTRY_NAME,(response.body() as GetKycDetailsModel).country_name)
                                startActivity(intent)
                            }
                            else if ((response.body() as GetKycDetailsModel).doc_status.equals("3"))
                            {
                                sharedPreferenceUtil!!.setString(SharedPreferencesConstants.LEGAL_NAME,(response.body() as GetKycDetailsModel).legal_name)
                                sharedPreferenceUtil!!.save()

                                var intent = Intent(this@IDVerificationActivity,KycVerificationActivity::class.java)
                                intent.putExtra(ConstantsRequest.COUNTRY_NAME,(response.body() as GetKycDetailsModel).country_name)
                                startActivity(intent)
                            }

                        }
                        else if ((response.body() as GetKycDetailsModel).kyc_status.equals("2"))
                        {
                            Snackbar.make(rl_main!!, resources.getString(R.string.kyc_documents_verified_msg), Snackbar.LENGTH_LONG).show()
                        }
                    }

                }

                override fun onFailure(call: Call<GetKycDetailsModel>, th: Throwable) {

                    DialogBox.closeDialogE()

                    img_back!!.isEnabled=true
                    tv_change_email!!.isEnabled=true
                    tv_change_phn!!.isEnabled=true

                    Snackbar.make(rl_main!!, th.message!!, Snackbar.LENGTH_LONG).show()

                }
            })
        }
        else
        {

            Snackbar.make(rl_main!!, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
        }
    }


}
