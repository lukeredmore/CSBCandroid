package com.csbcsaints.CSBCandroid

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.csbcsaints.CSBCandroid.ui.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import java.text.SimpleDateFormat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import java.util.*

//TODO - See stack about queuing notifications with Payload over multiple alarms

class NotificationController(val context: Context) {
    var timeFormatter : SimpleDateFormat = SimpleDateFormat("h:mm a")
    var timeFormatterIn24H : SimpleDateFormat = SimpleDateFormat("HH:mm")
    var dateStringFormatter : SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy")
    val dayScheduleLite = DaySchedule(AppCompatActivity())
    var notificationSettings : NotificationSettings?
    val todaysDate = Calendar.getInstance().time

    var sharedPreferences : SharedPreferences? = null


    init {
        sharedPreferences = context.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        notificationSettings = defineNotificationSettings()
    }
    fun reconstruct() {
        notificationSettings = null
        notificationSettings = defineNotificationSettings()
    }


    //MARK - Notification Settings
    fun setupNotifications() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    print("Connection to Firebase Messaging Unsucessful ${task.exception}")
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token!!
                DeveloperPrinter().print("Device token: $token")

                subscribeToTopics()
            })
    }
    private fun defineNotificationSettings() : NotificationSettings {
        val json = sharedPreferences?.getString("Notifications", null)
        if (!json.isNullOrEmpty()) {
            DeveloperPrinter().print("Notification settings exist")
            val settingsToReturn = Gson().fromJson(json, NotificationSettings::class.java)
            settingsToReturn.printNotifData()
            return settingsToReturn
        } else {
            DeveloperPrinter().print("No notification settings exist, returning default one")
            return NotificationSettings(true, "7:00 AM", arrayOf(true, true, true, true), false)
        }

    }
    fun storeNotificationSettings(settings: NotificationSettings) {
        settings.printNotifData()
        this.notificationSettings = settings
        val json = Gson().toJson(settings)
        sharedPreferences?.edit()?.putString("Notifications", json)?.apply()
    }


    //MARK - Show notifications
    fun subscribeToTopics() {
        val topicArray = arrayOf("setonNotifications","johnNotifications","saintsNotifications","jamesNotifications")
        val schoolBools = notificationSettings?.schools ?: arrayOf(false, false, false, false)
        for (i in 0 until 4) {
            if (schoolBools[i]) {
                FirebaseMessaging.getInstance().subscribeToTopic(topicArray[i])
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            DeveloperPrinter().print("Subscribed to ${topicArray[i]}")
                        } else {
                            println("Subscription to ${topicArray[i]} failed with exception: ${task.exception}")
                        }
                    }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topicArray[i])
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            DeveloperPrinter().print("Unsubscribed from ${topicArray[i]}")
                        } else {
                            println("Unsubscription to ${topicArray[i]} failed with exception: ${task.exception}")
                        }
                    }
            }
            FirebaseAnalytics.getInstance(context).setUserProperty(topicArray[i], "${notificationSettings!!.schools[i]}")

        }
    }

}
