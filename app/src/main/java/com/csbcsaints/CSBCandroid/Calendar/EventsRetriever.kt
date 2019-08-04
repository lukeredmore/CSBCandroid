package com.csbcsaints.CSBCandroid

import android.content.SharedPreferences
import com.csbcsaints.CSBCandroid.ui.addHours
import com.csbcsaints.CSBCandroid.ui.toDateWithTime
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.util.*

class EventsRetriever {
    fun retrieveEventsArray(preferences : SharedPreferences, forceReturn : Boolean = false, forceRefresh: Boolean = false, callback : (Array<EventsModel?>) -> Unit) {
        println("Attempting to retrieve stored Events data.")
        val currentTime = Calendar.getInstance().time
        val eventsArrayTimeString : String? = preferences.getString("eventsArrayTime", null)
        val eventsArrayTime = eventsArrayTimeString?.toDateWithTime()?.addHours(1)
        val json = preferences.getString("eventsArray", null)
        if (forceRefresh) {
            println("Events Data is being refreshed")
            getEventsDataFromOnline(preferences, callback)
        }
        if (eventsArrayTime != null && !json.isNullOrEmpty()) {
            if ((eventsArrayTime > currentTime) || (forceReturn)) {
                println("Up-to-date Events data found, no need to look online.")
                return callback(Gson().fromJson(json, Array<EventsModel?>::class.java))
            } else if (forceReturn) {
                println("Could not retrieve events data from online")
                return callback(arrayOf())
            } else {
                println("Events data found, but is old. Will refresh online.")
                getEventsDataFromOnline(preferences, callback)
            }
        } else {
            println("No Events data found. Looking online.")
            getEventsDataFromOnline(preferences, callback)
        }
    }
    private fun getEventsDataFromOnline(preferences: SharedPreferences, callback : (Array<EventsModel?>) -> Unit) {
        println("We are asking for Events data")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://csbcsaints.org/calendar/")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to CSBCSaints.org: ")
                println(e)
                retrieveEventsArray(preferences, true, false, callback)
            }
            override fun onResponse(call: Call, response: Response) {
                val html = response.body?.string()
                if (html != null) {
                    println("Successfully received calendar data")
                    EventsDataParser().parseEventsData(html, preferences)
                    retrieveEventsArray(preferences, false, false, callback)
                } else {
                    println("Received calendar data is null")
                    retrieveEventsArray(preferences, true, false, callback)
                }
            }
        })
    }

}