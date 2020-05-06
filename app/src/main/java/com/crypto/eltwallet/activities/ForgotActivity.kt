package com.crypto.eltwallet.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.ForgetPasswordModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ForgotActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout : RelativeLayout
    lateinit var back : ImageView

    lateinit var edt_email : EditText
    lateinit var img_forget : ImageView
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)

        supportActionBar?.hide()

        sharedPreferenceUtil = SharedPreferenceUtil(this)

        linearLayout = findViewById(R.id.linearLayout)
        back = findViewById(R.id.back)
        back.setOnClickListener(){
            onBackPressed()
        }

        edt_email = findViewById(R.id.edt_email)
        img_forget = findViewById(R.id.img_forget)

        img_forget.setOnClickListener(){

            val input_email = edt_email.text
            val email = input_email.toString()

            if (email.trim { it <= ' ' } == "") {

                Snackbar.make(linearLayout, R.string.enter_your_email, Snackbar.LENGTH_LONG).show()

            }
            else if (!email.trim { it <= ' ' }.matches(Constants.EMAIL_PATREN.toRegex())) {

                Snackbar.make(linearLayout, R.string.enter_your_correct_email, Snackbar.LENGTH_LONG).show()

            }
            else
            {
                requestForForgotPassword(input_email.toString())
            }

        }

    }


    fun requestForForgotPassword(input_email: String) {
        DialogBox.showLoader(this)
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
        weakHashMap.put(ConstantsRequest.EMAIL, input_email)

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

        (RetrofitClient.getClient.attemptForgotPassword(weakHashMap).enqueue(object :
            Callback<ForgetPasswordModel> {
            @RequiresApi(api = 16)
            override fun onResponse(
                call: Call<ForgetPasswordModel>,
                response: Response<ForgetPasswordModel>
            ) {
                DialogBox.closeDialogE()
                if(response == null)
                {
                    Snackbar.make(linearLayout, R.string.response_null, Snackbar.LENGTH_LONG).show()
                }
                else if(response.code() == 401)
                {
                    DialogBox.showError(this@ForgotActivity, (response.errorBody()!!.string()).toString())
                }
                else if(response.code() == 202)
                {
                    DialogBox.showError(this@ForgotActivity, (response.body() as ForgetPasswordModel).message)
                }
                else if(response.code() == 200)
                {
                    DialogBox.showForgot(this@ForgotActivity, (response.body() as ForgetPasswordModel).message)
                }

            }

            override fun onFailure(call: Call<ForgetPasswordModel>, th: Throwable) {
                DialogBox.closeDialogE()
                Snackbar.make(linearLayout, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()

            }
        }))
    }
}
