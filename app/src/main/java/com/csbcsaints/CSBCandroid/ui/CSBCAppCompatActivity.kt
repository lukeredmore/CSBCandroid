package com.csbcsaints.CSBCandroid.ui

import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.csbcsaints.CSBCandroid.NotificationController
import com.csbcsaints.CSBCandroid.R
import java.text.SimpleDateFormat

//TODO - Support dark mode, support hiding schools, store school selected in an object

abstract class CSBCAppCompatActivity : AppCompatActivity() {
    var schoolSelected = "Seton"
    var schoolSelectedInt = 0
    val schoolSelectedMap = mapOf<String, Int>("Seton" to 0, "St. John's" to 1, "All Saints" to 2, "St. James" to 3)
    val schoolSelectedArray = arrayOf<String>("Seton", "St. John's", "All Saints", "St. James")
    val dateStringFormatter = SimpleDateFormat("MM/dd/yyyy")


    //MARK - Activity control
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setTheme(R.style.AppTheme)
    }


    //MARK - Tab methods
    fun setupViewForTabbedActivity(layout: Int) {
        val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        getSupportActionBar()?.hide()
        findViewById<TextView>(R.id.activityTitle).setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)

        schoolSelected = sharedPreferences?.getString("schoolSelected", "Seton") ?: "Seton"
        schoolSelectedInt = schoolSelectedMap[schoolSelected]!!
        println("schoolSelected was found to be $schoolSelected")
        val tabLayout: TabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setScrollPosition(schoolSelectedInt, 0f, true)
        tabSelectedHandler()
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab) {
                schoolSelected = tab.text.toString()
                schoolSelectedInt = schoolSelectedMap[schoolSelected]!!
                sharedPreferences?.edit()?.putString("schoolSelected", schoolSelected)?.apply()
                println("schoolSelected was stored as $schoolSelected")
                tabSelectedHandler()
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) { }
            override fun onTabReselected(p0: TabLayout.Tab?) { }
        })
    }
    open fun tabSelectedHandler() {}
}