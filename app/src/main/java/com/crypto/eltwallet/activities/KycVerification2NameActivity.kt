package com.crypto.eltwallet.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.utilities.ConstantsRequest
import com.crypto.eltwallet.utilities.SharedPreferenceUtil
import com.crypto.eltwallet.utilities.SharedPreferencesConstants
import com.google.android.material.snackbar.Snackbar

class KycVerification2NameActivity : AppCompatActivity(), View.OnClickListener {

    var rl_main : RelativeLayout?=null
    var back : ImageView?=null
    var edt_legalName : EditText?=null
    var btn_next : TextView?=null
    lateinit var sharedPreferenceUtil: SharedPreferenceUtil
    var user_token : String?=null
    var legal_name : String?=null
    var country_name : String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kyc_verification2_name)
        supportActionBar?.hide()

        sharedPreferenceUtil = SharedPreferenceUtil(this)
        user_token = sharedPreferenceUtil.getString(SharedPreferencesConstants.USER_TOKEN, "").toString()
        legal_name = sharedPreferenceUtil.getString(SharedPreferencesConstants.LEGAL_NAME, "").toString()

        initXml()
        setOnClickListener()
    }

    private fun initXml()
    {
        back = findViewById(R.id.back)
        rl_main = findViewById(R.id.rl_main)
        edt_legalName = findViewById(R.id.edt_legalName)
        btn_next = findViewById(R.id.btn_next)
        getSetData()
    }
    private fun getSetData()
    {
        edt_legalName!!.setText(legal_name)
        edt_legalName!!.setSelection(edt_legalName!!.length())

        country_name = intent.getStringExtra(ConstantsRequest.COUNTRY_NAME)
    }
    private fun setOnClickListener()
    {
        back!!.setOnClickListener(this)
        btn_next!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id)
        {
            R.id.back->
            {
                finish()
            }

            R.id.btn_next->
            {
                validations()
            }

        }
    }

    private fun validations()
    {
        if (edt_legalName!!.text.toString().trim().isEmpty())
        {
            Snackbar.make(rl_main!!,resources.getString(R.string.please_enter_your_legal_name),Snackbar.LENGTH_LONG).show()
        }
        else
        {
            var intent = Intent(this@KycVerification2NameActivity,KycVerification3IdActivity::class.java)
            intent.putExtra(ConstantsRequest.LEGAL_NAME,edt_legalName!!.text.toString().trim())
            intent.putExtra(ConstantsRequest.COUNTRY_NAME,country_name)
            startActivity(intent)
        }
    }

}
