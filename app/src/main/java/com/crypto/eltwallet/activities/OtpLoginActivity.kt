package com.crypto.eltwallet.activities

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.LoginOtpModel
import com.crypto.eltwallet.model.ResendLoginOtpModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.ConstantsRequest
import com.crypto.eltwallet.utilities.DialogBox
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.crypto.eltwallet.utilities.SharedPreferencesConstants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OtpLoginActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout : RelativeLayout
    lateinit var back : ImageView
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    lateinit var input_username: String
    lateinit var input_password: String
    lateinit var input_message: String
    lateinit var input_otp_receiver: String

    private var otpTextView: OtpTextView? = null
    lateinit var msg_gif: ImageView
    lateinit var phnno: TextView
    lateinit var subheading_verify: TextView

    lateinit var resendlayout : RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_login)

        supportActionBar?.hide()

        linearLayout = findViewById(R.id.linearLayout)
        back = findViewById(R.id.back)
        back.setOnClickListener(){
            onBackPressed()
        }
        sharedPreferenceUtil = SharedPreferenceUtil(this)

        msg_gif = findViewById(R.id.msg_gif)
        phnno = findViewById(R.id.phnno)
        subheading_verify = findViewById(R.id.subheading_verify)
        resendlayout = findViewById(R.id.resendlayout)

        Glide.with(applicationContext).load(Integer.valueOf(R.drawable.emailgf)).into(this.msg_gif)

        val intent = intent
        if (intent != null) {
            input_username = intent.getStringExtra(ConstantsRequest.USERNAME)
            input_password = intent.getStringExtra(ConstantsRequest.PASSWORD)
            input_message = intent.getStringExtra(ConstantsRequest.MESSAGE)
            input_otp_receiver = intent.getStringExtra(ConstantsRequest.OTP_RECEIVER)

            phnno.setText(input_otp_receiver)
            subheading_verify.setText(input_message)

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
                DialogBox.showLoader(this@OtpLoginActivity)
                newRequestForVerifyOtp(input_username, otp)
            }
        }

        resendlayout.setOnClickListener(){
            DialogBox.showLoader(this@OtpLoginActivity)
            requestForResendOtp(input_username, input_password)
        }
    }



    fun newRequestForVerifyOtp(input_username: String, otp: String) {
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
        weakHashMap.put(ConstantsRequest.USERLOGIN, input_username)
        weakHashMap.put(ConstantsRequest.INPUT_OTP, otp)
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

        (RetrofitClient.getClient.attemptToLoginOtp(weakHashMap).enqueue(object :
            Callback<LoginOtpModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<LoginOtpModel>, response: Response<LoginOtpModel>) {

                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@OtpLoginActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@OtpLoginActivity, (response.body() as LoginOtpModel).message)
                }
                else if(response.code() == 200)
                {
                    Log.e("data response", (response.body() as LoginOtpModel).usertoken)

                    sharedPreferenceUtil.setString(SharedPreferencesConstants.USER_TOKEN,
                        (response.body() as LoginOtpModel).usertoken)
                    sharedPreferenceUtil.save()

                    val intent = Intent(applicationContext, BaseDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }

            }

            override fun onFailure(call: Call<LoginOtpModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
            }
        }))
    }


    fun requestForResendOtp(input_username: String, password: String) {
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
        weakHashMap.put(ConstantsRequest.USERLOGIN, input_username)
        weakHashMap.put(ConstantsRequest.PASSWORD, password)

        Log.e("message1", input_username + password)

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

        (RetrofitClient.getClient.attemptToResendOtp(weakHashMap).enqueue(object :
            Callback<ResendLoginOtpModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<ResendLoginOtpModel>, response: Response<ResendLoginOtpModel>) {
                DialogBox.closeDialogE()
                Log.e("message", response.body().toString())


                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@OtpLoginActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@OtpLoginActivity, (response.body() as ResendLoginOtpModel).message)
                }
                else if(response.code() == 200)
                {

                    phnno.setText((response.body() as ResendLoginOtpModel).otp_receiver)
                    subheading_verify.setText((response.body() as ResendLoginOtpModel).otp_message)

                }

            }

            override fun onFailure(call: Call<ResendLoginOtpModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
            }
        }))
    }


}
