package com.crypto.eltwallet.test

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.crypto.eltwallet.R


class TestKotlinActivity : AppCompatActivity() {

    lateinit var walletName : EditText
    lateinit var walletAddress : EditText
    lateinit var input_walletName : String
    lateinit var input_walletAddress : String
    lateinit var submit : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_kotlin)

        walletName = findViewById(R.id.walletName)
        submit.setOnClickListener {

            input_walletName = walletName.text.toString()
            input_walletAddress = walletAddress.text.toString()

            if(input_walletName.equals("")) {
                Toast.makeText(this, "Please enter wallet name", Toast.LENGTH_SHORT).show()
            }
            else if(input_walletAddress.equals("")) {
                Toast.makeText(this, "Please enter wallet address", Toast.LENGTH_SHORT).show()
            }
            else if(input_walletAddress.equals("")) {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            }
            else if(input_walletAddress.equals("")) {
                Toast.makeText(this, "Enter Email in correct format", Toast.LENGTH_SHORT).show()
            }
            else if(input_walletName.equals("")) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, input_walletName, Toast.LENGTH_SHORT).show()
            }
        }

        //
        //

        }

    ///

    //
}