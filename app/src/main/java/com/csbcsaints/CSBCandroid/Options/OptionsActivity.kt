package com.csbcsaints.CSBCandroid

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import com.csbcsaints.CSBCandroid.ui.yearString
import java.util.*

//TODO - add Admin Settings

class OptionsActivity : CSBCAppCompatActivity() {
    private var setonSwitch : Switch? = null
    private var johnSwitch : Switch? = null
    private var saintsSwitch : Switch? = null
    private var jamesSwitch : Switch? = null
    private var showAllSchoolsSwitch : Switch? = null
    private var deliverNotificationsSwitch : Switch? = null
    private var schoolSwitches : Array<Switch?> = arrayOf()
    private var reportIssue : ConstraintLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        supportActionBar?.title = "Options"

        setonSwitch = findViewById(R.id.setonSwitch)
        johnSwitch = findViewById(R.id.johnSwitch)
        saintsSwitch = findViewById(R.id.saintsSwitch)
        jamesSwitch = findViewById(R.id.jamesSwitch)
        showAllSchoolsSwitch = findViewById(R.id.showAllSchoolsSwitch)
        deliverNotificationsSwitch = findViewById(R.id.deliverNotificationsSwitch)
        findViewById<TextView>(R.id.copyrightLabel).text = "© ${Calendar.getInstance().time.yearString()} Catholic Schools of Broome County"
        if (BuildConfig.BUILD_TYPE == "debug") {
            findViewById<TextView>(R.id.versionLabel).text = "${BuildConfig.VERSION_NAME}a"
        } else {
            findViewById<TextView>(R.id.versionLabel).text = BuildConfig.VERSION_NAME
        }
        reportIssue = findViewById(R.id.reportIssue)


        reportIssue?.setOnClickListener {
            val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:lredmore@syrdio.org"))
            email.putExtra(Intent.EXTRA_SUBJECT, "CSBC Android App User Comment")
            email.putExtra(Intent.EXTRA_TEXT, "Please ensure your app has been updated to the latest version. Then, give a short description of the issue you would like to report or the suggestion you would like to submit:")
            startActivity(email)
        }



        showAllSchoolsSwitch?.setOnClickListener {
            if ((!schoolSwitches[0]!!.isChecked && !schoolSwitches[1]!!.isChecked && !schoolSwitches[2]!!.isChecked && !schoolSwitches[3]!!.isChecked && !showAllSchoolsSwitch!!.isChecked) || (schoolSwitches[0]!!.isChecked && schoolSwitches[1]!!.isChecked && schoolSwitches[2]!!.isChecked && schoolSwitches[3]!!.isChecked)) {
                val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
                showAllSchoolsSwitch?.isChecked = true
                preferences.edit().putBoolean("showAllSchools", true).apply()
            }
        }
        deliverNotificationsSwitch?.setOnClickListener {
            val settingsToChange = NotificationController(this@OptionsActivity).notificationSettings
            settingsToChange.shouldDeliver = deliverNotificationsSwitch!!.isChecked
            NotificationController(this@OptionsActivity).notificationSettings = settingsToChange
        }

        schoolSwitches = arrayOf(setonSwitch, johnSwitch, saintsSwitch, jamesSwitch)
        for (i in 0 until schoolSwitches.size) {
            schoolSwitches[i]?.setOnClickListener {
                //Changes notif settings
                println("School Switch $i checked: ${schoolSwitches[i]?.isChecked}")
                val settingsToChange = NotificationController(this@OptionsActivity).notificationSettings
                settingsToChange.schools[i] = schoolSwitches[i]!!.isChecked
                NotificationController(this@OptionsActivity).notificationSettings = settingsToChange

                //Turns on show all switch if all schools are off
                if (schoolSwitches[0] != null && schoolSwitches[1] != null && schoolSwitches[2] != null && schoolSwitches[3] != null) {
                    val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
                    if (!schoolSwitches[0]!!.isChecked && !schoolSwitches[1]!!.isChecked && !schoolSwitches[2]!!.isChecked && !schoolSwitches[3]!!.isChecked) {
                        showAllSchoolsSwitch!!.isChecked = true
                        preferences.edit().putBoolean("showAllSchools", true).apply()
                    }
                }
            }
        }

        getNotificationPreferences()
    }
    override fun onStop() {
        super.onStop()

        val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        val settingsToChange = NotificationController(this@OptionsActivity).notificationSettings
        if (!schoolSwitches[0]!!.isChecked && !schoolSwitches[1]!!.isChecked && !schoolSwitches[2]!!.isChecked && !schoolSwitches[3]!!.isChecked) {
            preferences.edit().putBoolean("showAllSchools", true).apply()
            settingsToChange.shouldDeliver = false
        } else {
            preferences.edit().putBoolean("showAllSchools", showAllSchoolsSwitch!!.isChecked).apply()
            settingsToChange.shouldDeliver = deliverNotificationsSwitch!!.isChecked
        }
        if (!settingsToChange.schools.contentEquals(arrayOf(false, false, false, false)))
            schoolSelected = schoolSelectedArray.first { schoolSwitches[schoolSelectedArray.indexOf(it)]?.isChecked ?: false }
        preferences.edit()?.putString("schoolSelected", schoolSelected)?.apply()
        NotificationController(this@OptionsActivity).notificationSettings = settingsToChange
    }


    private fun getNotificationPreferences() {
        val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        for (i in 0 until 4) { //Schools switches
            schoolSwitches[i]?.isChecked = NotificationController(this@OptionsActivity).notificationSettings.schools[i]
        }
        showAllSchoolsSwitch?.isChecked = preferences.getBoolean("showAllSchools", false)
        deliverNotificationsSwitch?.isChecked = NotificationController(this@OptionsActivity).notificationSettings.shouldDeliver
    }
}
