package com.csbcsaints.CSBCandroid.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.csbcsaints.CSBCandroid.NotificationController
import com.csbcsaints.CSBCandroid.R
import com.google.android.material.appbar.AppBarLayout

//TODO - Support dark mode, store school selected in an object

abstract class CSBCAppCompatActivity : AppCompatActivity() {
    var schoolSelected = "Seton"
    var schoolSelectedInt = 0
    val schoolSelectedMap = mapOf("Seton" to 0, "St. John's" to 1, "All Saints" to 2, "St. James" to 3)
    val schoolSelectedArray = arrayOf("Seton", "St. John's", "All Saints", "St. James")


    //MARK - Activity control
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        setTheme(R.style.AppTheme)

    }


    //MARK - Tab methods
    @SuppressLint("ResourceAsColor")
    fun setupViewForTabbedActivity(layout: Int) {
        val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        supportActionBar?.hide()
        findViewById<TextView>(R.id.activityTitle).setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)

        val tabLayout : TabLayout = findViewById(R.id.tabLayout)
        val fullParams = AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.WRAP_CONTENT)
        val collapsedParams = AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, 0)
        val barParams = AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, 8.toPx())
        val collapsedBarParams = AppBarLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, 0)

        val showAllSchools = sharedPreferences?.getBoolean("showAllSchools", false) ?: false
        println("showAllSchools was found in as $showAllSchools")
        val usersSchools =
            if (showAllSchools) { arrayOf(true, true, true, true) }
            else { NotificationController(this).notificationSettings.schools }
        println("print as [${usersSchools[0]}, ${usersSchools[1]}, ${usersSchools[2]}, ${usersSchools[3]}]")

        schoolSelected = sharedPreferences?.getString("schoolSelected", "Seton") ?: "Seton"
        schoolSelectedInt = schoolSelectedMap[schoolSelected] ?: 0
        println("schoolSelected was found to be $schoolSelected")

        tabLayout.removeAllTabs()
        for (i in 0 until usersSchools.size) {
            if (usersSchools[i]) {
                val tabToAdd = tabLayout.newTab()
                tabToAdd.text = schoolSelectedArray[i]
                print("adding tab with ${tabToAdd.text}")
                tabLayout.addTab(tabToAdd, schoolSelectedArray[i] == schoolSelected)
            }
        }





        tabLayout.setScrollPosition(tabLayout.selectedTabPosition, 0f, true)
        tabSelectedHandler()
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab) {
                schoolSelected = tab.text.toString()
                schoolSelectedInt = schoolSelectedMap[schoolSelected] ?: 0
                sharedPreferences?.edit()?.putString("schoolSelected", schoolSelected)?.apply()
                println("schoolSelected was stored as $schoolSelected")
                tabSelectedHandler()
            }
            override fun onTabUnselected(p0: TabLayout.Tab?) { }
            override fun onTabReselected(p0: TabLayout.Tab?) { }
        })

        if (tabLayout.tabCount < 2) {
            tabLayout.layoutParams = collapsedParams
            findViewById<View>(R.id.bar).layoutParams = barParams
        }
        else {
            tabLayout.layoutParams = fullParams
            findViewById<View>(R.id.bar).layoutParams = collapsedBarParams
        }
    }
    open fun tabSelectedHandler() {}
}