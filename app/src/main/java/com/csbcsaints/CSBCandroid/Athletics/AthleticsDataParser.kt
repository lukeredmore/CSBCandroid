package com.csbcsaints.CSBCandroid

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import com.csbcsaints.CSBCandroid.ui.dateStringWithTime
import com.csbcsaints.CSBCandroid.ui.printAll
import com.google.gson.Gson
import eu.amirs.JSON
import java.text.SimpleDateFormat
import java.util.*

class AthleticsDataParser {
    var athleticsModelArray : Array<AthleticsModel?> = arrayOf()
    private val teamAbbreviations = mapOf("V" to "Varsity","JV" to "JV","7/8TH" to "Modified")

    fun parseAthleticsData(json : JSON, preferences : SharedPreferences) {
        var modelListToReturn : MutableList<AthleticsModel> = arrayListOf()
        var dateToBeat = json.key("data").index(0).key("date").stringValue()
        var currentDate = dateToBeat
        var dateString : String = ""
        var n = 0
        while (n < json.key("data").count()) {
            val titleList : MutableList<String> = arrayListOf()
            val levelList : MutableList<String> = arrayListOf()
            val timeList : MutableList<String> = arrayListOf()
            while (currentDate == dateToBeat && n < json.key("data").count()) {
                val titleArray = json.key("data").index(n).key("title").stringValue().split(" ").toMutableList()
//                if titleArray[0] == "POSTPONED:" {
//                    titleArray.remove(at: 0)
//                }
                titleArray.removeAt(titleArray.count() - 1)
                titleArray.removeAt(titleArray.count() - 1)
                if (titleArray[0].contains("(")) { //if each data is formatted correctly
                    var gender : String
                    var sport : String
                    var homeGame : String
                    var opponent : String

                    if (titleArray[0] == "(G)") {
                        gender = "Girl"
                    } else {
                        gender = "Boy"
                    }

                    sport = titleArray[2]
                    if (sport == "Outdoor") {
                        sport = "Track & Field"
                    }

                    if (titleArray[3] == "@") {
                        homeGame = "@"
                    } else {
                        homeGame = "vs."
                    }

                    if (titleArray.count() == 8) {
                        opponent = titleArray[4] + titleArray[5]
                    } else if (titleArray.count() == 9) {
                        opponent = titleArray[4] + titleArray[5] + titleArray[6]
                    } else {
                        opponent = titleArray[4]
                    }

                    titleList.add(gender + "'s " + sport + " " + homeGame + " " + opponent)
                    levelList.add(teamAbbreviations[titleArray[1]]!!)
                    timeList.add(json.key("data").index(n).key("start_time").stringValue())

                    val jsonDateFormatter = SimpleDateFormat("MMM d, yyyy")
                    val modelDateFormatter = SimpleDateFormat("EEEE, MMMM d")
                    dateString = modelDateFormatter.format(jsonDateFormatter.parse(json.key("data").index(n).key("date").stringValue()))

                    if (n < json.key("data").count()-1) {
                        currentDate = json.key("data").index(n + 1).key("date").stringValue()
                    } else {
                        currentDate = "null"
                    }
                } else {
                    print("Error in parsing '")
                    titleArray.printAll()
                    println("'")
                }
                n++

            }
            dateToBeat = currentDate
            val modelToAppend = AthleticsModel(titleList, levelList, timeList, dateString)
            modelListToReturn.add(modelToAppend)

        }
        println("athleticsModelArray created, adding to user defaults")
        athleticsModelArray = modelListToReturn.toTypedArray()
        addObjectArrayToUserDefaults(athleticsModelArray, preferences)
    }
    private fun addObjectArrayToUserDefaults(athleticsArray: Array<AthleticsModel?>, preferences : SharedPreferences) {
        val dateTimeToAdd = Calendar.getInstance().time.dateStringWithTime()
        val json : String = Gson().toJson(athleticsArray)
        println("dateTimeToAdd: " + dateTimeToAdd)
        println("json: " + json)
        preferences.edit()?.putString("athleticsArray", json)?.apply()
        preferences.edit()?.putString("athleticsArrayTime", dateTimeToAdd)?.apply()
    }
}