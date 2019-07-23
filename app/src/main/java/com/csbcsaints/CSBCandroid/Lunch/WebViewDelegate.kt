package com.csbcsaints.CSBCandroid.Lunch

import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class WebViewDelegate : WebViewClient {

    var webView : WebView? = null

    constructor(webView: WebView, loadingSymbol : ProgressBar) {
        this.webView = webView
        webView.setWebViewClient(object:WebViewClient() {
            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                loadingSymbol.visibility = View.VISIBLE
            }

            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                loadingSymbol.visibility = View.INVISIBLE
            }
        })
    }
}