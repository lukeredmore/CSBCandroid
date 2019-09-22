package com.csbcsaints.CSBCandroid

import com.csbcsaints.CSBCandroid.ui.addDays
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context
import android.content.SharedPreferences
import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter
import com.csbcsaints.CSBCandroid.ui.printAll
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class DaySchedule(context : Context) {
    var preferences : SharedPreferences? = null

    private var forElementarySchool: Map<String,Int>
        get() {
        val existingDaySchedJSON = preferences?.getString("daySchedule", null)
        val listType = object : TypeToken<Map<String, Map<String, Int>>>() {}.type
        val existingDaySched: Map<String, Map<String, Int>> = Gson().fromJson(existingDaySchedJSON, listType) ?: mapOf()
        return existingDaySched["elementarySchool"] ?: mapOf()
        } set(newValue) {}

    private var forHighSchool: Map<String,Int>
        get() {
            val existingDaySchedJSON = preferences?.getString("daySchedule", null)
            val listType = object : TypeToken<Map<String, Map<String, Int>>>() {}.type
            val existingDaySched: Map<String, Map<String, Int>> = Gson().fromJson(existingDaySchedJSON, listType) ?: mapOf()
            return existingDaySched["highSchool"] ?: mapOf()
        } set(newValue) {}


    init {
        preferences = context.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
    }

    fun day(onDate: Date?, forSchool: String?) : Int? {
        val dateStringFormatter = SimpleDateFormat("yyyy-MM-dd")
        return if (onDate != null && forSchool !== null) {
            val dateString = dateStringFormatter.format(onDate)
            when (forSchool) {
                "Seton" -> forHighSchool[dateString]
                "St. John's", "St. James","All Saints" -> forElementarySchool[dateString]
                else -> null
            }
        } else null
    }
}