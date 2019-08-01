package com.csbcsaints.CSBCandroid

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.csbcsaints.CSBCandroid.ui.yearString
import java.text.SimpleDateFormat
import java.util.*

//TODO - add Admin Settings

class OptionsActivity : AppCompatActivity() {
    var setonSwitch : Switch? = null
    var johnSwitch : Switch? = null
    var saintsSwitch : Switch? = null
    var jamesSwitch : Switch? = null
    var showAllSchoolsSwitch : Switch? = null
    var deliverNotificationsSwitch : Switch? = null
    var deliveryTimeLabel : EditText? = null
    var deliveryTimeCell : LinearLayout? = null
    var reportIssue : ConstraintLayout? = null
    var settingsSwitch : Array<Switch?> = arrayOf()

    var notificationController : NotificationController? = null
    var notificationSettings : NotificationSettings? = null

    val normalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    val collapsedParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        getSupportActionBar()?.setTitle("Options")

        setonSwitch = findViewById(R.id.setonSwitch)
        johnSwitch = findViewById(R.id.johnSwitch)
        saintsSwitch = findViewById(R.id.saintsSwitch)
        jamesSwitch = findViewById(R.id.jamesSwitch)
        showAllSchoolsSwitch = findViewById(R.id.showAllSchoolsSwitch)
        deliverNotificationsSwitch = findViewById(R.id.deliverNotificationsSwitch)
        deliveryTimeLabel = findViewById(R.id.deliveryTimeLabel)
        deliveryTimeCell = findViewById(R.id.deliveryTimeCell)
        findViewById<TextView>(R.id.copyrightLabel).text = "© ${Calendar.getInstance().time.yearString()} Catholic Schools of Broome County"
        if (BuildConfig.BUILD_TYPE == "debug") {
            findViewById<TextView>(R.id.versionLabel).text = "${BuildConfig.VERSION_NAME}a"
        } else {
            findViewById<TextView>(R.id.versionLabel).text = BuildConfig.VERSION_NAME
        }
        reportIssue = findViewById(R.id.reportIssue)


        reportIssue?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v:View) {
                val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:lredmore@syrdio.org"))
                email.putExtra(Intent.EXTRA_SUBJECT, "CSBC Android App User Comment")
                email.putExtra(Intent.EXTRA_TEXT, "Please give a detailed description of the issue you would like to report or the suggestion you would like to submit:")
                startActivity(email)
            }
        })

        deliveryTimeLabel?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v:View) {
                val fullTimeFormatter = SimpleDateFormat("h:mm a")
                val deliveryTime = fullTimeFormatter.parse(notificationSettings?.deliveryTime)
                val hourFormatter = SimpleDateFormat("HH")
                val hour = hourFormatter.format(deliveryTime).toInt()
                val minuteFormatter = SimpleDateFormat("mm")
                val minute = minuteFormatter.format(deliveryTime).toInt()
                val is24HourView = false
                // date picker dialog
                val timePickerDialog = TimePickerDialog(this@OptionsActivity,
                    object:TimePickerDialog.OnTimeSetListener {
                        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                            val timeFormatter = SimpleDateFormat("HH:mm")
                            val timeAsDate = timeFormatter.parse("$hourOfDay:$minute")
                            deliveryTimeLabel?.setText(fullTimeFormatter.format(timeAsDate))
                            notificationSettings?.deliveryTime = deliveryTimeLabel?.text.toString()
                            notificationController?.storeNotificationSettings(notificationSettings!!)
                        }

                    }, hour, minute, is24HourView)
                timePickerDialog.show()
            }
        })


        setonSwitch?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                println("Seton Switch checked: ${setonSwitch?.isChecked}")
                notificationSettings!!.schools[0] = setonSwitch!!.isChecked
                settingsSwitchToggled()
            }
        })
        johnSwitch?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                println("John Switch checked: ${johnSwitch?.isChecked}")
                notificationSettings!!.schools[1] = setonSwitch!!.isChecked
                settingsSwitchToggled()
            }
        })
        saintsSwitch?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                println("Saints Switch checked: ${saintsSwitch?.isChecked}")
                notificationSettings!!.schools[2] = setonSwitch!!.isChecked
                settingsSwitchToggled()
            }
        })
        jamesSwitch?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view:View) {
                println("James Switch checked: ${jamesSwitch?.isChecked}")
                notificationSettings!!.schools[3] = setonSwitch!!.isChecked
                settingsSwitchToggled()
            }
        })
        showAllSchoolsSwitch?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view: View?) {
                notificationSettings!!.valuesChangedByUser = true
                if ((!settingsSwitch[0]!!.isChecked && !settingsSwitch[1]!!.isChecked && !settingsSwitch[2]!!.isChecked && !settingsSwitch[3]!!.isChecked && !settingsSwitch[4]!!.isChecked) || (settingsSwitch[0]!!.isChecked && settingsSwitch[1]!!.isChecked && settingsSwitch[2]!!.isChecked && settingsSwitch[3]!!.isChecked)) {
                    val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
                    settingsSwitch[4]?.isChecked = true
                    preferences.edit().putBoolean("showAllSchools", true).apply()
                }
            }
        })
        deliverNotificationsSwitch?.setOnClickListener(object:View.OnClickListener {
            override fun onClick(view: View?) {
                notificationSettings!!.valuesChangedByUser = true
                if (deliverNotificationsSwitch!!.isChecked) { //should get notifs
                    deliveryTimeCell?.setLayoutParams(normalParams)
                } else {
                    deliveryTimeCell?.setLayoutParams(collapsedParams)
                }
                notificationSettings!!.shouldDeliver = deliverNotificationsSwitch!!.isChecked
                notificationController?.storeNotificationSettings(notificationSettings!!)
            }

        })

        notificationController = NotificationController(this)
        notificationSettings = notificationController?.notificationSettings

        settingsSwitch = arrayOf(setonSwitch, johnSwitch, saintsSwitch, jamesSwitch, showAllSchoolsSwitch)

        getNotificationPreferences()
    }
    override fun onStop() {
        super.onStop()

        val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

        notificationSettings?.schools = arrayOf(settingsSwitch[0]!!.isChecked, settingsSwitch[1]!!.isChecked, settingsSwitch[2]!!.isChecked, settingsSwitch[3]!!.isChecked)
        val userEntered = deliveryTimeLabel?.text.toString()
        if ((userEntered.count() == 4 || userEntered.count() == 5) && (userEntered.toCharArray()[1] == ':' || userEntered.toCharArray()[2] == ':')) {
            notificationSettings?.deliveryTime = userEntered
        } else {
            notificationSettings?.deliveryTime = "7:00 AM"
        }

        if (!settingsSwitch[0]!!.isChecked && !settingsSwitch[1]!!.isChecked && !settingsSwitch[2]!!.isChecked && !settingsSwitch[3]!!.isChecked) {
            preferences.edit().putBoolean("showAllSchools", true).apply()
            notificationSettings?.shouldDeliver = false
        } else {
            preferences.edit().putBoolean("showAllSchools", settingsSwitch[4]!!.isChecked).apply()
            notificationSettings!!.shouldDeliver = deliverNotificationsSwitch!!.isChecked
        }

        if (!notificationSettings!!.shouldDeliver || notificationSettings!!.deliveryTime != "7:00 AM" || !notificationSettings!!.schools.contentEquals(arrayOf(true, true, true, true)) || notificationSettings!!.valuesChangedByUser) {
            notificationSettings!!.valuesChangedByUser = true
        }
        notificationController?.storeNotificationSettings(notificationSettings!!)
        notificationController?.subscribeToTopics()
    }


    fun getNotificationPreferences() {

        val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

        for (i in 0 until 4) { //Schools switches
            settingsSwitch[i]?.isChecked = notificationSettings!!.schools[i]
        }
        settingsSwitch[4]?.isChecked = preferences.getBoolean("showAllSchools", true)



        if (notificationSettings!!.shouldDeliver) { //should get notifs
            deliverNotificationsSwitch?.isChecked = true
            deliveryTimeCell?.setLayoutParams(normalParams)
        } else {
            deliverNotificationsSwitch?.isChecked = false
            deliveryTimeCell?.setLayoutParams(collapsedParams)
        }

        deliveryTimeLabel?.setText(notificationSettings?.deliveryTime)//what time should they be
    }
    fun settingsSwitchToggled() {

        notificationSettings?.valuesChangedByUser = true
        notificationController?.storeNotificationSettings(notificationSettings!!)
        if (settingsSwitch[0] != null && settingsSwitch[1] != null && settingsSwitch[2] != null && settingsSwitch[3] != null && settingsSwitch[4] != null) {
            val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
            if ((!settingsSwitch[0]!!.isChecked && !settingsSwitch[1]!!.isChecked && !settingsSwitch[2]!!.isChecked && !settingsSwitch[3]!!.isChecked) || (settingsSwitch[0]!!.isChecked && settingsSwitch[1]!!.isChecked && settingsSwitch[2]!!.isChecked && settingsSwitch[3]!!.isChecked)) {
                settingsSwitch[4]!!.isChecked = true
                preferences.edit().putBoolean("showAllSchools", true).apply()
            }

            notificationController?.notificationSettings = notificationSettings
        }
    }
}
