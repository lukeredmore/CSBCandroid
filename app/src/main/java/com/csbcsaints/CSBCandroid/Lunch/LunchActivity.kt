package com.csbcsaints.CSBCandroid

import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.csbcsaints.CSBCandroid.Calendar.EventsModel
import com.csbcsaints.CSBCandroid.Lunch.WebViewDelegate
import com.csbcsaints.CSBCandroid.ui.*
import com.google.gson.Gson
import java.util.*

//TODO - remove existing test stuff, load downloaded file locations on system from UserDefaults, display either webview or pdfview, add share button display date on bottom tab, enable rotation

class LunchActivity : CSBCAppCompatActivity() { //Fragment() {

    var webView : WebView? = null
    var loadingSymbol : ProgressBar? = null
    var lunchURLs : Array<String> = arrayOf("https://csbcsaints.org/wp-content/uploads/SETON-MENU-June-2019.pdf", "http://doclibrary.com/MSC20/DOC/Elem_LunchJune4354.pdf", "https://csbcsaints.org/wp-content/uploads/JUNE-LunchMenu6-2019-1.doc", "https://csbcsaints.org/wp-content/uploads/June-Lunch-2019.docx")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lunch)

        webView = findViewById(R.id.webView)
        loadingSymbol = findViewById(R.id.loadingSymbol)
        val webClient = WebViewDelegate(webView!!, loadingSymbol!!)
        val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        lunchURLs = Gson().fromJson(sharedPreferences.getString("lunchURLs", Gson().toJson(lunchURLs)), Array<String>::class.java)

        setupViewForTabbedActivity(R.layout.activity_lunch)
    }

    override fun tabSelectedHandler() {
        webView?.loadUrl("https://docs.google.com/gview?url=${lunchURLs[schoolSelectedInt]}")
    }




}
