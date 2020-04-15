package com.csbcsaints.CSBCandroid

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson


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
            override fun onCancelled(p0: DatabaseError) { println("$p0") }
        })
    }

    fun checkForAlert() {
        println("Checking for alert from Firebase")
        FirebaseDatabase.getInstance().reference.child("BannerAlert/message")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val alertMessage = p0.value as? String
                    println(alertMessage)
                    if (alertMessage != null && alertMessage != "null" && alertMessage != "nil" && alertMessage != "") {
                        parent.showBannerAlert(alertMessage.replace("--include-covid-modal--", ""))
                    } else parent.removeBannerAlert()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("$databaseError")
                }
            })
    }
}