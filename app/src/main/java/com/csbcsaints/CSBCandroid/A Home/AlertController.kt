package com.csbcsaints.CSBCandroid

import android.content.Context
import com.csbcsaints.CSBCandroid.MainActivity
import com.csbcsaints.CSBCandroid.ui.dateString
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/// Checks for snow days and other critical alerts, tells the main screen and updates Firebase
class AlertController(val parent : MainActivity) {
    var closedData : MutableList<String> = arrayListOf()
    var shouldSnowDatesReinit = false
    var shouldOverridesReinit = false
    var snowDatesChecked = false
    var dayOverridesChecked = false
    var client = OkHttpClient()

    init {
        parent.removeBannerAlert()
        getSnowDatesAndOverridesAndQueueNotifications()
    }

    fun getSnowDatesAndOverridesAndQueueNotifications() {
        //var snowDays : Set<String> = setOf()
        FirebaseDatabase.getInstance().reference.child("SnowDays")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val preferences = parent.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

                    val myMap: Map<String, String>? = p0.value as? Map<String, String>
                    val myList: Collection<String>? = myMap?.values
                    val newSnowDays = myList?.toSet()
                    val ogSnowDays = preferences.getStringSet("snowDays", null)
                    println("Existing Snow Days: $ogSnowDays")
                    println("Firebase Snow Days: $newSnowDays")
                    if (newSnowDays != null) {
                        if (newSnowDays.contains(Calendar.getInstance().time.dateString())) {
                            parent.showBannerAlert("The Catholic Schools of Broome County are closed today")
                        } else {
                            parent.removeBannerAlert()
                        }
                        if (ogSnowDays != null) {
                            if (newSnowDays != ogSnowDays) {
                                println("Saving Firebase snow days and reinitializing")
                                preferences.edit().putStringSet("snowDays", newSnowDays).apply()
                                shouldSnowDatesReinit = true
                                tryToReinit()
                            } else {
                                println("They are equal, no need to reinit")
                            }
                        } else {
                            println("Saving Firebase snow days and reinitializing")
                            preferences.edit().putStringSet("snowDays", newSnowDays).apply()
                            shouldSnowDatesReinit = true
                            tryToReinit()
                        }
                        snowDatesChecked = true
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError)
                }

            })
        FirebaseDatabase.getInstance().reference.child("DayScheduleOverrides")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val preferences = parent.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
                    val newOverrides: Map<String, Int>? = p0.value as? Map<String, Int>
                    val ogOverrides = mapOf(
                        "Seton" to preferences.getInt("SetonOverrides", 0),
                        "John" to preferences.getInt("JohnOverrides", 0),
                        "Saints" to preferences.getInt("SaintsOverrides", 0),
                        "James" to preferences.getInt("JamesOverrides", 0)
                    )
                    if (!newOverrides.isNullOrEmpty() && !ogOverrides.isNullOrEmpty()) {
                        if (newOverrides != ogOverrides) {
                            println("Saving Firebase overrides and reinitializing")
                            preferences.edit().putInt("SetonOverrides", newOverrides["Seton"]!!).apply()
                            preferences.edit().putInt("JohnOverrides", newOverrides["John"]!!).apply()
                            preferences.edit().putInt("SaintsOverrides", newOverrides["Saints"]!!).apply()
                            preferences.edit().putInt("JamesOverrides", newOverrides["James"]!!).apply()
                            shouldOverridesReinit = true
                            tryToReinit()
                        } else {
                            println("They are equal, no need to reinit")
                        }
                    } else if (newOverrides != null) {
                        println("Saving Firebase overrides and reinitializing")
                        preferences.edit().putInt("SetonOverrides", newOverrides["Seton"]!!).apply()
                        preferences.edit().putInt("JohnOverrides", newOverrides["John"]!!).apply()
                        preferences.edit().putInt("SaintsOverrides", newOverrides["Saints"]!!).apply()
                        preferences.edit().putInt("JamesOverrides", newOverrides["James"]!!).apply()
                        shouldOverridesReinit = true
                        tryToReinit()
                    }
                    dayOverridesChecked = true
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError)
                }

            })
    }
    fun tryToReinit() {
        if (snowDatesChecked && dayOverridesChecked && (shouldSnowDatesReinit || shouldOverridesReinit)) {
            parent.reinitNotifications()
        }
    }

    fun checkForAlert() {
        println("Checking for alert from CSBC site")
        val request = Request.Builder()
            .url("https://csbcsaints.org/")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to CSBCSaints.org: ")
                println(e)
                checkForAlertFromWBNG()
            }
            override fun onResponse(call: Call, response: Response) {
                println("Successfully received CSBC homepage")
                val html = response.body?.string() ?: ""
                if (html.contains("strong")) {
                    val closedData : MutableList<String> = arrayListOf()
                    val doc = Jsoup.parse(html)
                    doc.select("strong")
                        .forEach {
                            closedData.add(it.text())
                        }
                    if (closedData[0] != "") {
                        print("An alert was found")
                        parent.showBannerAlert(closedData[0])
                        if (closedData[0].toLowerCase().contains("closed")) {
                            print("Today is a snow day")
                            addSnowDateToDatabase(Calendar.getInstance().time)
                        }
                    } else {
                        checkForAlertFromWBNG()
                    }
                } else {
                    checkForAlertFromWBNG()
                }
            }
        })
    }
    fun checkForAlertFromWBNG() {
        println("Checking for alert from WBNG")
        val request = Request.Builder()
            .url("http://ftpcontent6.worldnow.com/wbng/newsticker/closings.html")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to WBNG Closings: ")
                println(e)
            }

            override fun onResponse(call: Call, response: Response) {
                println("Successfully received WBNG closing data")
                val html = response.body?.string() ?: ""
                if (html.toLowerCase().contains("catholic")) {
                    var indexToSelect : Int? = null
                    val doc = Jsoup.parse(html)
                    doc.select("font")
                        .forEach {
                            if (indexToSelect == it.elementSiblingIndex()) {
                                parseStatus(it.text())
                                return
                            }
                            val value = it.text()
                            if (value.toLowerCase().contains("Catholic") && value.toLowerCase().contains("Broome")) {
                                indexToSelect = it.elementSiblingIndex() + 1
                            }
                        }
                } else println("No messages found from WBNG")
            }
        })
    }
    fun parseStatus(status : String) {
        if (status.toLowerCase().contains("closed")) {
            println("Snow day today was found")
            parent.showBannerAlert("The Catholic Schools of Broome County are closed today.")
            addSnowDateToDatabase(Calendar.getInstance().time)
        }
    }
    fun addSnowDateToDatabase(date : Date) { //Fix
        val dateValueFormatter = SimpleDateFormat("MM/dd/yyyy")
        val dateValueString = dateValueFormatter.format(date)
        val dateKeyFormatter = SimpleDateFormat("MMddyyyy")
        val dateKeyString = dateKeyFormatter.format(date)
        val dateToAddDict : Map<String, String> = mapOf(dateKeyString to dateValueString)
        val preferences = parent.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        val currentSnowDays = preferences.getStringSet("snowDays", null)
        if (currentSnowDays != null) {
            if (!currentSnowDays.contains(dateValueString)) {
                println("Adding snow day on $dateValueString to database")
                FirebaseDatabase.getInstance().reference.child("SnowDays").updateChildren(dateToAddDict) {
                        databaseError, databaseReference ->
                    println("Shit completed")
                    if (databaseError != null) {
                        println("Error adding snow day to database: " + databaseError)

                    } else {
                        println("Snow day successfully added")
                        getSnowDatesAndOverridesAndQueueNotifications()
                    }
                }
            } else {
                println("Snow day on $dateValueString already exists in database")
            }
        }

    }
}