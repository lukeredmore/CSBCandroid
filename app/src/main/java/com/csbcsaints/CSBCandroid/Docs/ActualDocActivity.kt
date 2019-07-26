package com.csbcsaints.CSBCandroid.Docs

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.webkit.WebView
import android.widget.ProgressBar
import com.csbcsaints.CSBCandroid.Lunch.WebViewDelegate
import com.csbcsaints.CSBCandroid.R
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import com.google.gson.Gson

//TODO - Pull document from bundle instead of internet, add share button

class ActualDocActivity : CSBCAppCompatActivity() {

    var webView : WebView? = null
    var loadingSymbol : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_viewer)

        webView = findViewById(R.id.webView)
        loadingSymbol = findViewById(R.id.loadingSymbol)
        val webClient = WebViewDelegate(webView!!, loadingSymbol!!)
        webView?.setWebViewClient(webClient)

    }

    override fun onStart() {
        super.onStart()
        webView?.loadUrl(intent.getStringExtra("selectedLink"))
    }

}