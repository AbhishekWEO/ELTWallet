package com.crypto.eltwallet.activities

import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.crypto.eltwallet.R
import com.crypto.eltwallet.fingerPrintsInterface.FingerPrintAuthCallback
import com.crypto.eltwallet.model.UserProfileModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountSecurityActivity : AppCompatActivity(), View.OnClickListener, FingerPrintAuthCallback {

    var img_back : ImageView?=null
    var rl_main : RelativeLayout?=null
    var rl_securePin : RelativeLayout?=null
    var tv_securePin :TextView?=null
    var switch_securePin : SwitchCompat?=null
    var switch_faceId : SwitchCompat?=null
    var rl_changePwd : RelativeLayout?=null


    var sharedPreferenceUtil: SharedPreferenceUtil?=null
    var mFingerPrintAuthHelper: FingerPrintAuthHelper? = null
    var user_token : String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_security)
        supportActionBar?.hide()

        sharedPreferenceUtil = SharedPreferenceUtil(this)
        user_token = sharedPreferenceUtil!!.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()

        initXml()
        setOnCheckedChangeListener()
        setOnClickListener()
    }

    private fun initXml()
    {
        rl_main = findViewById(R.id.rl_main)
        img_back = findViewById(R.id.img_back)
        rl_securePin = findViewById(R.id.rl_securePin)
        tv_securePin = findViewById(R.id.tv_securePin)
        switch_securePin = findViewById(R.id.switch_securePin)
        switch_faceId = findViewById(R.id.switch_faceId)
        rl_changePwd = findViewById(R.id.rl_changePwd)

        getSetData()
    }

    private fun getSetData()
    {
        if(sharedPreferenceUtil!!.getString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"").toString().equals("true"))
        {
            switch_securePin!!.isChecked=true
        }
        else
        {
            switch_securePin!!.isChecked=false

        }


        //touch id
        if (sharedPreferenceUtil!!.getBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)!!)
        {
            switch_faceId!!.setChecked(true)
        }
        else
        {
            switch_faceId!!.setChecked(false)
        }

    }

    private fun setOnCheckedChangeListener()
    {
        switch_faceId!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this@AccountSecurityActivity,
                    this@AccountSecurityActivity)
                mFingerPrintAuthHelper!!.startAuth()
                sharedPreferenceUtil!!.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, true)
                sharedPreferenceUtil!!.save()
            } else {
                sharedPreferenceUtil!!.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
                sharedPreferenceUtil!!.save()
            }
        })

        //
        switch_securePin!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
            {
                if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"").toString().equals("true"))
                {
                    switch_securePin!!.isChecked=false
                    sharedPreferenceUtil!!.setString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"false")
                    sharedPreferenceUtil!!.save()
                }
                else
                {
                    if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.IS_CREATE_SECURE_PIN,"").toString().equals("true"))
                    {
                        switch_securePin!!.isChecked=true
                        sharedPreferenceUtil!!.setString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"true")
                        sharedPreferenceUtil!!.save()
                    }
                    else
                    {
                        switch_securePin!!.isChecked=false
                        sharedPreferenceUtil!!.setString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"false")
                        sharedPreferenceUtil!!.save()
                    }

                }
            }
            else {
                switch_securePin!!.isChecked=false
                sharedPreferenceUtil!!.setString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"false")
                sharedPreferenceUtil!!.save()
            }
        })
    }

    private fun setOnClickListener()
    {
        img_back!!.setOnClickListener(this)
        rl_securePin!!.setOnClickListener(this)
        switch_securePin!!.setOnClickListener(this)
        switch_faceId!!.setOnClickListener(this)
        rl_changePwd!!.setOnClickListener(this)
    }

    override fun onClick(v: View?)
    {
        when (v!!.id)
        {
            R.id.img_back->
            {
                finish()
            }
            R.id.rl_securePin->
            {
                var intent = Intent(this,SecurePinActivity::class.java)
                startActivity(intent)
            }
            R.id.switch_securePin->
            {
                /*if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"").toString().equals("true"))
                {
                    switch_securePin!!.isChecked=false
                    sharedPreferenceUtil!!.setString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"false")
                    sharedPreferenceUtil!!.save()
                }
                else
                {
                    if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.IS_CREATE_SECURE_PIN,"").toString().equals("true"))
                    {
                        switch_securePin!!.isChecked=true
                        sharedPreferenceUtil!!.setString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"true")
                        sharedPreferenceUtil!!.save()
                    }
                    else
                    {
                        switch_securePin!!.isChecked=false
                        sharedPreferenceUtil!!.setString(SharedPreferencesConstants.ENABLE_SECURE_PIN,"false")
                        sharedPreferenceUtil!!.save()
                    }

                }

               */
            }
            R.id.switch_faceId->
            {
            }
            R.id.rl_changePwd->
            {
                var intent = Intent(this,ChangePasswordActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.IS_CREATE_SECURE_PIN,"").toString().equals("true"))
        {
            tv_securePin!!.setText(resources.getString(R.string.change_secure_pin))
            switch_securePin!!.isEnabled=true
        }
        else
        {
            tv_securePin!!.setText(resources.getString(R.string.create_secure_pin))
            switch_securePin!!.isEnabled=false
        }

        //checking for user authentication
        if (UtilityPermissions.isNetworkAvailable(this))
        {
            getUserDetails()
        }
        else
        {
            Snackbar.make(rl_main!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mFingerPrintAuthHelper != null)
        {
            mFingerPrintAuthHelper!!.stopAuth()
        }

    }

    override fun onNoFingerPrintHardwareFound()
    {
        DialogBox.showError(this@AccountSecurityActivity, resources.getString(R.string.no_hardware))
        switch_faceId!!.setChecked(false)
        sharedPreferenceUtil!!.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil!!.save()
    }

    override fun onNoFingerPrintRegistered() {
        DialogBox.showError(this@AccountSecurityActivity, resources.getString(R.string.please_enable_touchid))
        switch_faceId!!.setChecked(false)
        sharedPreferenceUtil!!.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil!!.save()
    }

    override fun onBelowMarshmallow() {
        // Log.e(TAG, "onBelowMarshmallow: " );
        DialogBox.showError(this@AccountSecurityActivity, resources.getString(R.string.update_yourmobile))
        switch_faceId!!.setChecked(false)
        sharedPreferenceUtil!!.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil!!.save()
    }

    override fun onAuthSuccess(cryptoObject: FingerprintManager.CryptoObject?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAuthFailed(errorCode: Int, errorMessage: String?) {
        /*DialogBox.showError(this@AccountSecurityActivity, errorMessage!!)
        switch_faceId!!.setChecked(false)
        sharedPreferenceUtil!!.setBoolean(SharedPreferencesConstants.IS_FINGER_PRINT_ENABLE, false)
        sharedPreferenceUtil!!.save()*/
    }


    fun getUserDetails()
    {
        RetrofitClient.getClient.attemptToUserProfile(user_token).enqueue(object :
            Callback<UserProfileModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<UserProfileModel>, response: Response<UserProfileModel>) {

                if(response == null)
                {
                    //Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@AccountSecurityActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    //DialogBox.showError(this@AccountSecurityActivity, ((response.body() as UserProfileModel).getMessage()).toString())
                }
                else if(response.code() == 200)
                {

                }

            }

            override fun onFailure(call: Call<UserProfileModel>, th: Throwable) {

                //Snackbar.make(rl_main!!, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        })
    }

}
