package com.csbcsaints.CSBCandroid

import android.content.Context
import android.os.Bundle
import android.webkit.WebView
import android.widget.TextView
import com.csbcsaints.CSBCandroid.CSBCAppCompatActivity
import com.csbcsaints.CSBCandroid.UserFontFamilies
import com.csbcsaints.CSBCandroid.UserFontStyles
import com.csbcsaints.CSBCandroid.setCustomFont
import com.google.android.material.tabs.TabLayout
import android.webkit.WebChromeClient

class DressCodeActivity : CSBCAppCompatActivity() {
    var dressCodeSelectedInt = 0
    var dressCodeSelected = "Elementary"
    val dressCodeTitles = arrayOf("Elementary", "Middle School", "High School")
    val dressCodeHTMLs = arrayOf("elementarySchoolDress", "middleSchoolDress", "highSchoolDress")
    val dressCodeMap = mapOf("Elementary" to 0, "Middle School" to 1, "High School" to 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dresscode)
        setupDressCodeView()
    }
    override fun tabSelectedHandler() {
        println(dressCodeTitles[dressCodeSelectedInt])
        val webView : WebView? = findViewById(R.id.webView)
        webView?.settings?.loadWithOverviewMode = true
        webView?.settings?.useWideViewPort = true
        webView?.webChromeClient = WebChromeClient()
        webView?.loadUrl("file:///android_asset/${dressCodeHTMLs[dressCodeSelectedInt]}.html")
    }

    private fun setupDressCodeView() {
        val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        supportActionBar?.hide()
        findViewById<TextView>(R.id.activityTitle).setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)

        dressCodeSelected = sharedPreferences?.getString("dressCodeSelected", "Elementary") ?: "Elementary"
        dressCodeSelectedInt = dressCodeMap[dressCodeSelected]!!
        println("schoolSelected was found to be $dressCodeSelected")
        val tabLayout: TabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setScrollPosition(dressCodeSelectedInt, 0f, true)
        tabSelectedHandler()
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab) {
                dressCodeSelected = tab.text.toString()
                dressCodeSelectedInt = dressCodeMap[dressCodeSelected]!!
                sharedPreferences?.edit()?.putString("dressCodeSelected", dressCodeSelected)?.apply()
                println("dressCodeSelected was stored as $dressCodeSelected")
                tabSelectedHandler()
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) { }
            override fun onTabReselected(p0: TabLayout.Tab?) { }
        })
    }


}
