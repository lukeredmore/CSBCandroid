package com.csbcsaints.CSBCandroid.Covid

import android.app.ActionBar
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.csbcsaints.CSBCandroid.*

class CovidCheckInActivity : CSBCAppCompatActivity() {

    private var familyQuestionnaireButton : View? = null
    private var familyQuestionnaireWebView: WebView? = null
    private var familyLoadingSymbol: View? = null
    private var familyQuestionnaireText: TextView? = null

    private val code = StaticData.readData("general/familyCovidCheckInCode") ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_covid_check_in)

        val inflator = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customActionBar = inflator.inflate(R.layout.action_bar_with_loading_symbol, null)
        val titleTextView = customActionBar?.findViewById<TextView>(R.id.textView)
        titleTextView?.setCustomFont(
            UserFontFamilies.GOTHAM,
            UserFontStyles.SEMIBOLD
        )
        titleTextView?.text = "COVID-19 Check-In"

       customActionBar?.findViewById<View>(R.id.progressBar)?.visibility = View.INVISIBLE

        supportActionBar?.customView = customActionBar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

        class CovidWebViewClient : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (url?.contains("confirmation") == true) {
                    familyQuestionnaireWebView?.visibility = View.INVISIBLE
                    findViewById<View>(R.id.contentView)?.visibility = View.INVISIBLE
                    finish()
                    writeToScreen("Check-In completed! If any of your answers change throughout the day/week, please complete this form again.")
                } else if (url?.contains("/form/$code") == true) {
                    familyLoadingSymbol?.visibility = View.INVISIBLE
                    familyQuestionnaireText?.visibility = View.VISIBLE
                }
            }
        }

        familyQuestionnaireButton = findViewById(R.id.familyQuestionnaireButton)
        familyQuestionnaireButton?.setOnClickListener {
            if (familyLoadingSymbol?.visibility == View.INVISIBLE) {
                familyQuestionnaireWebView?.visibility = View.VISIBLE
            }
        }

        familyQuestionnaireWebView = findViewById(R.id.familyQuestionnaireWebView)
        familyQuestionnaireWebView?.webViewClient = CovidWebViewClient()
        familyQuestionnaireWebView?.isHorizontalScrollBarEnabled = false

        familyLoadingSymbol = findViewById(R.id.familyLoadingSymbol)
        familyQuestionnaireText = findViewById(R.id.familyQuestionnaireText)



    }

    override fun onStart() {
        super.onStart()

        familyQuestionnaireText?.visibility = View.INVISIBLE
        familyLoadingSymbol?.visibility = View.VISIBLE
        familyQuestionnaireWebView?.visibility = View.INVISIBLE
        familyQuestionnaireWebView?.loadUrl("https://app.mobilecause.com/form/$code")


    }

    companion object {
        val showCovidCheckIn : Boolean
            get()
        {
            return if (BuildConfig.BUILD_TYPE == "debug")
                true
            else
                StaticData.readData("general/showCovidCheckIn")?.toBoolean() ?: false

        }
    }
}