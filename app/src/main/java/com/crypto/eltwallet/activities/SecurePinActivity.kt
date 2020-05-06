package com.crypto.eltwallet.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.crypto.eltwallet.R
import com.crypto.eltwallet.utilities.DialogBox
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.crypto.eltwallet.utilities.SharedPreferencesConstants
import com.crypto.eltwallet.utilities.UtilityPermissions
import com.google.android.material.snackbar.Snackbar

class SecurePinActivity : AppCompatActivity(), View.OnClickListener {

    var rl_main : RelativeLayout?=null
    var img_back : ImageView?=null
    var tv_title : TextView?=null
    var rl_currentPin : RelativeLayout?=null
    var edt_currentPin : EditText?=null
    var edt_createPin : EditText?=null
    var edt_confirmPin : EditText?=null
    var txt_save : TextView?=null
    var progressBarSave : ProgressBar?=null

    var sharedPreferenceUtil: SharedPreferenceUtil?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secure_pin)
        supportActionBar!!.hide()
        sharedPreferenceUtil = SharedPreferenceUtil(this)
        initXml()
        setOnClickListener()
    }

    private fun initXml()
    {
        rl_main = findViewById(R.id.rl_main)
        img_back = findViewById(R.id.img_back)
        tv_title = findViewById(R.id.tv_title)

        rl_currentPin = findViewById(R.id.rl_currentPin)
        edt_currentPin = findViewById(R.id.edt_currentPin)
        edt_createPin = findViewById(R.id.edt_createPin)
        edt_confirmPin = findViewById(R.id.edt_confirmPin)
        txt_save = findViewById(R.id.txt_save)
        progressBarSave = findViewById(R.id.progressBarSave)

        getSetData()

    }
    private fun getSetData()
    {
        if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.IS_CREATE_SECURE_PIN,"").toString().equals("true"))
        {
            tv_title!!.setText(resources.getString(R.string.change_secure_pin))
            rl_currentPin!!.visibility=View.VISIBLE
            edt_createPin!!.setHint(resources.getString(R.string.enter_new_secure_pin))
            edt_confirmPin!!.setHint(resources.getString(R.string.confirm_new_secure_pin))
        }
        else
        {
            tv_title!!.setText(resources.getString(R.string.create_secure_pin))
            rl_currentPin!!.visibility=View.GONE
            edt_createPin!!.setHint(resources.getString(R.string.enter_secure_pin))
            edt_confirmPin!!.setHint(resources.getString(R.string.confirm_secure_pin))
        }
    }
    private fun setOnClickListener()
    {
        img_back!!.setOnClickListener(this)
        txt_save!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.img_back->
            {
                finish()
            }

            R.id.txt_save->
            {
                DialogBox.hideKeyboard(this@SecurePinActivity)
                if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.IS_CREATE_SECURE_PIN,"").toString().equals("true"))
                {
                    validations2()
                }
                else
                {
                    validations()
                }

            }
        }

    }

    private fun validations()
    {
        if (edt_createPin!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!, R.string.enter_secure_pin, Snackbar.LENGTH_LONG).show()
        }
        else if (edt_createPin!!.text.toString().trim().length < 4)
        {
            Snackbar.make(rl_main!!, R.string.secure_pin_length, Snackbar.LENGTH_LONG).show()
        }
        //
        else if (edt_confirmPin!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!, R.string.confirm_secure_pin, Snackbar.LENGTH_LONG).show()
        }

        else if (edt_createPin!!.text.toString().trim() != edt_confirmPin!!.text.toString().trim())
        {
            Snackbar.make(rl_main!!, R.string.make_sure_you_enter_same_pin, Snackbar.LENGTH_LONG).show()
        }
        else
        {
            /*if (UtilityPermissions.isNetworkAvailable(this))
            {*/
            sharedPreferenceUtil!!.setString(SharedPreferencesConstants.SECURE_PIN,edt_createPin!!.text.toString().trim())
            sharedPreferenceUtil!!.save()
            sharedPreferenceUtil!!.setString(SharedPreferencesConstants.IS_CREATE_SECURE_PIN,"true")
            sharedPreferenceUtil!!.save()
            finish()
            /*}
            else
            {
                Snackbar.make(rl_main!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }*/
        }
    }

    private fun validations2()
    {
        if (edt_currentPin!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!, R.string.enter_current_secure_pin, Snackbar.LENGTH_LONG).show()
        }
        else if (edt_createPin!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!, R.string.enter_new_secure_pin, Snackbar.LENGTH_LONG).show()
        }
        else if (edt_createPin!!.text.toString().trim().length < 4)
        {
            Snackbar.make(rl_main!!, R.string.secure_pin_length, Snackbar.LENGTH_LONG).show()
        }
        //
        else if (edt_confirmPin!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!, R.string.confirm_new_secure_pin, Snackbar.LENGTH_LONG).show()
        }

        else if (edt_createPin!!.text.toString().trim() != edt_confirmPin!!.text.toString().trim())
        {
            Snackbar.make(rl_main!!, R.string.make_sure_you_enter_same_pin, Snackbar.LENGTH_LONG).show()
        }
        else if (edt_createPin!!.text.toString().trim().equals(edt_currentPin!!.text.toString().trim()))
        {
            Snackbar.make(rl_main!!, resources.getString(R.string.secure_pin_msg), Snackbar.LENGTH_LONG).show()
        }
        else
        {
            if (UtilityPermissions.isNetworkAvailable(this))
            {
                if (sharedPreferenceUtil!!.getString(SharedPreferencesConstants.SECURE_PIN,"").equals(edt_currentPin!!.text.toString().trim()))
                {
                    sharedPreferenceUtil!!.setString(SharedPreferencesConstants.SECURE_PIN,edt_createPin!!.text.toString().trim())
                    sharedPreferenceUtil!!.save()
                    sharedPreferenceUtil!!.setString(SharedPreferencesConstants.IS_CREATE_SECURE_PIN,"true")
                    sharedPreferenceUtil!!.save()
                    finish()
                }

            }
            else
            {
                Snackbar.make(rl_main!!, resources.getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            }
        }
    }
}
