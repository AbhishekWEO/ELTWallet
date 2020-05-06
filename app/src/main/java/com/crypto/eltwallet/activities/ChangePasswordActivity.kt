package com.crypto.eltwallet.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.ChangePasswordModel
import com.crypto.eltwallet.network.RetrofitClient
import com.crypto.eltwallet.utilities.*
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern

class ChangePasswordActivity : AppCompatActivity(), View.OnClickListener {

    var img_back : ImageView?=null
    var rl_main : RelativeLayout?=null
    var edt_oldPwd : EditText?=null
    var eye1 : ImageView?=null
    var edt_newPwd : EditText?=null
    var eye2 : ImageView?=null
    var edt_cPwd : EditText?=null
    var eye3 : ImageView?=null
    var tv_msg : TextView?=null
    var txt_submit : TextView?=null
    var progressBarSubmit : ProgressBar?=null

    var passwordVisibility1 = false
    var passwordVisibility2 = false
    var passwordVisibility3 = false
    var sharedPreferenceUtil: SharedPreferenceUtil?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        supportActionBar!!.hide()
        sharedPreferenceUtil = SharedPreferenceUtil(this)
        initXml()
        setOnClickListener()
    }

    fun initXml()
    {
        rl_main = findViewById(R.id.rl_main)
        img_back = findViewById(R.id.img_back)
        edt_oldPwd = findViewById(R.id.edt_oldPwd)
        eye1 = findViewById(R.id.eye1)
        edt_newPwd = findViewById(R.id.edt_newPwd)
        eye2 = findViewById(R.id.eye2)
        edt_cPwd = findViewById(R.id.edt_cPwd)
        eye3 = findViewById(R.id.eye3)
        tv_msg = findViewById(R.id.tv_msg)
        txt_submit = findViewById(R.id.txt_submit)
        progressBarSubmit = findViewById(R.id.progressBarSubmit)
    }

    fun setOnClickListener()
    {
        img_back!!.setOnClickListener(this)
        eye1!!.setOnClickListener(this)
        eye2!!.setOnClickListener(this)
        eye3!!.setOnClickListener(this)
        txt_submit!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.img_back->
            {
                finish()
            }
            R.id.eye1->
            {
                manageVisibilityOfPassword1()
            }
            R.id.eye2->
            {
                manageVisibilityOfPassword2()
            }
            R.id.eye3->
            {
                manageVisibilityOfPassword3()
            }
            R.id.txt_submit->
            {
                if (progressBarSubmit!!.isVisible)
                { }
                else
                {
                    DialogBox.hideKeyboard(this@ChangePasswordActivity)
                    validations()
                }

            }
        }

    }

    fun validations()
    {
        if (edt_oldPwd!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!, R.string.enter_oldPwd, Snackbar.LENGTH_LONG).show()
        }
        /*else if (edt_oldPwd!!.text.toString().trim().length < 8)
        {
            Snackbar.make(rl_main!!, R.string.atleast_eight, Snackbar.LENGTH_LONG).show()
        }*/

        else if (edt_newPwd!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!, R.string.enter_newPwd, Snackbar.LENGTH_LONG).show()
        }
        else if (edt_newPwd!!.text.toString().trim().length < 8)
        {
            Snackbar.make(rl_main!!, R.string.atleastEight, Snackbar.LENGTH_LONG).show()
        }
        else if (!validate(edt_newPwd!!.text.toString().trim()))
        {
            Snackbar.make(rl_main!!, R.string.enter_well_pattern, Snackbar.LENGTH_LONG).show()
        }
        else if (edt_cPwd!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!, R.string.enter_cPwd, Snackbar.LENGTH_LONG).show()
        }
        /*else if (edt_cPwd!!.text.toString().trim().length < 8)
        {
            Snackbar.make(rl_main!!, R.string.atleast_eight, Snackbar.LENGTH_LONG).show()
        }*/

        else if (edt_newPwd!!.text.toString().trim() != edt_cPwd!!.text.toString().trim())
        {
            Snackbar.make(rl_main!!, R.string.make_sure_you_enter_same_password, Snackbar.LENGTH_LONG).show()
        }
        else
        {
            if (UtilityPermissions.isNetworkAvailable(this))
            {
                img_back!!.isEnabled=false
                //progressBarSubmit!!.visibility=View.VISIBLE
                //txt_submit!!.setText("")

                changePwd()
            }
            else
            {
                Snackbar.make(rl_main!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    fun validate(password: String): Boolean
    {
        //var pattern: Pattern
        //var matcher: Matcher

        var pattern = Pattern.compile(Constants.PASSWORD_PATREN)
        var matcher = pattern.matcher(password)
        return matcher.matches()

    }

    private fun manageVisibilityOfPassword1()
    {
        if (passwordVisibility1)
        {
            passwordVisibility1 = false
            edt_oldPwd!!.transformationMethod = PasswordTransformationMethod()
            edt_oldPwd!!.setSelection(edt_oldPwd!!.getText().length)
            return
        }
        passwordVisibility1 = true
        edt_oldPwd!!.transformationMethod = HideReturnsTransformationMethod()
        edt_oldPwd!!.setSelection(edt_oldPwd!!.getText().length)
    }

    fun manageVisibilityOfPassword2()
    {
        if (passwordVisibility2)
        {
            passwordVisibility2 = false
            edt_newPwd!!.transformationMethod = PasswordTransformationMethod()
            edt_newPwd!!.setSelection(edt_newPwd!!.getText().length)
            return
        }
        passwordVisibility2 = true
        edt_newPwd!!.transformationMethod = HideReturnsTransformationMethod()
        edt_newPwd!!.setSelection(edt_newPwd!!.getText().length)
    }

    fun manageVisibilityOfPassword3()
    {
        if (passwordVisibility3)
        {
            passwordVisibility3 = false
            edt_cPwd!!.transformationMethod = PasswordTransformationMethod()
            edt_cPwd!!.setSelection(edt_cPwd!!.getText().length)
            return
        }
        passwordVisibility3 = true
        edt_cPwd!!.transformationMethod = HideReturnsTransformationMethod()
        edt_cPwd!!.setSelection(edt_cPwd!!.getText().length)
    }

    fun changePwd()
    {
        DialogBox.showLoader(this)
        var weakHashMap: WeakHashMap<String, String> = WeakHashMap<String, String>()
        weakHashMap.put(ConstantsRequest.OLD_PWD, edt_oldPwd!!.text.toString().trim())
        weakHashMap.put(ConstantsRequest.NEW_PWD, edt_newPwd!!.text.toString().trim())

        RetrofitClient.getClient.attemptToChangePassword(sharedPreferenceUtil!!.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()
            , weakHashMap)
            .enqueue(object:
                Callback<ChangePasswordModel>
            {
                override
                fun onResponse(call: Call<ChangePasswordModel>, response: Response<ChangePasswordModel>)
                {
                    DialogBox.closeDialogE()

                    if (response == null)
                    {
                        Snackbar.make(rl_main!!, R.string.response_null, Snackbar.LENGTH_LONG).show()
                    }
                    else if(response.code()==200)
                    {
                        if (response.body()!!.getStatuscode()!!.equals("200"))
                        {
                            edt_oldPwd!!.setText("")
                            edt_newPwd!!.setText("")
                            edt_cPwd!!.setText("")
                            sharedPreferenceUtil!!.setString(SharedPreferencesConstants.USER_TOKEN,response.body()!!.getUsertoken()!!)
                            sharedPreferenceUtil!!.save()
                            Snackbar.make(rl_main!!, resources.getString(R.string.password_changed_successfully), Snackbar.LENGTH_LONG).show()//response.body()!!.getMessage()!!
                        }
                        else
                        {
                            Snackbar.make(rl_main!!, response.body()!!.getMessage()!!, Snackbar.LENGTH_LONG).show()
                        }
                    }
                    else if (response.code() == 401)
                    {
                        DialogBox.showError(this@ChangePasswordActivity, (response.errorBody()!!.string()).toString())
                    }
                    else if (response.code() == 202)
                    {
                        DialogBox.showError(this@ChangePasswordActivity, (response.errorBody()!!.string()).toString())
                    }

                    img_back!!.isEnabled=true
                   // progressBarSubmit!!.visibility=View.GONE
                    //txt_submit!!.setText(resources.getString(R.string.submit))

                }
                override fun onFailure(call: Call<ChangePasswordModel>, t:Throwable)
                {
                    t.printStackTrace()
                    DialogBox.closeDialogE()
                    Snackbar.make(rl_main!!, t.message!!, Snackbar.LENGTH_LONG).show()
                    img_back!!.isEnabled=true
                    //progressBarSubmit!!.visibility=View.GONE
                    //txt_submit!!.setText(resources.getString(R.string.submit))
                }
            })
    }
}
