package com.csbcsaints.CSBCandroid.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.TableLayout
import android.widget.TextView
import com.csbcsaints.CSBCandroid.AthleticsModel
import com.csbcsaints.CSBCandroid.Calendar.EventsModel
import com.csbcsaints.CSBCandroid.R
import com.google.gson.Gson
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

//TODO - Support dark mode, support hiding schools, store school selected in an object

abstract class CSBCAppCompatActivity : AppCompatActivity() {
    var schoolSelected = "Seton"
    var schoolSelectedInt = 0
    val schoolSelectedMap = mapOf<String, Int>("Seton" to 0, "St. John's" to 1, "All Saints" to 2, "St. James" to 3)
    val schoolSelectedArray = arrayOf<String>("Seton", "St. John's", "All Saints", "St. James")
    val dateStringFormatter = SimpleDateFormat("MM/dd/yyyy")
    var testSharedPrefs : SharedPreferences? = null

//    constructor() {
//        testSharedPrefs = getSharedPreferences("UserDefaults", Context.MODE_WORLD_READABLE)
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
//        testSharedPrefs =

    }

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

    open fun tabSelectedHandler() {
        print("try this tho")
        println(Calendar.getInstance().time.dateString())
    }

    fun retrieveEventsArrayFromUserDefaults(preferences : SharedPreferences, forceReturn : Boolean = false) : Array<EventsModel?> {
        println("Attempting to retrieve stored Events data.")
        val currentTime = Calendar.getInstance().time
        val eventsArrayTimeString : String? = preferences.getString("athleticsArrayTime", null)
        val eventsArrayTime = eventsArrayTimeString?.toDateWithTime()?.addHours(1)
        val json = preferences.getString("eventsArray", null)
        if (eventsArrayTime != null && !json.isNullOrEmpty()) {
            if ((eventsArrayTime > currentTime) || (forceReturn)) {
                println("Up-to-date Events data found, no need to look online.")
                return Gson().fromJson(json, Array<EventsModel?>::class.java)
            } else {
                println("Events data found, but is old. Will refresh online.")
                return arrayOf(EventsModel("Could not connect.", "", "", "", "", ""))
            }
        } else {
            println("No Events data found. Looking online.")
            return arrayOf()
        }
    }
    fun retrieveAthleticsArrayFromUserDefaults(preferences : SharedPreferences, forceReturn : Boolean = false) : Array<AthleticsModel?> {
        println("Attempting to retrieve stored Athletics data.")
        val currentTime = Calendar.getInstance().time
        val athleticsArrayTimeString : String? = preferences.getString("athleticsArrayTime", null)
        val athleticsArrayTime = athleticsArrayTimeString?.toDateWithTime()?.addHours(1)
        val json = preferences.getString("athleticsArray", null)
        if (athleticsArrayTime != null && !json.isNullOrEmpty()) {
            if ((athleticsArrayTime > currentTime) || (forceReturn)) {
                println("Up-to-date Athletics data found, no need to look online.")
                return Gson().fromJson(json, Array<AthleticsModel?>::class.java)
            } else {
                println("Athletics data found, but is old. Will refresh online.")
                return arrayOf()
            }
        } else {
            println("No Athletics data found. Looking online.")
            return arrayOf()
        }
    }

}