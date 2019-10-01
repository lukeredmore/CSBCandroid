package com.csbcsaints.CSBCandroid

import android.content.SharedPreferences
import com.csbcsaints.CSBCandroid.ui.dateStringWithTime
import com.google.gson.Gson
import java.util.*

class EventsDataParser {
    fun parseJSON(json : Array<Map<String,String>>, preferences : SharedPreferences?) : Set<EventsModel> {
        var eventsModelSet : Set<EventsModel> = mutableSetOf()
        for (event in json) {
            val title = event["title"] ?: continue
            val date = event["date"] ?: continue
            val dateInts = date.split("-").map { it.toInt() }.toTypedArray()
            val cal = Calendar.getInstance()
            cal.set(dateInts[0], dateInts[1] - 1, dateInts[2])
            val eventToInsert = EventsModel(
                title,
                cal,
                if (event["time"] == "" || event["time"] == null) null else event["time"],
                if (event["schools"] == "" || event["schools"] == null) null else event["schools"]
            )
            println(eventToInsert)
            if (!eventsModelSet.contains(eventToInsert)) { eventsModelSet = eventsModelSet.plus(eventToInsert) }

        }
        addObjectArrayToUserDefaults(eventsModelSet, preferences)
        return eventsModelSet
    }
    private fun addObjectArrayToUserDefaults(eventsSet: Set<EventsModel>, preferences : SharedPreferences?) {
        val dateTimeToAdd = Calendar.getInstance().time.dateStringWithTime()
        val json : String = Gson().toJson(eventsSet)
        if (preferences != null) {
            preferences.edit()?.putString("eventsSet", json)?.apply()
            preferences.edit()?.putString("eventsArrayTime", dateTimeToAdd)?.apply()
            println("Events data successfully added to user defaults")
        } else println("Preferences are null, so events data that was parsed wasn't saved")
    }

}