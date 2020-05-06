package com.crypto.eltwallet.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.utilities.ConstantsRequest

class KycVerificationActivity : AppCompatActivity(), View.OnClickListener {

    var back : ImageView?=null
    var btn_start : TextView?=null
    var country_name : String=""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kyc_verification)
        supportActionBar?.hide()

        initXml()
        setOnClickListener()
    }

    private fun initXml()
    {
        back = findViewById(R.id.back)
        btn_start = findViewById(R.id.btn_start)

        country_name = intent.getStringExtra(ConstantsRequest.COUNTRY_NAME)
    }


    private fun setOnClickListener()
    {
        back!!.setOnClickListener(this)
        btn_start!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id)
        {
            R.id.back->
            {
                finish()
            }

            R.id.btn_start->
            {
                var intent = Intent(this@KycVerificationActivity,KycVerification2NameActivity::class.java)
                intent.putExtra(ConstantsRequest.COUNTRY_NAME,country_name)
                startActivity(intent)
            }


        }
    }

}
