package com.csbcsaints.CSBCandroid

import android.content.SharedPreferences
import com.csbcsaints.CSBCandroid.ui.abbrvMonthString
import com.csbcsaints.CSBCandroid.ui.dateStringWithTime
import com.google.gson.Gson
import org.jsoup.Jsoup
import java.util.*

class EventsDataParser {
    val titleList : MutableList<String> = arrayListOf()
    val timeList : MutableList<String> = arrayListOf()
    val dayList : MutableList<String> = arrayListOf()
    val monthList : MutableList<String> = arrayListOf()
    val schoolsList : MutableList<String> = arrayListOf()

    private var eventsModelArray : Array<EventsModel?> = arrayOf() //ONLY ACCESS FROM SHAREDPREFERENCES

    fun parseEventsData(html: String, preferences : SharedPreferences) {
        println("Events data is being parsed")
        if (html.contains("evcal_event_title")) {
            val doc = Jsoup.parse(html)
            doc.select(".evcal_event_title")
                .forEach {
                    titleList.add(it.text())
                }
            doc.select(".evcal_time")
                .forEach {
                    if (it.text().contains("(All Day: ")) {
                        timeList.add("All Day")
                    } else {
                        timeList.add(it.text())
                    }
                }
            doc.select(".date")
                .forEach {
                    dayList.add(it.text())
                }
            doc.select(".month")
                .forEach {
                    var dateString = it.text()
                    monthList.add(Calendar.getInstance().time.abbrvMonthString())
                }
            doc.select(".ett1")
                .forEach {
                    val schools = it.text().replace("Schools:", "")
                    schoolsList.add(schools)
                }
            val month = Calendar.getInstance().time.abbrvMonthString().toUpperCase()
            val modelListToReturn : MutableList<EventsModel> = arrayListOf()
            for (i in 0 until titleList.size) {
                val modelToAppend = EventsModel(
                    titleList.getOrNull(i) ?: "",
                    month + dayList.getOrNull(i) ?: "",
                    dayList.getOrNull(i) ?: "",
                    month,
                    timeList.getOrNull(i) ?: "",
                    schoolsList.getOrNull(i) ?: "")
                modelListToReturn.add(modelToAppend)
            }
            eventsModelArray = modelListToReturn.toTypedArray()
        } else {
            eventsModelArray = arrayOf()
        }
        addObjectArrayToUserDefaults(eventsModelArray, preferences)
    }
    private fun addObjectArrayToUserDefaults(eventsArray: Array<EventsModel?>, preferences : SharedPreferences) {
        val dateTimeToAdd = Calendar.getInstance().time.dateStringWithTime()
        val json : String = Gson().toJson(eventsArray)
        preferences.edit()?.putString("eventsArray", json)?.apply()
        preferences.edit()?.putString("eventsArrayTime", dateTimeToAdd)?.apply()
    }

}