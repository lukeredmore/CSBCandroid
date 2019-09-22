package com.csbcsaints.CSBCandroid

import android.content.Context
import android.renderscript.Sampler
import android.renderscript.Type
import com.csbcsaints.CSBCandroid.MainActivity
import com.csbcsaints.CSBCandroid.ui.dateString
import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.reflect.TypeToken



/// Checks for snow days and other critical alerts, tells the main screen and updates Firebase
class AlertController(val parent : MainActivity) {

    init {
        parent.removeBannerAlert()
        getDaySchedule()
        checkForAlert()
    }

    private fun getDaySchedule() {
        FirebaseDatabase.getInstance().reference.child("DaySchedule").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val preferences = parent.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
                val daySchedule = p0.value as? Map<String,Map<String,Int>>
                if (!daySchedule.isNullOrEmpty()) {
                    val dayScheduleJSONString = Gson().toJson(daySchedule)
                    preferences.edit().putString("daySchedule", dayScheduleJSONString).apply()
                }
            }
            override fun onCancelled(p0: DatabaseError) { DeveloperPrinter().print("$p0") }
        })
    }

    fun checkForAlert() {
        DeveloperPrinter().print("Checking for alert from Firebase")
        FirebaseDatabase.getInstance().reference.child("BannerAlertMessage")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val alertMessage = p0.value as? String
                    println(alertMessage)
                    if (alertMessage != null && alertMessage != "null" && alertMessage != "nil") {
                        parent.showBannerAlert(alertMessage)
                    } else parent.removeBannerAlert()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    DeveloperPrinter().print("$databaseError")
                }
            })
    }
}