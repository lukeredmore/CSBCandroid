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

open class CSBCAppCompatActivity : AppCompatActivity() {
    var schoolSelected = "Seton"
    var schoolSelectedInt = 0
    val schoolSelectedMap = mapOf<String, Int>("Seton" to 0, "St. John's" to 1, "All Saints" to 2, "St. James" to 3)
    val schoolSelectedArray = arrayOf<String>("Seton", "St. John's", "All Saints", "St. James")
    val dateStringFormatter = SimpleDateFormat("MM/dd/yyyy")
    var testSharedPrefs : SharedPreferences? = null



    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        testSharedPrefs = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

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

    fun addObjectArrayToUserDefaults(eventsArray: Array<EventsModel?>) {

        //val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        val dateTimeToAdd = Calendar.getInstance().time.dateStringWithTime()
        val json = Gson().toJson(eventsArray)
        testSharedPrefs?.edit()?.putString("eventsArray", json)?.apply()
        testSharedPrefs?.edit()?.putString("eventsArrayTime", dateTimeToAdd)?.apply()
    }
    fun addObjectArrayToUserDefaults(athleticsArray: Array<AthleticsModel?>) {
        //val sharedPreferences2 = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        val dateTimeToAdd = Calendar.getInstance().time.dateStringWithTime()
        val json = Gson().toJson(athleticsArray)
        print(dateTimeToAdd)
        println(json)
        testSharedPrefs?.edit()?.putString("athleticsArray", json)?.apply()
        testSharedPrefs?.edit()?.putString("athleticsArrayTime", dateTimeToAdd)?.apply()
    }
    fun retrieveEventsArrayFromUserDefaults(forceReturn : Boolean = false) : Array<EventsModel?> {
        //val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        println("retireving evnts defaults")
        val currentTime = Calendar.getInstance().time
        val eventsArrayTimeString : String? = testSharedPrefs?.getString("eventsArrayTime", null)
        val eventsArrayTime = eventsArrayTimeString?.toDateWithTime()?.addHours(1)
        val json = testSharedPrefs?.getString("eventsArray", null)
        if (eventsArrayTime != null && !json.isNullOrEmpty()) {
            if ((eventsArrayTime!! > currentTime) || (forceReturn)) {
                return Gson().fromJson(json, Array<EventsModel?>::class.java)
            } else {
                return arrayOf()
            }
        } else if (forceReturn) {
            return arrayOf(EventsModel("Could not connect.", "", "", "", "", ""))
        } else {
            return arrayOf()
        }

    }
    fun retrieveAthleticsArrayFromUserDefaults(forceReturn : Boolean = false) : Array<AthleticsModel?> {

        //val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        println("retireving athletics defaults")
        val currentTime = Calendar.getInstance().time
        val athleticsArrayTimeString : String? = testSharedPrefs?.getString("athleticsArrayTime", null)
        val athleticsArrayTime = athleticsArrayTimeString?.toDateWithTime()?.addHours(1)
        val json = testSharedPrefs?.getString("athleticsArray", null)
        println(json)
        println(athleticsArrayTime)
        if (athleticsArrayTime != null && !json.isNullOrEmpty()) {
            print("1 ")
            if ((athleticsArrayTime!! > currentTime) || (forceReturn)) {
                print("2 ")
                return Gson().fromJson(json, Array<AthleticsModel?>::class.java)
            } else {
                print("3 ")
                return arrayOf()
            }
        } else {
            print("4 ")
            return arrayOf()
        }
    }

}