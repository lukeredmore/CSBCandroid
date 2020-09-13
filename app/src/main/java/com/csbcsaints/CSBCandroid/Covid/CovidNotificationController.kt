package com.csbcsaints.CSBCandroid.Covid

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.csbcsaints.CSBCandroid.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class CovidNotificationController : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val notification =
            intent.getParcelableExtra<Notification>(NOTIFICATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            if (notificationManager == null) {
                error("Assertion failed")
            }
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        if (notificationManager == null) {
            error("Assertion failed")
        }
        notificationManager.notify(id, notification)
    }




    companion object {
        var NOTIFICATION_ID = "notification-id"
        var NOTIFICATION = "notification"
        const val NOTIFICATION_CHANNEL_ID = "10001"
        private const val default_notification_channel_id = "default"

        fun configure(context: Context, familyCheckInNotifications: Boolean) {

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager

            println("hereee")
            reCreateNotificationChannel(notificationManager)

            if (!familyCheckInNotifications) return

            val startDate = Calendar.getInstance()
//            startDate.set(Calendar.HOUR_OF_DAY, 17)
//            startDate.set(Calendar.MINUTE, 30)
//            startDate.set(Calendar.SECOND, 0)
//
//            while (startDate[Calendar.DAY_OF_WEEK] != Calendar.MONDAY) {
//                startDate.add(Calendar.DATE, 1)
//            }

            for (i in 0..10) {
                createAndScheduleNotification(context, alarmManager, "Time to check in!", "Content of notif", startDate.time.time)
                startDate.time.time += 10000//1000 * 60 * 60 * 24 * 7

            }
        }

        private fun createAndScheduleNotification(context: Context, alarmManager: AlarmManager?, title: String, content: String, time: Long) {
            fun createNotification(context: Context, title: String, content: String): Notification {
                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                val builder = NotificationCompat.Builder(context, default_notification_channel_id)
                builder.setContentTitle(title)
                builder.setContentText(content)
                builder.setSmallIcon(R.drawable.ic_stat_lettermark)
                builder.setAutoCancel(true)
                builder.setChannelId(NOTIFICATION_CHANNEL_ID)
                builder.setTicker(content)
                builder.setSound(defaultSoundUri)
                builder.setStyle(NotificationCompat.BigTextStyle().bigText(content))
                println("This one is now scheduled" + content)
                return builder.build()
            }


            val notification = createNotification(context, title, content)
            val notificationIntent = Intent(context, CovidNotificationController::class.java)
            notificationIntent.putExtra(NOTIFICATION_ID, 1)
            notificationIntent.putExtra(NOTIFICATION, notification)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            if (alarmManager != null) {
                alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, time] = pendingIntent
            }
        }

        private fun reCreateNotificationChannel(notificationManager: NotificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.cancelAll()
                notificationManager.deleteNotificationChannel(NOTIFICATION_CHANNEL_ID)

                // Create the NotificationChannel
                val name = "Test Channel"
                val descriptionText = "This is a test for now"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
                mChannel.description = descriptionText
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this

                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }



}