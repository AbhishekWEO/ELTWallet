package com.crypto.eltwallet.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.utilities.Conversions

class TransactionDetailsActivity : AppCompatActivity() {

    private val TAG = this::class.java.simpleName
    lateinit var linearLayout: LinearLayout

    lateinit var blockHash : TextView
    lateinit var blockNumber : TextView
    lateinit var timestamp : TextView
    lateinit var from : TextView
    lateinit var to: TextView
    lateinit var value : TextView
    lateinit var c : TextView
    lateinit var gas : TextView
    lateinit var nonce : TextView
    lateinit var input : TextView

    lateinit var input_blockHash : String
    lateinit var input_blockNumber : String
    lateinit var input_timestamp : String
    lateinit var input_from : String
    lateinit var input_to : String
    lateinit var input_value : String
    lateinit var input_c : String
    lateinit var input_gas : String
    lateinit var input_nonce : String
    lateinit var input_input : String

    lateinit var back : ImageView

    companion object
    {
        var isBackFromTransdetails = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)

        supportActionBar?.hide()

        back = findViewById(R.id.back)
        back.setOnClickListener{
            isBackFromTransdetails = true
            finish()
        }

        linearLayout = findViewById(R.id.linearLayout)

        blockHash = findViewById(R.id.blockHash)
        blockNumber = findViewById(R.id.blockNumber)
        timestamp = findViewById(R.id.timestamp)
        from = findViewById(R.id.from)
        to = findViewById(R.id.to)
        value = findViewById(R.id.value)
        c = findViewById(R.id.c)
        gas = findViewById(R.id.gas)
        nonce = findViewById(R.id.nonce)
        input = findViewById(R.id.input)


        val intent = intent
        if (intent != null) {
            input_blockHash = intent.getStringExtra("blockHash")
            input_blockNumber = intent.getStringExtra("blockNumber")
            input_timestamp = intent.getStringExtra("timestamp")
            input_from = intent.getStringExtra("from")
            input_to = intent.getStringExtra("to")
            input_value = intent.getStringExtra("value")
            input_c = intent.getStringExtra("c")
            input_gas = intent.getStringExtra("gas")
            input_nonce = intent.getStringExtra("nonce")
            input_input = intent.getStringExtra("input")
        }

        blockHash.setText(input_blockHash)
        blockNumber.setText(input_blockNumber)
        timestamp.setText(Conversions.getTimeFormat(input_timestamp))
        from.setText(input_from)
        to.setText(input_to)
        value.setText(input_value)
        c.setText(input_c.substring(input_c.indexOf("[")+1, input_c.indexOf("]")))
        gas.setText(input_gas)
        nonce.setText(input_nonce)
        input.setText(input_input)

    }
}
