package com.csbcsaints.CSBCandroid.Lunch

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class WebViewDelegate : WebViewClient {

    var webView : WebView? = null

    constructor(webView: WebView, loadingSymbol : ProgressBar) {
        this.webView = webView
        webView.setWebViewClient(object:WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingSymbol.visibility = View.VISIBLE
                println("WEBVIEW started LAODING")
            }

            override fun onPageCommitVisible(view: WebView?, url: String?) {
                super.onPageCommitVisible(view, url)
                loadingSymbol.visibility = View.INVISIBLE
                println("WEBVIEW FINISHED LAODINGdafd")
            }

            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                println("WEBVIEW FINISHED LAODING")
                loadingSymbol.visibility = View.INVISIBLE
            }
        })
    }
}