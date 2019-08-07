package com.csbcsaints.CSBCandroid

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import com.csbcsaints.CSBCandroid.ui.*
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

//TODO - display as pdf if possible, display either webview or pdfview, add share button, enable rotation

class LunchActivity : CSBCAppCompatActivity() { //Fragment() {

    var webView : WebView? = null
    var loadingSymbol : ProgressBar? = null
    var lunchURLs : Array<String> = arrayOf("https://csbcsaints.org/wp-content/uploads/SETON-MENU-June-2019.pdf", "http://doclibrary.com/MSC20/DOC/Elem_LunchJune4354.pdf", "https://csbcsaints.org/wp-content/uploads/JUNE-LunchMenu6-2019-1.doc", "https://csbcsaints.org/wp-content/uploads/June-Lunch-2019.docx")
    var dateLabel : TextView? = null
    val dateLabelFormatter = SimpleDateFormat("EEEE, MMMM d")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch)

        dateLabel = findViewById(R.id.dateLabel)
        webView = findViewById(R.id.webView)
        loadingSymbol = findViewById(R.id.loadingSymbol)

        dateLabel?.text = dateLabelFormatter.format(Calendar.getInstance().time)
        dateLabel?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)

        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.builtInZoomControls = true
        webView?.settings?.displayZoomControls = false
        webView?.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingSymbol?.visibility = View.VISIBLE
            }

            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                loadingSymbol?.visibility = View.INVISIBLE
            }
        }

        val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        lunchURLs = Gson().fromJson(sharedPreferences.getString("lunchURLs", Gson().toJson(lunchURLs)), Array<String>::class.java)

        setupViewForTabbedActivity(R.layout.activity_lunch)
    }
    override fun tabSelectedHandler() {
        webView?.loadUrl("https://docs.google.com/gview?url=${lunchURLs[schoolSelectedInt]}")
    }
}
