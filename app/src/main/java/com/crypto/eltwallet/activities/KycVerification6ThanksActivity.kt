package com.crypto.eltwallet.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.crypto.eltwallet.R

class KycVerification6ThanksActivity : AppCompatActivity(), View.OnClickListener {

    var btn_finished : TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kyc_verification6_thanks)

        supportActionBar?.hide()

        initXml()
        setOnClickListener()
    }

    private fun initXml()
    {
        btn_finished = findViewById(R.id.btn_finished)

    }
    private fun setOnClickListener()
    {
        btn_finished!!.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.btn_finished->
            {
                var intent = Intent(this,SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }
}