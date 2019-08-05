package com.csbcsaints.CSBCandroid

import android.content.SharedPreferences
import com.csbcsaints.CSBCandroid.ui.dateStringWithTime
import com.google.gson.Gson
import org.jsoup.Jsoup
import java.util.*
import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter

class EventsDataParser {
    private var eventsModelArray : Array<EventsModel?> = arrayOf() //ONLY ACCESS FROM SHAREDPREFERENCES

    fun parseEventsData(html: String, preferences : SharedPreferences?) {
        DeveloperPrinter().print("Events data is being parsed")
        if (html.contains("evcal_event_title")) {
            val modelListToReturn : MutableList<EventsModel> = arrayListOf()
            Jsoup.parse(html).select(".desc_trig_outter")
                .forEach {
                    val dictToAppend = mutableMapOf<String, String>()
                    it.select(".evcal_event_title")
                        .forEach {
                            dictToAppend["event"] = it.text()
                        }
                    it.select(".evcal_time")
                        .forEach {
                            if (it.text().toLowerCase().contains("all day")) {
                                dictToAppend["time"] = "All Day"
                            } else {
                                dictToAppend["time"] = it.text()
                            }
                        }
                    it.select(".evo_start")
                        .forEach {
                            it.select(".date")
                                .forEach {
                                    dictToAppend["day"] = it.text()
                                }
                            it.select(".month")
                                .forEach {
                                    dictToAppend["month"] = it.text().toUpperCase()
                                }
                        }
                    it.select(".evcal_desc3 .ett1")
                        .forEach {
                            dictToAppend["schools"] = it.text().replace("Schools:","", true)
                        }
                    val modelToAppend = EventsModel(
                        dictToAppend["event"] ?: "",
                        "${dictToAppend["month"] ?: ""}${dictToAppend["day"] ?: ""}",
                        dictToAppend["day"] ?: "",
                        dictToAppend["month"] ?: "",
                        dictToAppend["time"] ?: "",
                        dictToAppend["schools"] ?: "")
                    if (!modelListToReturn.contains(modelToAppend)) {
                        modelListToReturn.add(modelToAppend)
                    }
                }
            eventsModelArray = modelListToReturn.sortedWith(compareBy { it.day }).toTypedArray()
        } else {
            eventsModelArray = arrayOf()
        }
        addObjectArrayToUserDefaults(eventsModelArray, preferences)
    }
    private fun addObjectArrayToUserDefaults(eventsArray: Array<EventsModel?>, preferences : SharedPreferences?) {
        val dateTimeToAdd = Calendar.getInstance().time.dateStringWithTime()
        val json : String = Gson().toJson(eventsArray)
        if (preferences != null) {
            preferences.edit()?.putString("eventsArray", json)?.apply()
            preferences.edit()?.putString("eventsArrayTime", dateTimeToAdd)?.apply()
            DeveloperPrinter().print("Events data successfully added to user defaults")
        } else DeveloperPrinter().print("Preferences are null, so events data that was parsed wasn't saved")
    }

}