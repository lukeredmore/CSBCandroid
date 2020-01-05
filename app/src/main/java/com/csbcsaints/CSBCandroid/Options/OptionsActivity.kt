package com.csbcsaints.CSBCandroid.Options

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.csbcsaints.CSBCandroid.BuildConfig
import com.csbcsaints.CSBCandroid.NotificationController
import com.csbcsaints.CSBCandroid.R
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import com.csbcsaints.CSBCandroid.ui.toPx
import com.csbcsaints.CSBCandroid.ui.writeToScreen
import com.csbcsaints.CSBCandroid.ui.yearString
import com.google.android.gms.auth.api.signin.GoogleSignIn
import java.util.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInClient

//Handles all of a user's preferences and their adminstrative status
class OptionsActivity : CSBCAppCompatActivity() {
    private var setonSwitch : Switch? = null
    private var johnSwitch : Switch? = null
    private var saintsSwitch : Switch? = null
    private var jamesSwitch : Switch? = null
    private var showAllSchoolsSwitch : Switch? = null
    private var deliverNotificationsSwitch : Switch? = null
    private var schoolSwitches : Array<Switch?> = arrayOf()
    private var reportIssue : ConstraintLayout? = null
    private var copyrightLabel : TextView? = null
    private var versionLabel : TextView? = null

    private var adminSettingsHeader : ConstraintLayout? = null
    private var viewActivePassesCell : ConstraintLayout? = null
    private var sendNotificationCell : ConstraintLayout? = null
    private var signInButton : TextView? = null

    private val GID_REQUEST_CODE = 42069
    private val allowedUserEmails : Map<String,String> = mapOf(
    "lredmore" to "Seton", "kehret" to "Seton", "ecarter" to "Seton", "mmartinkovic" to "Seton", "llevis" to "Seton",
    "jfountaine" to "St. John's", "krosen" to "St. John's",
    "wpipher" to "All Saints", "kpawlowski" to "All Saints",
    "skitchen" to "St. James", "isanyshyn" to "St. James")
    private val gso : GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
    private val mGoogleSignInClient : GoogleSignInClient by lazy {
        GoogleSignIn.getClient(this, gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_options)
            supportActionBar?.hide()
            supportActionBar?.title = "Options"
            findViewsInContentLayout()

            copyrightLabel?.text = "© ${Calendar.getInstance().time.yearString()} Catholic Schools of Broome County"
            versionLabel?.text = BuildConfig.VERSION_NAME + (if (BuildConfig.BUILD_TYPE == "debug") "a" else "")

            updateUIForAuthentication()

            reportIssue?.setOnClickListener {
                val config = ComposerConfiguration("Submit", "Please give a detailed description of the issue you would like to report or the suggestion you would like to submit:", true)
                ComposerActivity(this, config) {
                    IssueReporter().report(it) { error ->
                        runOnUiThread {
                            if (error != null) { errorSending(error); return@runOnUiThread }
                            writeToScreen("Report successfully submitted")
                        }
                    }
                }.show()
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

    private fun errorSending(error: String) {
        println("Error sending composer result: $error")
        writeToScreen("The message could not be sent. Please check your connection and try again.")
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == GID_REQUEST_CODE && data != null) {
                val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val userEmailComponents = account?.email?.split("@")
                if (preferences == null || task == null || account == null || userEmailComponents == null) {
                    mGoogleSignInClient.signOut()
                    return
                } else if (allowedUserEmails.keys.contains(userEmailComponents[0]) && userEmailComponents[1].contains("syrdio") && !userEmailComponents[0].contains(".") && !userEmailComponents[0].matches(Regex(".*\\d.*"))) {
                    preferences.edit().putBoolean("userIsAnAdmin", true).apply()
                    preferences.edit().putString("adminSchool", allowedUserEmails[userEmailComponents[0]]).apply()
                    writeToScreen("Successfully signed in")
                } else if (userEmailComponents[1].contains("syrdio") && !userEmailComponents[0].contains(".") && !userEmailComponents[0].matches(Regex(".*\\d.*"))) {
                    preferences.edit().putBoolean("userIsATeacher", true).apply()
                    writeToScreen("Successfully signed in")
                } else {
                    print("${userEmailComponents[0]} is unauthorized")
                    preferences.edit().putBoolean("userIsATeacher", false).apply()
                    preferences.edit().putBoolean("userIsAnAdmin", false).apply()
                    writeToScreen("You must be a teacher or administrator to access these settings.")
                }
                updateUIForAuthentication()
                mGoogleSignInClient.signOut()
            } else if (requestCode == GID_REQUEST_CODE) mGoogleSignInClient.signOut()
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

    private fun updateUIForAuthentication() {

            val expandedParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val collapsedParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
            val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
            val userIsATeacher = preferences.getBoolean("userIsATeacher", false)
            val userIsAnAdmin = preferences.getBoolean("userIsAnAdmin", false)
            if (userIsATeacher || userIsAnAdmin) {
                signInButton?.text = "Sign Out"
                signInButton?.setOnClickListener {
                    preferences.edit().putBoolean("userIsATeacher", false).apply()
                    preferences.edit().putBoolean("userIsAnAdmin", false).apply()
                    updateUIForAuthentication()
                    writeToScreen("Successfully signed out")
                }
                adminSettingsHeader?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60.toPx())
                viewActivePassesCell?.layoutParams = expandedParams
                viewActivePassesCell?.setOnClickListener {
                    startActivity(Intent(baseContext, ActivePassesActivity::class.java))
                }
                sendNotificationCell?.layoutParams = if (userIsAnAdmin) expandedParams else collapsedParams
                if (userIsAnAdmin) sendNotificationCell?.setOnClickListener {
                    val config = ComposerConfiguration("Send", "Enter a message", false)

                    ComposerActivity(this, config) {
                        val school = preferences.getString("adminSchool", null)
                        val schoolInt = schoolSelectedMap[school]
                        if (schoolInt == null) { errorSending("Invalid school"); return@ComposerActivity }
                        PushNotificationSender().send(it, schoolInt!!) { error ->
                            runOnUiThread {
                                if (error != null) { errorSending(error); return@runOnUiThread }
                                writeToScreen("Report successfully submitted")
                            }
                        }
                    }.show()
                } else sendNotificationCell?.setOnClickListener(null)
            } else {
                signInButton?.text = "Sign In"
                signInButton?.setOnClickListener {
                    val signInIntent = mGoogleSignInClient.signInIntent
                    startActivityForResult(signInIntent, GID_REQUEST_CODE)
                }
                adminSettingsHeader?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
                viewActivePassesCell?.layoutParams = collapsedParams
                viewActivePassesCell?.setOnClickListener(null)
                sendNotificationCell?.layoutParams = collapsedParams
                sendNotificationCell?.setOnClickListener(null)
            }
        }

    private fun findViewsInContentLayout() {
            setonSwitch = findViewById(R.id.setonSwitch)
            johnSwitch = findViewById(R.id.johnSwitch)
            saintsSwitch = findViewById(R.id.saintsSwitch)
            jamesSwitch = findViewById(R.id.jamesSwitch)
            showAllSchoolsSwitch = findViewById(R.id.showAllSchoolsSwitch)
            deliverNotificationsSwitch = findViewById(R.id.deliverNotificationsSwitch)
            copyrightLabel = findViewById(R.id.copyrightLabel)
            versionLabel = findViewById(R.id.versionLabel)
            reportIssue = findViewById(R.id.reportIssue)

            adminSettingsHeader = findViewById(R.id.adminSettingsHeader)
            viewActivePassesCell = findViewById(R.id.viewActivePasses)
            sendNotificationCell = findViewById(R.id.sendNotification)
            signInButton = findViewById(R.id.submitButton)
        }

}
