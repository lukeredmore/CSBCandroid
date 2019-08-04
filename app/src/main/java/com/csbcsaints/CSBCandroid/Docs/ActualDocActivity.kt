package com.csbcsaints.CSBCandroid

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter

//TODO - Pull document from bundle instead of internet, add share button

class ActualDocActivity : CSBCAppCompatActivity() {
    var webView : WebView? = null
    var loadingSymbol : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_viewer)
        DeveloperPrinter().print("WE HAVE ACHIEVED ENLIGHTENMENT")
        this.title = intent.getStringExtra("selectedTitle")

        webView = findViewById(R.id.webViewDocs)
        loadingSymbol = findViewById(R.id.loadingSymbolDocs)
        webView?.setWebViewClient(object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingSymbol?.visibility = View.VISIBLE
            }
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                loadingSymbol?.visibility = View.INVISIBLE
            }
        })


    }
    override fun onStart() {
        super.onStart()
        webView?.loadUrl("https://docs.google.com/gview?url=${intent.getStringExtra("selectedLink")}")

    }

}