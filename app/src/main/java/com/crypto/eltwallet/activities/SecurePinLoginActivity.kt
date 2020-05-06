package com.crypto.eltwallet.activities

import `in`.aabhasjindal.otptextview.OTPListener
import `in`.aabhasjindal.otptextview.OtpTextView
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.crypto.eltwallet.R
import com.crypto.eltwallet.utilities.DialogBox
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.crypto.eltwallet.utilities.SharedPreferencesConstants
import com.google.android.material.snackbar.Snackbar

class SecurePinLoginActivity : AppCompatActivity() {

    var rl_main : RelativeLayout?=null
    var msg_gif : ImageView?=null
    var otp_view : OtpTextView?=null
    lateinit var sharedPreferenceUtil :SharedPreferenceUtil
    var type : String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secure_pin_login)
        supportActionBar!!.hide()

        sharedPreferenceUtil= SharedPreferenceUtil(this)
        initXml()
    }

    private  fun initXml()
    {
        rl_main = findViewById(R.id.rl_main)
        msg_gif = findViewById(R.id.msg_gif)
        otp_view = findViewById(R.id.otp_view)

        getSetData()
    }

    private fun getSetData()
    {
        //type = intent.getStringExtra("type")

        Glide.with(applicationContext).load(Integer.valueOf(R.drawable.emailgf)).into(this.msg_gif)
        otp_view!!.requestFocusOTP()

        OTPListener()
    }

    private fun OTPListener()
    {
        otp_view!!.otpListener = object : OTPListener
        {
            override fun onInteractionListener() {

            }

            override fun onOTPComplete(otp: String) {
                DialogBox.hideKeyboard(this@SecurePinLoginActivity)
                checkOTP(otp)
            }
        }
    }

    private fun checkOTP(otp: String)
    {
        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.SECURE_PIN,"").equals(otp))
        {
            sharedPreferenceUtil.setBoolean(SharedPreferencesConstants.isBackground, true)
            sharedPreferenceUtil.save()
            val intent = Intent(applicationContext, BaseDashboardActivity::class.java)
            //intent.putExtra("type",type)
            startActivity(intent)
            finish()
        }
        else
        {
            Snackbar.make(rl_main!!,resources.getString(R.string.please_enter_correct_secure_pin),Snackbar.LENGTH_LONG).show()
        }

    }
}
