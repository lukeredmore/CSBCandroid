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
import com.csbcsaints.CSBCandroid.Calendar.EventsModel
import com.csbcsaints.CSBCandroid.ui.*
import com.google.firebase.analytics.FirebaseAnalytics
import java.text.SimpleDateFormat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import java.util.*

class NotificationController {

    var timeFormatter : SimpleDateFormat = SimpleDateFormat("h:mm a")
    var timeFormatterIn24H : SimpleDateFormat = SimpleDateFormat("HH:mm")
    var dateStringFormatter : SimpleDateFormat = SimpleDateFormat("MM/dd/yyyy")

    var timeComponents : MutableList<Int> = arrayListOf()
    val dayScheduleLite = DaySchedule(AppCompatActivity())
    var notificationSettings : NotificationSettings?

    val todaysDate = Calendar.getInstance().time
    var context : Context

    var sharedPreferences : SharedPreferences? = null

    constructor(context: Context) {
        this.context = context
        sharedPreferences = context.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        notificationSettings = defineNotificationSettings()
    }



    fun reconstruct() {
        notificationSettings = null
        notificationSettings = defineNotificationSettings()
    }

    fun queueNotifications() {
        val center = NotificationManagerCompat.from(context)
        center.cancelAll()

        if (notificationSettings?.shouldDeliver ?: false && todaysDate < dateStringFormatter.parse(dayScheduleLite.endDateString)!!) { //If date is during school year
            print("Notifications queuing")

            val notifTimeAsDate = timeFormatter.parse(notificationSettings!!.deliveryTime) //Get time of notif deliver as date
            val notif24HTimeString = timeFormatterIn24H.format(notifTimeAsDate!!) //rewrite in 24h (16:23)
            println(notif24HTimeString)
//            val timeComponentsStrings = notif24HTimeString.split(":").toMutableList() //convert to components
//            print(timeComponentsStrings)
//            for (i in timeComponentsStrings) {
//                timeComponents.add(i.toInt())
//            }
//            print(timeComponents)

            val daySchedule = DaySchedule(context, notificationSettings!!.schools[0], notificationSettings!!.schools[1], notificationSettings!!.schools[2], notificationSettings!!.schools[3])
            val allSchoolDays : MutableList<String> = daySchedule.dateDayDictArray
            allSchoolDays.printAll()
            while (dateStringFormatter.parse(allSchoolDays.first())!! < todaysDate.addDays(-1)) { //Remove past dates
                println(allSchoolDays[0])
                allSchoolDays.removeAt(0)
            }
            allSchoolDays.printAll()

//            for (i in 50 until allSchoolDays.count()) { //Limit all schoolDaysCount to 50
//                allSchoolDays.removeAt(50)
//            }

            //Mark: Setup notification
            for (date in allSchoolDays) {
                //print(date)

                //WHAT
                var notificationContent1 = ""
                var notificationContent2 = ""
                var notificationContent3 = ""
                var notificationContent4 = ""

                if (notificationSettings!!.schools[0] && !daySchedule.restrictedDatesForHS.contains(dateStringFormatter.parse(date)!!)) {
                    val dayOfCycle = daySchedule.dateDayDict["Seton"]!![date]
                    if (dayOfCycle != null) {
                        notificationContent1 = "Day $dayOfCycle at Seton, "
                    }
                }
                if (notificationSettings!!.schools[1] && !daySchedule.restrictedDatesForES.contains(dateStringFormatter.parse(date)!!)) {
                    val dayOfCycle = daySchedule.dateDayDict["St. John's"]!![date]
                    if (dayOfCycle != null) {
                        notificationContent2 = "Day $dayOfCycle at St. John's, "
                    }
                }
                if (notificationSettings!!.schools[2] && !daySchedule.restrictedDatesForES.contains(dateStringFormatter.parse(date)!!)) {
                    val dayOfCycle = daySchedule.dateDayDict["All Saints"]!![date]
                    if (dayOfCycle != null) {
                        notificationContent3 = "Day $dayOfCycle at All Saints, "
                    }
                }
                if (notificationSettings!!.schools[3] && !daySchedule.restrictedDatesForES.contains(dateStringFormatter.parse(date)!!)) {
                    val dayOfCycle = daySchedule.dateDayDict["St. James"]!![date]
                    if (dayOfCycle != null) {
                        notificationContent4 = "Day $dayOfCycle at St. James, "
                    }
                }
                val notificationContent = "$notificationContent1$notificationContent2$notificationContent3$notificationContent4"
                notificationContent.dropLast(2)

                //WHEN
                val dateWithTimeString = "$date $notif24HTimeString"
                val dateWithTimeFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm")
                val dateWithTime = dateWithTimeFormatter.parse(dateWithTimeString)
                val timeInMillisUntilNotifShouldDeliver = dateWithTime.time //- Calendar.getInstance().time.time

                println("dateWithTimeString: $dateWithTimeString")
                println("dateWithTime: $dateWithTime")
                println("timeInMillisUntilNotifShouldDeliver: $timeInMillisUntilNotifShouldDeliver")

//                //REQUEST
                val requestCode = date.replace("/", "").toInt()
                val notificationIntent = Intent("android.media.action.DISPLAY_NOTIFICATION")
                notificationIntent.addCategory("android.intent.category.DEFAULT")
                notificationIntent.putExtra("body", "Today is $date. Today is $notificationContent.")
                //notificationIntent.putExtra("body", "Today is Day $dayOfCycle. $gymDayString")
                notificationIntent.putExtra("requestCode", requestCode)

                val broadcast = PendingIntent.getBroadcast(context, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                if (timeInMillisUntilNotifShouldDeliver > Calendar.getInstance().time.time) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillisUntilNotifShouldDeliver, broadcast)
                }

                println("Today is $date. Today is $notificationContent.")
                println(" ")
            }

//            center.getPendingNotificationRequests(completionHandler: { requests in
//                    for request in requests {
//                        print(request)
//                    }
//            })
        } else {
            print("User has declined to receive notifications")
        }

    }

    fun subscribeToTopics() {
        val topicArray = arrayOf("setonNotifications","johnNotifications","saintsNotifications","jamesNotifications")
        val schoolBools = notificationSettings?.schools ?: arrayOf(false, false, false, false)
        for (i in 0 until 4) {
            if (schoolBools[i]) {
                FirebaseMessaging.getInstance().subscribeToTopic(topicArray[i])
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("Subscribed to ${topicArray[i]}")
                        } else {
                            print("Subscription to ${topicArray[i]} failed with exception: ")
                            println(task.exception)
                        }
                    }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topicArray[i])
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            println("Unsubscribed from ${topicArray[i]}")
                        } else {
                            print("Unsubscription to ${topicArray[i]} failed with exception: ")
                            println(task.exception)
                        }
                    }
            }
            FirebaseAnalytics.getInstance(context).setUserProperty(topicArray[i], "${notificationSettings!!.schools[i]}")

        }
    }

    fun defineNotificationSettings() : NotificationSettings {
        //val sharedPreferences2 =

        val json = sharedPreferences?.getString("Notifications", null)
        if (!json.isNullOrEmpty()) {
            println("Notification settings exist")
            val settingsToReturn = Gson().fromJson(json, NotificationSettings::class.java)
            settingsToReturn.printNotifData()
            return settingsToReturn
        } else {
            println("No notification settings exist, returning default one")
            return NotificationSettings(true, "7:00 AM", arrayOf(true, true, true, true), false)
        }

    }

    fun storeNotificationSettings(settings: NotificationSettings) {
        settings.printNotifData()
        this.notificationSettings = settings
        val json = Gson().toJson(settings)
        sharedPreferences?.edit()?.putString("Notifications", json)?.apply()
    }

}