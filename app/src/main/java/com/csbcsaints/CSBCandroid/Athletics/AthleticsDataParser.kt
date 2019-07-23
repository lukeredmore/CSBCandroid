package com.csbcsaints.CSBCandroid

import androidx.appcompat.app.AppCompatActivity
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import eu.amirs.JSON
import java.text.SimpleDateFormat

class AthleticsDataParser() : CSBCAppCompatActivity() {

    var athleticsModelArray : Array<AthleticsModel?> = arrayOf()
    val teamAbbreviations = mapOf("V" to "Varsity","JV" to "JV","7/8TH" to "Modified")
    val months = mapOf("Jan" to "01", "Feb" to "02", "Mar" to "03", "Apr" to "04", "May" to "05", "Jun" to "06", "Jul" to "07", "Aug" to "08", "Sep" to "09", "Oct" to "10", "Nov" to "11", "Dec" to "12")

    fun parseAthleticsData(json : JSON) {
        var modelListToReturn : MutableList<AthleticsModel> = arrayListOf()
        var dateToBeat = json.key("data").index(0).key("date").stringValue()
        var currentDate = dateToBeat
        var dateString : String = ""
        var n = 0
        while (n < json.key("data").count()) {
            var homeGameList : MutableList<String> = arrayListOf()
            var genderList : MutableList<String> = arrayListOf()
            var levelList : MutableList<String> = arrayListOf()
            var sportList : MutableList<String> = arrayListOf()
            var opponentList : MutableList<String> = arrayListOf()
            var timeList : MutableList<String> = arrayListOf()
            while (currentDate == dateToBeat && n < json.key("data").count()) {
                val title = json.key("data").index(n).key("title").stringValue()
                var titleArray = title.split(" ").toMutableList()
//                    if titleArray[0] == "POSTPONED:" {
////                    titleArray.remove(at: 0)
////                    }
                titleArray.removeAt(titleArray.count() - 1)
                titleArray.removeAt(titleArray.count() - 1)
                if (titleArray[0].contains("(")) { //if each data is formatted correctly
                    if (titleArray[3] == "@") {
                        homeGameList.add("@")
                    } else {
                        homeGameList.add("vs.")
                    }
                    if (titleArray[0] == "(G)") {
                        genderList.add("Girl")
                    } else {
                        genderList.add("Boy")
                    }
                    levelList.add(teamAbbreviations[titleArray[1]]!!)
                    var sport = titleArray[2]
                    if (sport == "Outdoor") {
                        sport = "Track & Field"
                    }
                    sportList.add(sport)
                    if (titleArray.count() == 8) {
                        opponentList.add(titleArray[4] + titleArray[5])
                    } else if (titleArray.count() == 9) {
                        opponentList.add(titleArray[4] + titleArray[5] + titleArray[6])
                    } else {
                        opponentList.add(titleArray[4])
                    }

                    timeList.add(json.key("data").index(n).key("start_time").stringValue())
                    if (n < json.key("data").count()-1) {
                        currentDate = json.key("data").index(n + 1).key("date").stringValue()
                    } else {
                        currentDate = "null"
                    }
                    dateString = json.key("data").index(n).key("date").stringValue()
                    dateString = dateString.replace(",", "")
                    var dateArray = dateString.split(" ").toMutableList()
                    dateArray[0] = months[dateArray[0]]!!
                    val dateParser = SimpleDateFormat("MM dd yyyy")
                    val dateFormatter = SimpleDateFormat("EEEE, MMMM d")
                    val newDate = dateParser.parse(dateArray[0] + " " + dateArray[1] + " " + dateArray[2])
                    dateString = dateFormatter.format(newDate)

                } else {
                    println("Error in parsing '$title'")
                }
                n++

            }
            dateToBeat = currentDate
            val modelToAppend = AthleticsModel(homeGameList, genderList, levelList, sportList, opponentList, timeList, dateString)
            modelListToReturn.add(modelToAppend)

        }
        println("athleticsModelArray created, adding to user defaults")
        athleticsModelArray = modelListToReturn.toTypedArray()
        addObjectArrayToUserDefaults(athleticsModelArray)

    }

}