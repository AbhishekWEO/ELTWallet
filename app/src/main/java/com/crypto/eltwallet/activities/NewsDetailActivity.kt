package com.crypto.eltwallet.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.utilities.ConstantsRequest

class NewsDetailActivity : AppCompatActivity(), View.OnClickListener {

    private var img_close : ImageView?=null
    private var webview : WebView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)
        supportActionBar!!.hide()
        initXml()
        setOnClickListener()
    }

    private fun initXml()
    {
        img_close = findViewById(R.id.img_close)
        webview = findViewById(R.id.webview)

        getSetData()
    }
    private fun getSetData()
    {
        var url = intent.getStringExtra(ConstantsRequest.NEWS_URL)
        webview!!.loadUrl(url)

    }

    private fun setOnClickListener()
    {
        img_close!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.img_close->
            {
                finish()
            }
        }
    }
}
