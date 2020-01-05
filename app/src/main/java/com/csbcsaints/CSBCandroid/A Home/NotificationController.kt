package com.csbcsaints.CSBCandroid

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson


class NotificationController(val context: Context) {
    var notificationSettings: NotificationSettings
        get() {
            val json = sharedPreferences?.getString("NotificationSettings", null)
            return if (!json.isNullOrEmpty()) {
                println("Notification settings exist")
                Gson().fromJson(json, NotificationSettings::class.java)
            } else {
                println("No notification settings exist, returning default one")
                NotificationSettings(true, arrayOf(true, true, true, true))
            }
        }
        set(newValue) {
            print("settings did change")
            newValue.printNotifData()
            subscribeToTopics()
            val json = Gson().toJson(newValue)
            sharedPreferences?.edit()?.putString("NotificationSettings", json)?.apply()
        }

    private var sharedPreferences : SharedPreferences? = null


    init {
        sharedPreferences = context.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
    }


    //MARK - Notification Settings
    fun setupNotifications() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                print("Connection to Firebase Messaging Unsuccessful ${task.exception}")
                return@OnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result?.token!!
            println("Device token: $token")
            subscribeToTopics()
        })
    }


    //MARK - Show notifications
    private fun subscribeToTopics() {
        if (!notificationSettings.shouldDeliver) {
            FirebaseMessaging.getInstance().subscribeToTopic("notReceivingNotifications").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Subscribed to 'notReceivingNotifications'")
                } else {
                    println("Subscription to 'notReceivingNotifications' failed with exception: ${task.exception}")
                }
            }
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("notReceivingNotifications").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Unsubscribed from 'notReceivingNotifications'")
                } else {
                    println("Unsubscription from 'notReceivingNotifications' failed with exception: ${task.exception}")
                }
            }
        }
        val topicArray = arrayOf("setonNotifications","johnNotifications","saintsNotifications","jamesNotifications")
        val schoolBools = notificationSettings.schools
        for (i in 0 until 4) {
            if (schoolBools[i]) {
                FirebaseMessaging.getInstance().subscribeToTopic(topicArray[i]).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Subscribed to '${topicArray[i]}'")
                    } else {
                        println("Subscription to '${topicArray[i]}' failed with exception: ${task.exception}")
                    }
                }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(topicArray[i]).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        println("Unsubscribed from ${topicArray[i]}")
                    } else {
                        println("Unsubscription from ${topicArray[i]} failed with exception: ${task.exception}")
                    }
                }
            }
            FirebaseAnalytics.getInstance(context).setUserProperty(topicArray[i], "${notificationSettings.schools[i]}")

        }
    }

}
