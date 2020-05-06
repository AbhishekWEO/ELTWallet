package com.crypto.eltwallet.activities

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.*
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.crypto.eltwallet.utilities.BaseActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class OtpSignupActivity : BaseActivity() {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout : RelativeLayout
    lateinit var back : ImageView

    lateinit var input_name: String
    lateinit var input_mobile: String
    lateinit var input_country_code: String
    lateinit var input_email: String
    lateinit var input_password: String
    lateinit var input_auth_info: String
    lateinit var input_timer_duration_sec: String

    private var otpTextView: OtpTextView? = null
    lateinit var msg_gif: ImageView
    lateinit var phnno : TextView

    lateinit var timer_duration_sec: TextView
    lateinit var resend: TextView
    private var timer : Int = 0

    private var ISVERIFYMOBILEOTP : Boolean = false
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_signup)

        supportActionBar?.hide()
        sharedPreferenceUtil = SharedPreferenceUtil(this)

        linearLayout = findViewById(R.id.linearLayout)
        back = findViewById(R.id.back)
        back.setOnClickListener(){
            onBackPressed()
        }
        msg_gif = findViewById(R.id.msg_gif)
        phnno = findViewById(R.id.phnno)
        timer_duration_sec = findViewById(R.id.timer_duration_sec)
        resend = findViewById(R.id.resend)

        Glide.with(applicationContext).load(Integer.valueOf(R.drawable.emailgf)).into(this.msg_gif)

        val intent = intent
        if (intent != null)
        {
            //
            if (intent.getStringExtra(ConstantsRequest.ISVERIFYMOBILEOTP).equals("NO"))
            {
                ISVERIFYMOBILEOTP = false
                input_name = intent.getStringExtra(ConstantsRequest.USERNAME)
                input_mobile = intent.getStringExtra(ConstantsRequest.PHONENUMBER)
                input_country_code = intent.getStringExtra(ConstantsRequest.COUNTRYCODE)
                input_email = intent.getStringExtra(ConstantsRequest.EMAIL)
                input_password = intent.getStringExtra(ConstantsRequest.PASSWORD)
                input_auth_info = intent.getStringExtra(ConstantsRequest.AUTHINFO)
                input_timer_duration_sec = intent.getStringExtra(ConstantsRequest.TIMER_DURATION_SEC)

                phnno.setText(encodedMobileNo(input_mobile))
                timer = Integer.parseInt(input_timer_duration_sec) * 1000
            }
            else if (intent.getStringExtra(ConstantsRequest.ISVERIFYMOBILEOTP).equals("YES"))
            {
                resend.visibility = View.GONE
                ISVERIFYMOBILEOTP = true
                input_auth_info = intent.getStringExtra(ConstantsRequest.AUTHINFO)
                input_mobile = intent.getStringExtra(ConstantsRequest.PHONENUMBER)
                input_timer_duration_sec = intent.getStringExtra(ConstantsRequest.TIMER_DURATION_SEC)

                phnno.setText(encodedMobileNo(input_mobile))
                timer = Integer.parseInt(input_timer_duration_sec) * 1000

            }

            setTimer()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.black)
        }

        otpTextView = findViewById(R.id.otp_view)
        otpTextView?.requestFocusOTP()
        otpTextView?.otpListener = object : OTPListener {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                if (ISVERIFYMOBILEOTP)
                {
                    verifyOTP(otp)
                }
                else
                {
                    if (UtilityPermissions.isNetworkAvailable(this@OtpSignupActivity))
                    {
                        DialogBox.showLoader(this@OtpSignupActivity)
                        newRequestForVerifyOtp(otp, input_auth_info)
                    }
                    else
                    {
                        Snackbar.make(linearLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
                    }

                }

            }
        }

        resend.setOnClickListener(){

            if (ISVERIFYMOBILEOTP)
            {
                if (UtilityPermissions.isNetworkAvailable(this))
                {
                    DialogBox.showLoader(this)
                    verifyMobileOTP()
                }
                else
                {
                    Snackbar.make(linearLayout, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
                }


            }
            else
            {
                if (UtilityPermissions.isNetworkAvailable(this))
                {
                    DialogBox.showLoader(this)
                    attemptToSignup(input_name.toString(), input_email.toString(),
                        input_mobile.toString(), input_password.toString(), input_country_code.toString())
                }
                else
                {
                    Snackbar.make(linearLayout, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
                }


            }


        }
    }

    private fun encodedMobileNo(str: String): String {
        val charArray = str.toCharArray()
        val cArr = CharArray(10)
        for (i in 0 until str.length) {
            if (i <= 1 || i >= 7) {
                cArr[i] = charArray[i]
            } else {
                cArr[i] = '*'
            }
        }
        val str2 = this.TAG
        val sb = StringBuilder()
        sb.append("onCreate: ")
        sb.append(String(cArr))
        Log.e(str2, sb.toString())
        return String(cArr)
    }


    fun newRequestForVerifyOtp(otp: String, input_auth_info: String) {
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
        weakHashMap.put(ConstantsRequest.INPUT_OTP, otp)
        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "").equals("Chinese"))
        {
            weakHashMap.put(ConstantsRequest.lang, "zh")
        }
        else
        {
            weakHashMap.put(ConstantsRequest.lang, "en")
        }
//        if (this.preferenceUtil.getString(
//                SharedPreferenceConstant.language,
//                ""
//            ).equalsIgnoreCase("Chinese")
//        ) {
//            weakHashMap.put(Constants.lang, "zh")
//        } else {
//            weakHashMap.put(Constants.lang, "en")
//        }
//        this.progress = ProgressDialog(this.context)
//        this.progress.setMessage("Please wait")
//        this.progress.setProgressStyle(0)
//        this.progress.setIndeterminate(true)
//        this.progress.setCancelable(false)
//        this.progress.show()
//        Log.e("Sending data", weakHashMap.toString())

        (RetrofitClient.getClient.attemptToNewSignUpOtp(input_auth_info, weakHashMap).enqueue(object :
            Callback<SignupOtpModel>{
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<SignupOtpModel>, response: Response<SignupOtpModel>) {

                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@OtpSignupActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@OtpSignupActivity, (response.body() as SignupOtpModel).message)
                }
                else if(response.code() == 200)
                {
                    val intent = Intent(applicationContext, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            override fun onFailure(call: Call<SignupOtpModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }

    private fun attemptToSignup(input_name: String, input_email: String,
        input_mobile: String, input_password: String, input_country_code : String) {

        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()

        weakHashMap.put(ConstantsRequest.USERNAME, input_name)
        weakHashMap.put(ConstantsRequest.PHONENUMBER, input_mobile)
        weakHashMap.put(ConstantsRequest.COUNTRYCODE, input_country_code)
        weakHashMap.put(ConstantsRequest.EMAIL, input_email)
        weakHashMap.put(ConstantsRequest.PASSWORD, input_password)
        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "").equals("Chinese"))
        {
            weakHashMap.put(ConstantsRequest.lang, "zh")
        }
        else
        {
            weakHashMap.put(ConstantsRequest.lang, "en")
        }

//        weakHashMap.put(
//            Constants.username,
//            this.edt_username.getText().toString().trim({ it <= ' ' })
//        )
//        weakHashMap.put("email", this.edt_email.getText().toString().trim({ it <= ' ' }))
//        weakHashMap.put("password", this.edt_password.getText().toString().trim({ it <= ' ' }))
//        if (this.preferenceUtil.getString(
//                SharedPreferenceConstant.language,
//                ""
//            ).equalsIgnoreCase("Chinese")
//        ) {
//            weakHashMap.put(Constants.lang, "zh")
//        } else {
//            weakHashMap.put(Constants.lang, "en")
//        }
//        weakHashMap.put(
//            Constants.phonenumber,
//            this.edt_mobile.getText().toString().trim({ it <= ' ' })
//        )
//        val str = Constants.countrycode
//        val sb = StringBuilder()
//        sb.append("+")
//        sb.append(this.ccp.getSelectedCountryCode())
//        weakHashMap.put(str, sb.toString())
//        this.progress = ProgressDialog(getActivity())
//        this.progress.setMessage("Please wait")
//        this.progress.setProgressStyle(0)
//        this.progress.setIndeterminate(true)
//        this.progress.setCancelable(false)
//        this.progress.show()
//        Log.e("Sending data", weakHashMap.toString())

        (RetrofitClient.getClient.attemptToSignup(weakHashMap).enqueue(object :
            Callback<SignupModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<SignupModel>, response: Response<SignupModel>) {
                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@OtpSignupActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@OtpSignupActivity, (response.body() as SignupModel).message)
                }
                else if(response.code() == 200)
                {
                    input_auth_info = (response.body() as SignupModel).authinfo

                    timer = ((response.body() as SignupModel).timer_duration_sec)*1000
                    resend.visibility = View.GONE
                    setTimer()

                }

            }

            override fun onFailure(call: Call<SignupModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }


    fun setTimer() {
        object : CountDownTimer(timer.toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {

                val f = DecimalFormat("00")
                val minutes = millisUntilFinished / 60000
                val seconds = millisUntilFinished % 60000 / 1000

                timer_duration_sec.text = resources.getString(R.string.expire) + "\n" + f.format(minutes) + ":" + f.format(seconds)

            }

            override fun onFinish() {
                resend.visibility = View.VISIBLE
                timer_duration_sec.text = resources.getString(R.string.send_again)
            }
        }.start()
    }

    fun verifyMobileOTP()
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {

            RetrofitClient.getClient.verifyMobileOTP(input_auth_info).enqueue(object :
                Callback<VerifyMobileOTPModel> {
                @RequiresApi(api = 16)
                override fun onResponse(call: Call<VerifyMobileOTPModel>, response: Response<VerifyMobileOTPModel>) {
                    DialogBox.closeDialogE()
                    if(response == null)
                    {
                        Snackbar.make(linearLayout!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code() == 401)
                    {
                        DialogBox.showError(this@OtpSignupActivity, (response.errorBody()!!.string()).toString())
                    }
                    else if(response.code() == 202)
                    {
                        DialogBox.showError(this@OtpSignupActivity, ((response.body() as VerifyMobileOTPModel).message))
                    }
                    else if(response.code() == 200)
                    {
                        timer = ((response.body() as VerifyMobileOTPModel).timer_duration_sec)*1000
                        resend.visibility = View.GONE
                        setTimer()
                    }

                }

                override fun onFailure(call: Call<VerifyMobileOTPModel>, th: Throwable) {
                    DialogBox.closeDialogE()
                    Snackbar.make(linearLayout, th.message!!, Snackbar.LENGTH_LONG).show()

                }
            })
        }
        else
        {

            Snackbar.make(linearLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
        }

    }
    private fun verifyOTP(otp: String)
    {
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            DialogBox.showLoader(this@OtpSignupActivity)

            var weakHashMap: WeakHashMap<String, Int> = WeakHashMap<String, Int>()
            weakHashMap.put(ConstantsRequest.INPUT_OTP, Integer.parseInt(otp))


            (RetrofitClient.getClient.verifyOTP(input_auth_info, weakHashMap).enqueue(object :
                Callback<UpdateMobileModel>{
                @RequiresApi(api = 16)
                override fun onResponse(call: Call<UpdateMobileModel>, response: Response<UpdateMobileModel>) {
                    DialogBox.closeDialogE()
                    if(response == null)
                    {
                        Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code() == 401)
                    {
                        DialogBox.showError(this@OtpSignupActivity, (response.errorBody()!!.string()).toString())
                    }
                    else if(response.code() == 202)
                    {
                        DialogBox.showError(this@OtpSignupActivity, (response.body() as UpdateMobileModel).message)
                    }
                    else if(response.code() == 200)
                    {
                        finish()
                    }

                }

                override fun onFailure(call: Call<UpdateMobileModel>, th: Throwable) {
                    DialogBox.closeDialogE()
                    Snackbar.make(linearLayout,th.message!!, Snackbar.LENGTH_LONG).show()

                }
            }))
        }
        else
        {
            Snackbar.make(linearLayout, R.string.no_internet_connection, Snackbar.LENGTH_LONG).show()
        }


    }
}
