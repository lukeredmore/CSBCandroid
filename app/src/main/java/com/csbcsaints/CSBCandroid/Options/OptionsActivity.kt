package com.csbcsaints.CSBCandroid

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.csbcsaints.CSBCandroid.ui.yearString
import java.util.*
import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter

//TODO - add Admin Settings

class OptionsActivity : AppCompatActivity() {
    private var setonSwitch : Switch? = null
    private var johnSwitch : Switch? = null
    private var saintsSwitch : Switch? = null
    private var jamesSwitch : Switch? = null
    private var showAllSchoolsSwitch : Switch? = null
    private var deliverNotificationsSwitch : Switch? = null
    private var reportIssue : ConstraintLayout? = null
    private var settingsSwitch : Array<Switch?> = arrayOf()


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
            email.putExtra(Intent.EXTRA_TEXT, "Please ensure your app has been updated to the lates version. Then, give a short description of the issue you would like to report or the suggestion you would like to submit:")
            startActivity(email)
        }


        setonSwitch?.setOnClickListener {
            DeveloperPrinter().print("Seton Switch checked: ${setonSwitch?.isChecked}")
            NotificationController(this@OptionsActivity).notificationSettings.schools[0] = setonSwitch!!.isChecked
            settingsSwitchToggled()
        }
        johnSwitch?.setOnClickListener {
            DeveloperPrinter().print("John Switch checked: ${johnSwitch?.isChecked}")
            NotificationController(this@OptionsActivity).notificationSettings.schools[1] = setonSwitch!!.isChecked
            settingsSwitchToggled()
        }
        saintsSwitch?.setOnClickListener {
            DeveloperPrinter().print("Saints Switch checked: ${saintsSwitch?.isChecked}")
            NotificationController(this@OptionsActivity).notificationSettings.schools[2] = setonSwitch!!.isChecked
            settingsSwitchToggled()
        }
        jamesSwitch?.setOnClickListener {
            DeveloperPrinter().print("James Switch checked: ${jamesSwitch?.isChecked}")
            NotificationController(this@OptionsActivity).notificationSettings.schools[3] = setonSwitch!!.isChecked
            settingsSwitchToggled()
        }
        showAllSchoolsSwitch?.setOnClickListener {
            NotificationController(this@OptionsActivity).notificationSettings.valuesChangedByUser = true
            if ((!settingsSwitch[0]!!.isChecked && !settingsSwitch[1]!!.isChecked && !settingsSwitch[2]!!.isChecked && !settingsSwitch[3]!!.isChecked && !settingsSwitch[4]!!.isChecked) || (settingsSwitch[0]!!.isChecked && settingsSwitch[1]!!.isChecked && settingsSwitch[2]!!.isChecked && settingsSwitch[3]!!.isChecked)) {
                val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
                settingsSwitch[4]?.isChecked = true
                preferences.edit().putBoolean("showAllSchools", true).apply()
            }
        }
        deliverNotificationsSwitch?.setOnClickListener {
            NotificationController(this@OptionsActivity).notificationSettings.valuesChangedByUser = true
            NotificationController(this@OptionsActivity).notificationSettings.shouldDeliver = deliverNotificationsSwitch!!.isChecked
        }

        settingsSwitch = arrayOf(setonSwitch, johnSwitch, saintsSwitch, jamesSwitch, showAllSchoolsSwitch)

        getNotificationPreferences()
    }
    override fun onStop() {
        super.onStop()

        val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

        NotificationController(this@OptionsActivity).notificationSettings.schools = arrayOf(settingsSwitch[0]!!.isChecked, settingsSwitch[1]!!.isChecked, settingsSwitch[2]!!.isChecked, settingsSwitch[3]!!.isChecked)

        if (!settingsSwitch[0]!!.isChecked && !settingsSwitch[1]!!.isChecked && !settingsSwitch[2]!!.isChecked && !settingsSwitch[3]!!.isChecked) {
            preferences.edit().putBoolean("showAllSchools", true).apply()
            NotificationController(this@OptionsActivity).notificationSettings.shouldDeliver = false
        } else {
            preferences.edit().putBoolean("showAllSchools", settingsSwitch[4]!!.isChecked).apply()
            NotificationController(this@OptionsActivity).notificationSettings.shouldDeliver = deliverNotificationsSwitch!!.isChecked
        }

        if (!NotificationController(this@OptionsActivity).notificationSettings.shouldDeliver || NotificationController(this@OptionsActivity).notificationSettings.deliveryTime != "7:00 AM" || !NotificationController(this@OptionsActivity).notificationSettings.schools.contentEquals(arrayOf(true, true, true, true)) || NotificationController(this@OptionsActivity).notificationSettings.valuesChangedByUser) {
            NotificationController(this@OptionsActivity).notificationSettings.valuesChangedByUser = true
        }
    }


    private fun getNotificationPreferences() {

        val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

        for (i in 0 until 4) { //Schools switches
            settingsSwitch[i]?.isChecked = NotificationController(this@OptionsActivity).notificationSettings.schools[i]
        }
        settingsSwitch[4]?.isChecked = preferences.getBoolean("showAllSchools", true)



        deliverNotificationsSwitch?.isChecked = NotificationController(this@OptionsActivity).notificationSettings.shouldDeliver

    }
    private fun settingsSwitchToggled() {

        NotificationController(this@OptionsActivity).notificationSettings.valuesChangedByUser = true
        if (settingsSwitch[0] != null && settingsSwitch[1] != null && settingsSwitch[2] != null && settingsSwitch[3] != null && settingsSwitch[4] != null) {
            val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
            if ((!settingsSwitch[0]!!.isChecked && !settingsSwitch[1]!!.isChecked && !settingsSwitch[2]!!.isChecked && !settingsSwitch[3]!!.isChecked) || (settingsSwitch[0]!!.isChecked && settingsSwitch[1]!!.isChecked && settingsSwitch[2]!!.isChecked && settingsSwitch[3]!!.isChecked)) {
                settingsSwitch[4]!!.isChecked = true
                preferences.edit().putBoolean("showAllSchools", true).apply()
            }
        }
    }
}
