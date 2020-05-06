package com.crypto.eltwallet.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.utilities.DialogBoxWalletSuccess
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.lang.Exception

class CreateWalletSuccessActivity : AppCompatActivity() {

    lateinit var input_address: String
    lateinit var input_privateKey: String

    lateinit var address : TextView
    lateinit var privateKey : TextView
    lateinit var qr_image : ImageView
    lateinit var back : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet_success)

        supportActionBar?.hide()

        address = findViewById(R.id.address)
        privateKey = findViewById(R.id.privateKey)
        qr_image = findViewById(R.id.qr_image)
        back = findViewById(R.id.back)
        back.setOnClickListener{
            DialogBoxWalletSuccess.showWarning(this)
        }

        val intent = intent
        if (intent != null) {
            input_address = intent.getStringExtra("address")
            input_privateKey = intent.getStringExtra("privateKey")
        }

        address.text = input_address
        privateKey.text = input_privateKey

        generateQrCode()

    }

    private fun generateQrCode() {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix =
                multiFormatWriter.encode(input_privateKey, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qr_image.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        DialogBoxWalletSuccess.showWarning(this)
    }
}
