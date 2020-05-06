package com.crypto.eltwallet.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.crypto.eltwallet.R

class ScannerActivity : AppCompatActivity() {

    lateinit var scannerView : CodeScannerView
    lateinit var mCodeScanner: CodeScanner

//    protected fun attachBaseContext(base: Context) {
//        super.attachBaseContext(LocaleHelper.onAttach(base))
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        supportActionBar?.hide()

        scannerView = findViewById(R.id.scanner_view)

        mCodeScanner = CodeScanner(this, scannerView)
        mCodeScanner.setDecodeCallback(DecodeCallback { result ->
            this@ScannerActivity.runOnUiThread(Runnable {
                Toast.makeText(this@ScannerActivity, result.text, Toast.LENGTH_SHORT).show()


                val returnIntent = Intent()
                returnIntent.putExtra("result", result.text)
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            })
        })
        scannerView.setOnClickListener(View.OnClickListener { mCodeScanner.startPreview() })
    }

    override fun onResume() {
        super.onResume()

        mCodeScanner.startPreview()
    }


    override fun onPause() {
        super.onPause()

        mCodeScanner.startPreview()
    }
}
