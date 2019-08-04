package com.csbcsaints.CSBCandroid

import com.csbcsaints.CSBCandroid.ui.addDays
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.SharedPreferences
import com.csbcsaints.CSBCandroid.ui.printAll


class DaySchedule(context : Context, forSeton : Boolean = false, forJohn : Boolean = false, forSaints : Boolean = false, forJames : Boolean = false) {
    val startDateString : String = "09/04/2019" //first day of school
    val endDateString : String = "06/19/2020" //last day of school
    var dateDayDict : MutableMap<String,MutableMap<String,Int>> = mutableMapOf("Seton" to mutableMapOf(), "St. John's" to mutableMapOf(), "All Saints" to mutableMapOf(), "St. James" to mutableMapOf())
    var dateDayDictArray : MutableList<String> = arrayListOf()

    val noSchoolDateStrings : Array<String> = arrayOf("10/05/2019", "10/08/2019", "11/12/2019", "11/21/2019", "11/22/2019", "11/23/2019", "12/24/2019", "12/25/2019", "12/26/2019", "12/27/2019", "12/28/2019", "12/31/2019", "01/01/2020", "02/15/2020", "02/18/2020", "03/14/2020", "03/15/2020", "03/18/2020", "04/15/2020", "04/16/2020", "04/17/2020", "04/18/2020", "04/19/2020", "04/22/2020", "05/23/2020", "05/24/2020", "05/27/2020")
    val noElementarySchoolDateStrings : Array<String> = arrayOf("11/15/2019")
    val noHighSchoolDateStrings : Array<String> = arrayOf("01/21/2020", "01/22/2020", "01/23/2020", "01/24/2020", "01/25/2020", "06/18/2020", "06/19/2020")

    var snowDateStrings : Array<String> = arrayOf()
    var dayScheduleOverides : MutableMap<String, Int> = mutableMapOf()
    //var snowDateCount = 0

    var restrictedDates : MutableList<Date> = arrayListOf()
    var restrictedDatesForHS : MutableList<Date> = arrayListOf()
    var restrictedDatesForES : MutableList<Date> = arrayListOf()
    var restrictedDateStrings : MutableList<String> = arrayListOf()
    var appendableRestrictedDatesForHSStrings : MutableList<String> = arrayListOf()
    var appendableRestrictedDatesForESStrings : MutableList<String> = arrayListOf()
    var restrictedDatesForHSStrings : Array<String> = arrayOf()
    var restrictedDatesForESStrings : Array<String> = arrayOf()

    var preferences : SharedPreferences? = null

    init {
        if (forSeton || forJohn || forSaints || forJames) {
            preferences = context.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
            restrictedDates.clear()

            snowDateStrings = preferences?.getStringSet("snowDays", setOf())!!.toTypedArray()
            dayScheduleOverides["Seton"] = preferences?.getInt("SetonOverrides", 0)!!
            dayScheduleOverides["John"] = preferences?.getInt("JohnOverrides", 0)!!
            dayScheduleOverides["Saints"] = preferences?.getInt("SaintsOverrides", 0)!!
            dayScheduleOverides["James"] = preferences?.getInt("JamesOverrides", 0)!!
            if (forSeton) { dateDayDict["Seton"]?.clear() }
            if (forJohn) { dateDayDict["St. John's"]?.clear() }
            if (forSaints) { dateDayDict["All Saints"]?.clear() }
            if (forJames) { dateDayDict["St. James"]?.clear() }
            findDayOfCycle(forSeton, forJohn, forSaints, forJames)

        }
    }

    //MARK - Day schedule creator
    private fun findDayOfCycle(forSeton : Boolean, forJohn : Boolean, forSaints : Boolean, forJames : Boolean) {
        val dateParser = SimpleDateFormat("MM/dd/yyyy")
        val weekDayFormatter = SimpleDateFormat("EEEE")
        var date : Date = dateParser.parse(startDateString)
        val endDate : Date = dateParser.parse(endDateString)

        //print("adding no school")
        for (dateString in noSchoolDateStrings) {
            val restrictedDate = dateParser.parse(dateString)
            restrictedDates.add(restrictedDate)
            restrictedDateStrings.add(dateString)
        }
        //print("adding snow dates")
        for (dateString in snowDateStrings) {
            val restrictedDate = dateParser.parse(dateString)
            restrictedDates.add(restrictedDate)
            restrictedDateStrings.add(dateString)
        }

//        restrictedDatesForHSStrings = restrictedDateStrings
//        restrictedDatesForESStrings = restrictedDateStrings
        restrictedDatesForHS = restrictedDates
        restrictedDatesForES = restrictedDates
        restrictedDates.clear()

        if (forSeton) {
            //print("adding exam dates")
            for (dateString in noHighSchoolDateStrings) {
                val restrictedDate = dateParser.parse(dateString)
                restrictedDatesForHS.add(restrictedDate)
                appendableRestrictedDatesForHSStrings.add(dateString)
            }
        }
        restrictedDatesForHSStrings = (restrictedDateStrings + appendableRestrictedDatesForHSStrings).toTypedArray()
        if (forJohn || forSaints || forJames) {
            //print("adding ptc dates")
            for (dateString in noElementarySchoolDateStrings) {
                val restrictedDate = dateParser.parse(dateString)
                restrictedDatesForES.add(restrictedDate)
                appendableRestrictedDatesForESStrings.add(dateString)
            }
        }
        restrictedDatesForESStrings = (restrictedDateStrings + appendableRestrictedDatesForESStrings).toTypedArray()
        restrictedDatesForHS.sort()
        restrictedDatesForES.sort()


        restrictedDatesForHSStrings.printAll()

        restrictedDatesForESStrings.printAll()



        var setonDay = 1 + dayScheduleOverides["Seton"]!!
        var johnDay = 1 + dayScheduleOverides["John"]!!
        var saintsDay = 1 + dayScheduleOverides["Saints"]!!
        var jamesDay = 1 + dayScheduleOverides["James"]!!

        while (date <= endDate) {
            if (weekDayFormatter.format(date) != "Sunday" && weekDayFormatter.format(date) != "Saturday") { //if its a weekday
                val dateString = dateParser.format(date)
                if (forSeton && !restrictedDatesForHSStrings.contains(dateString)) {
                    dateDayDict["Seton"]!![dateString] = setonDay
                    setonDay = proceedToNextDay(setonDay)
                    checkToAddDateToArray(dateString)
                }
                if (forJohn && !restrictedDatesForESStrings.contains(dateString)) {
                    dateDayDict["St. John's"]!![dateString] = johnDay
                    johnDay = proceedToNextDay(johnDay)
                    checkToAddDateToArray(dateString)
                }
                if (forSaints && !restrictedDatesForESStrings.contains(dateString)) {
                    dateDayDict["All Saints"]!![dateString] = saintsDay
                    saintsDay = proceedToNextDay(saintsDay)
                    checkToAddDateToArray(dateString)
                }
                if (forJames && !restrictedDatesForESStrings.contains(dateString)) {
                    dateDayDict["St. James"]!![dateString] = jamesDay
                    jamesDay = proceedToNextDay(jamesDay)
                    checkToAddDateToArray(dateString)
                }

            }
            date = date.addDays(1)
        }

//        if (dateDayDictArray[0] == "") {
//            dateDayDictArray.remove(at: 0)
//        }
//        //print(dateDayDict)
        //print(dateDayDictArray)

    }
    private fun proceedToNextDay(day : Int) : Int {
        var newDay = day + 1
        if (newDay > 6) {
            newDay =  1
        }
        return newDay
    }
    private fun checkToAddDateToArray(dateString : String) {
        if (!dateDayDictArray.contains(dateString)) {
            dateDayDictArray.add(dateString)
        }
    }
}