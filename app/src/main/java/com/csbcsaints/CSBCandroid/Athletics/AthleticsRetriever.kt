package com.csbcsaints.CSBCandroid

import android.content.SharedPreferences
import com.csbcsaints.CSBCandroid.addHours
import com.csbcsaints.CSBCandroid.toDateWithTime
import com.google.gson.Gson
import eu.amirs.JSON
import okhttp3.*
import java.io.IOException
import java.util.*

//Asks for athletics data, first from user defaults. If it's old/nonexistent, it will get it from online, wait for it to parse, then return it to Athletics Activity
class AthleticsRetriever {
    fun retrieveAthleticsArray(preferences : SharedPreferences?, forceReturn : Boolean = false, forceRefresh : Boolean = false, completion : (Array<AthleticsModel?>) -> Unit) {
        if (forceRefresh) {
            println("Athletics Data is being refreshed")
            getAthleticsDataFromOnline(preferences, completion)
        } else if (forceReturn) {
            val json = preferences?.getString("athleticsArray", null)
            return if (json != null) {
                println("Force return found an old JSON value")
                completion(Gson().fromJson(json, Array<AthleticsModel?>::class.java))
            } else {
                println("Force return returned an empty array")
                completion(arrayOf())
            }
        } else {
            println("Attempting to retrieve stored Athletics data.")
            val currentTime = Calendar.getInstance().time
            val athleticsArrayTimeString : String? = preferences?.getString("athleticsArrayTime", null)
            val athleticsArrayTime = athleticsArrayTimeString?.toDateWithTime()?.addHours(1)
            val json = preferences?.getString("athleticsArray", null)

            if ((athleticsArrayTime != null && !json.isNullOrEmpty())) {
                if (athleticsArrayTime > currentTime) {
                    println("Up-to-date Athletics data found, no need to look online.")
                    return completion(Gson().fromJson(json, Array<AthleticsModel?>::class.java))
                } else {
                    println("Athletics data found, but is old. Will refresh online.")
                    getAthleticsDataFromOnline(preferences, completion)
                }
            } else {
                println("No Athletics data found. Looking online.")
                getAthleticsDataFromOnline(preferences, completion)
            }
        }
    }
    private fun getAthleticsDataFromOnline(preferences: SharedPreferences?, completion : (Array<AthleticsModel?>) -> Unit) {
        println("We are asking for Athletics data")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://www.schedulegalaxy.com/api/v1/schools/163/activities")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to ScheduleGalaxy: ")
                println(e)
                retrieveAthleticsArray(preferences, true, false, completion)
            }
            override fun onResponse(call: Call, response: Response) {
                println("success")
                val json = JSON(response.body?.string())
                AthleticsDataParser().parseAthleticsData(json, preferences)
                retrieveAthleticsArray(preferences, false, false, completion)
            }
        })
    }
}