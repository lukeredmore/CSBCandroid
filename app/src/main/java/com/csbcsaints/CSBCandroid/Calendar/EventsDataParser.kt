package com.csbcsaints.CSBCandroid

import android.os.AsyncTask
import com.csbcsaints.CSBCandroid.Calendar.EventsModel
import com.csbcsaints.CSBCandroid.CalendarActivity
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import com.csbcsaints.CSBCandroid.ui.abbrvMonthString
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException
import java.util.*

class EventsDataParser : CSBCAppCompatActivity() {//AsyncTask<Void, Void, Void>() {

    val titleList : MutableList<String> = arrayListOf()
    val timeList : MutableList<String> = arrayListOf()
    val dayList : MutableList<String> = arrayListOf()
    val monthList : MutableList<String> = arrayListOf()
    val schoolsList : MutableList<String> = arrayListOf()

    var eventsModelArray : Array<EventsModel?> = arrayOf()

    fun parseEventsData(html: String) {
        println(html.toUpperCase())
        if (html.contains("evcal_event_title")) {
            val doc = Jsoup.parse(html)
            doc.select(".evcal_event_title")
                .forEach {
                    println(it.text())
                    titleList.add(it.text())
                }
            doc.select(".evcal_time")
                .forEach {
                    println(it.text())
                    if (it.text().contains("(All Day: ")) {
                        timeList.add("All Day")
                    } else {
                        timeList.add(it.text())
                    }
                }
            doc.select(".date")
                .forEach {
                    println(it.text())
                    dayList.add(it.text())
                }
            doc.select(".month")
                .forEach {
                    println(it.text())
                    var dateString = it.text()
                    monthList.add(Calendar.getInstance().time.abbrvMonthString())
                }
            doc.select(".ett1")
                .forEach {
                    println(it.text())
                    val schools = it.text().replace("Schools:", "")
                    schoolsList.add(schools)
                }
            val month = Calendar.getInstance().time.abbrvMonthString().toUpperCase()
            var modelListToReturn : MutableList<EventsModel> = arrayListOf()
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
            eventsModelArray = arrayOf(EventsModel("There are no more events this month.", "", "", "", "", ""))
        }
        addObjectArrayToUserDefaults(eventsModelArray)




    }

}