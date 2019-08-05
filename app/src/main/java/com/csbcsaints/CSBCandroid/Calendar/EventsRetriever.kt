package com.csbcsaints.CSBCandroid

import android.content.SharedPreferences
import com.csbcsaints.CSBCandroid.ui.addHours
import com.csbcsaints.CSBCandroid.ui.toDateWithTime
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.util.*
import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter

class EventsRetriever {
    fun retrieveEventsArray(preferences : SharedPreferences?, forceReturn : Boolean = false, forceRefresh: Boolean = false, completion : (Array<EventsModel?>) -> Unit) {
        if (forceRefresh) {
            DeveloperPrinter().print("Events Data is being refreshed")
            getEventsDataFromOnline(preferences, completion)
        } else if (forceReturn) {
            val json = preferences?.getString("eventsArray", null)
            return if (json != null) {
                DeveloperPrinter().print("Force return found an old JSON value")
                completion(Gson().fromJson(json, Array<EventsModel?>::class.java))
            } else {
                DeveloperPrinter().print("Force return returned an empty array")
                completion(arrayOf())
            }
        } else {
            DeveloperPrinter().print("Attempting to retrieve stored Events data.")
            val currentTime = Calendar.getInstance().time
            val eventsArrayTimeString : String? = preferences?.getString("eventsArrayTime", null)
            val eventsArrayTime = eventsArrayTimeString?.toDateWithTime()?.addHours(1)
            val json = preferences?.getString("eventsArray", null)

            if (eventsArrayTime != null && !json.isNullOrEmpty()) {
                if (eventsArrayTime > currentTime) {
                    DeveloperPrinter().print("Up-to-date Events data found, no need to look online.")
                    return completion(Gson().fromJson(json, Array<EventsModel?>::class.java))
                } else {
                    DeveloperPrinter().print("Events data found, but is old. Will refresh online.")
                    getEventsDataFromOnline(preferences, completion)
                }
            } else {
                DeveloperPrinter().print("No Events data found. Looking online.")
                getEventsDataFromOnline(preferences, completion)
            }
        }
    }
    private fun getEventsDataFromOnline(preferences: SharedPreferences?, completion : (Array<EventsModel?>) -> Unit) {
        DeveloperPrinter().print("We are asking for Events data")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://csbcsaints.org/calendar/")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DeveloperPrinter().print("Error on request to CSBCSaints.org: ")
                println(e)
                retrieveEventsArray(preferences, true, false, completion)
            }
            override fun onResponse(call: Call, response: Response) {
                val html = response.body?.string()
                if (html != null) {
                    println("Successfully received calendar data")
                    EventsDataParser().parseEventsData(html, preferences)
                    retrieveEventsArray(preferences, false, false, completion)
                } else {
                    println("Received calendar data is null")
                    retrieveEventsArray(preferences, true, false, completion)
                }
            }
        })
    }

}