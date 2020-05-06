package com.crypto.eltwallet.adapter

import android.content.Context
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.ForgotActivity
import com.crypto.eltwallet.activities.OtpLoginActivity
import com.crypto.eltwallet.activities.OtpSignupActivity
import com.crypto.eltwallet.model.AuthModel
import com.crypto.eltwallet.model.LoginModel
import com.crypto.eltwallet.model.SignupModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_signup.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CustomPagerAdapter(private val mContext: Context, private val countryCode : String) : PagerAdapter() {

    private val TAG = this::class.java.simpleName

    lateinit var linearLayout: LinearLayout
    lateinit var linearLayout_login: LinearLayout
    private var passwordVisibility = false
    private var passwordVisibility1 = false
    private var passwordVisibility2 = false
    lateinit var edt_password: EditText
    lateinit var edt_password1: EditText
    lateinit var edt_repassword: EditText
    lateinit var forget: TextView
    lateinit var ccp : CountryCodePicker
    var token : String=""
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val modelObject = AuthModel.values()[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(modelObject.layoutResId, collection, false) as ViewGroup

        sharedPreferenceUtil = SharedPreferenceUtil(mContext)
        DialogBox.showLoginLoader(mContext)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                token = task.result?.token!!

                // Log and toast
                //val msg = getString(R.string.msg_token_fmt, token)
                Log.d("FirebaseToken", token)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            })

        if (position == 0)
        {


            edt_password = layout.findViewById(R.id.edt_password)

            layout.txt_login.setOnClickListener{

                linearLayout_login = layout.findViewById(R.id.linearLayout_login)
                val input_email_name = layout.emailorusername.text
                val input_password_login = layout.edt_password.text

                if (Validations.validateForLogin(input_email_name.toString(), input_password_login.toString(),
                        linearLayout_login))
                {
                    //DialogBox.showLoader(mContext)
                    DialogBox.showLoginLoader2()
                    requestForLogin(input_email_name.toString(), input_password_login.toString(),token)
                }

            }

            layout.eye.setOnClickListener{
                manageVisibilityOfPassword()
            }

            forget = layout.findViewById(R.id.forget)
            forget.setOnClickListener(){

                val intent = Intent(mContext, ForgotActivity::class.java)
                mContext.startActivity(intent)
            }

        } else if (position == 1) {

            edt_password1 = layout.findViewById(R.id.edt_password1)
            edt_repassword = layout.findViewById(R.id.edt_repassword)

            ccp = layout.findViewById(R.id.ccp)
            if(!(countryCode.equals("")))
            {
                ccp.setCountryForNameCode(countryCode)
            }

            layout.txt_signup.setOnClickListener() {

                val input_name = layout.edt_name.text
                val input_email = layout.edt_email.text
                val input_mobile = layout.edt_mobile.text
                val input_password = layout.edt_password1.text
                val input_re_password = layout.edt_repassword.text
                val input_ccp = ccp.selectedCountryCode
                linearLayout = layout.findViewById(R.id.linearLayout)

                if (Validations.validateForSignUp(input_name.toString(), input_email.toString(),
                        input_password.toString(), input_re_password.toString(), input_mobile.toString(), linearLayout))
                {
                    DialogBox.showLoginLoader2()
                    attemptToSignup(input_name.toString(), input_email.toString(), input_mobile.toString(),
                        input_password.toString(), input_ccp.toString())
                }
            }

            layout.eye1.setOnClickListener{
                manageVisibilityOfPassword1()
            }

            layout.eye2.setOnClickListener{
                manageVisibilityOfPassword2()
            }

        }

        collection.addView(layout)
        return layout
    }

    private fun manageVisibilityOfPassword() {
        if (passwordVisibility) {
            passwordVisibility = false
            edt_password.transformationMethod = PasswordTransformationMethod()
            edt_password.setSelection(edt_password.getText().length)
            return
        }
        passwordVisibility = true
        edt_password.transformationMethod = HideReturnsTransformationMethod()
        edt_password.setSelection(edt_password.getText().length)

    }

    private fun manageVisibilityOfPassword1() {
        if (passwordVisibility1) {
            passwordVisibility1 = false
            edt_password1.transformationMethod = PasswordTransformationMethod()
            edt_password1.setSelection(edt_password1.getText().length)
            return
        }
        passwordVisibility1 = true
        edt_password1.transformationMethod = HideReturnsTransformationMethod()
        edt_password1.setSelection(edt_password1.getText().length)
    }

    private fun manageVisibilityOfPassword2() {
        if (passwordVisibility2) {
            passwordVisibility2 = false
            edt_repassword.transformationMethod = PasswordTransformationMethod()
            edt_repassword.setSelection(edt_repassword.getText().length)
            return
        }
        passwordVisibility2 = true
        edt_repassword.transformationMethod = HideReturnsTransformationMethod()
        edt_repassword.setSelection(edt_repassword.getText().length)
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return AuthModel.values().size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getPageTitle(position: Int): CharSequence {
        val customPagerEnum = AuthModel.values()[position]
        return mContext.getString(customPagerEnum.titleResId)
    }


    private fun requestForLogin(input_email_name: String, input_password_login: String, token: String) {
        //DialogBox.showLoader(mContext)//21-04-2020
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()

        weakHashMap.put(ConstantsRequest.USERLOGIN, input_email_name)
        weakHashMap.put(ConstantsRequest.PASSWORD, input_password_login)
        weakHashMap.put(ConstantsRequest.DEVICE_TOKEN, token)


        if (sharedPreferenceUtil.getString(SharedPreferencesConstants.LANGUAGE, "").equals("Chinese"))
        {
            weakHashMap.put(ConstantsRequest.lang, "zh")
        }
        else
        {
            weakHashMap.put(ConstantsRequest.lang, "en")
        }

        (RetrofitClient.getClient.attemptToLogin(weakHashMap).enqueue(object :
            Callback<LoginModel> {
            @RequiresApi(api = 16)
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                //DialogBox.closeDialogE()//21-04-2020
                DialogBox.closeLoginDialog()

                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(mContext, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(mContext, (response.body() as LoginModel).message)
                }
                else if(response.code() == 200)
                {
                    val intent = Intent(mContext, OtpLoginActivity::class.java)
                    intent.putExtra(ConstantsRequest.USERNAME, input_email_name)
                    intent.putExtra(ConstantsRequest.PASSWORD, input_password_login)
                    intent.putExtra(ConstantsRequest.MESSAGE, (response.body() as LoginModel).message)
                    intent.putExtra(ConstantsRequest.OTP_RECEIVER, (response.body() as LoginModel).otp_receiver)
                    intent.putExtra(ConstantsRequest.OTP_RECEIVER, (response.body() as LoginModel).otp_receiver)
                    mContext.startActivity(intent)
                }

            }

            override fun onFailure(call: Call<LoginModel>, th: Throwable) {
               // DialogBox.closeDialogE()//21-04-2020
                DialogBox.closeLoginDialog()
                Snackbar.make(linearLayout_login, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }



    private fun attemptToSignup(input_name: String, input_email: String, input_mobile: String, input_password: String, input_ccp: String) {

        //DialogBox.showLoader(mContext)

        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()

        weakHashMap.put(ConstantsRequest.USERNAME, input_name)
        weakHashMap.put(ConstantsRequest.PHONENUMBER, input_mobile)
        weakHashMap.put(ConstantsRequest.COUNTRYCODE, input_ccp)
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
                //DialogBox.closeDialogE()
                DialogBox.closeLoginDialog()
                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(mContext, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(mContext, (response.body() as SignupModel).message)
                }
                else if(response.code() == 200)
                {
                    val intent = Intent(mContext, OtpSignupActivity::class.java)
                    intent.putExtra(ConstantsRequest.ISVERIFYMOBILEOTP, "NO")
                    intent.putExtra(ConstantsRequest.USERNAME, input_name)
                    intent.putExtra(ConstantsRequest.PHONENUMBER, input_mobile)
                    intent.putExtra(ConstantsRequest.COUNTRYCODE, input_ccp)
                    intent.putExtra(ConstantsRequest.EMAIL, input_email)
                    intent.putExtra(ConstantsRequest.PASSWORD, input_password)
                    intent.putExtra(ConstantsRequest.AUTHINFO,(response.body() as SignupModel).authinfo)
                    intent.putExtra(
                        ConstantsRequest.TIMER_DURATION_SEC,
                        (response.body() as SignupModel).timer_duration_sec.toString()
                    )
                    mContext.startActivity(intent)
                }


            }

            override fun onFailure(call: Call<SignupModel>, th: Throwable) {
                //DialogBox.closeDialogE()
                DialogBox.closeLoginDialog()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }

}