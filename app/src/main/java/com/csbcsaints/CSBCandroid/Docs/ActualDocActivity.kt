package com.csbcsaints.CSBCandroid

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.csbcsaints.CSBCandroid.CSBCAppCompatActivity

//TODO - Pull document from bundle instead of internet, add share button

class ActualDocActivity : CSBCAppCompatActivity() {
    var webView : WebView? = null
    var loadingSymbol : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_viewer)
        println("WE HAVE ACHIEVED ENLIGHTENMENT")
        this.title = intent.getStringExtra("selectedTitle")

        loadingSymbol = findViewById(R.id.loadingSymbolDocs)

        webView = findViewById(R.id.webViewDocs)
        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.builtInZoomControls = true
        webView?.settings?.displayZoomControls = false
        webView?.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                webView?.visibility = View.INVISIBLE
                loadingSymbol?.visibility = View.VISIBLE
            }

            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                webView?.visibility = View.VISIBLE
                loadingSymbol?.visibility = View.INVISIBLE
            }
        }


    }
    override fun onStart() {
        super.onStart()
        webView?.loadUrl("https://docs.google.com/gview?url=${intent.getStringExtra("selectedLink")}")
    }

}