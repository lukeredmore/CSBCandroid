package com.csbcsaints.CSBCandroid

import android.app.ActionBar
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Parcelable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.csbcsaints.CSBCandroid.ui.dateString
import com.csbcsaints.CSBCandroid.ui.toPx
import com.csbcsaints.CSBCandroid.ui.write
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*
import kotlin.collections.ArrayList

//TODO - Fix launch screen, check for alerts from WBNG and display them, start downloading lunch menus

class MainActivity: AppCompatActivity() {

    companion object {
        const val START_CALENDAR_ACTIVITY_REQUEST_CODE = 0
        const val START_ATHLETICS_ACTIVITY_REQUEST_CODE = 1
        const val START_TODAY_ACTIVITY_REQUEST_CODE = 2
        const val START_OPTIONS_ACTIVITY_REQUEST_CODE = 3
        const val START_TAB_ACTIVITY_REQUEST_CODE = 4
    }
    var eventsParcelableArray : Array<Parcelable>? = null
    var athleticsParcelableArray : Array<Parcelable>? = null
    var myAdapter: MainIconGridAdapter? = null
    var iconsList = ArrayList<Icon>()
    val iconTitles = arrayOf("Today", "Portal","Contact","Calendar","News","Lunch","Athletics","Give","Connect","Dress Code","Docs","Options")
    val iconImages = arrayOf(R.drawable.today,R.drawable.portal,R.drawable.contact,R.drawable.calendar,R.drawable.news,R.drawable.lunch,R.drawable.athletics,R.drawable.give,R.drawable.connect,R.drawable.dresscode,R.drawable.docs,R.drawable.options)
    var schoolSelected = "Seton"
    val urlMap = mapOf(1 to "https://www.plusportals.com/setoncchs", 4 to "https://csbcsaints.org/news", 7 to "https://app.mobilecause.com/form/fi0kKA?vid=hf0o")
    var shouldSnowDatesReinit = false
    var shouldOverridesReinit = false
    var snowDatesChecked = false
    var dayOverridesChecked = false
    var notificationController : NotificationController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()

        removeBannerAlert()

        setupNotifications()

        val html = HTMLController()
        html.downloadAndStoreLunchMenus(this)

        for (i in 0 until iconTitles.size) {
            iconsList.add(Icon(iconTitles[i], iconImages[i]))
        }
        myAdapter = MainIconGridAdapter(this, iconsList)
        iconGridView.adapter = myAdapter
        iconGridView.setOnItemClickListener(OnItemClickListener { parent, v, position, id ->
            println(position)
            val iconSelected = iconsList[position]
            iconSelected.announce()
            if(position != 1 && position != 4 && position != 7) {
                performSegue(position)
            } else if(position > -1 && position < 12) {
                showWebPage(urlMap[position])
            }

        })

        getSnowDatesAndOverridesAndQueueNotifications()


    }
    fun setupNotifications() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    //Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token!!

                // Log and toast
                write(token)
                println("Device token: " + token)

                notificationController = NotificationController(this)
                notificationController?.subscribeToTopics()
                notificationController?.queueNotifications()
            })
    }

    fun getSnowDatesAndOverridesAndQueueNotifications() {
        //var snowDays : Set<String> = setOf()
        FirebaseDatabase.getInstance().reference.child("SnowDays")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

                    val myMap: Map<String, String>? = p0.value as? Map<String, String>
                    val myList: Collection<String>? = myMap?.values
                    val newSnowDays = myList?.toSet()
                    val ogSnowDays = preferences.getStringSet("snowDays", null)
                    println("Existing Snow Days: $ogSnowDays")
                    println("Firebase Snow Days: $newSnowDays")
                    if (newSnowDays != null) {
                        if (newSnowDays.contains(Calendar.getInstance().time.dateString())) {
                            showBannerAlert("The Catholic Schools of Broome County are closed today")
                        } else {
                            removeBannerAlert()
                        }
                        if (ogSnowDays != null) {
                            if (newSnowDays != ogSnowDays) {
                                println("Saving Firebase snow days and reinitializing")
                                preferences.edit().putStringSet("snowDays", newSnowDays).apply()
                                shouldSnowDatesReinit = true
                                tryToReinit()
                            } else {
                                println("They are equal, no need to reinit")
                            }
                        } else {
                            println("Saving Firebase snow days and reinitializing")
                            preferences.edit().putStringSet("snowDays", newSnowDays).apply()
                            shouldSnowDatesReinit = true
                            tryToReinit()
                        }
                        snowDatesChecked = true
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError)
                }

            })
        FirebaseDatabase.getInstance().reference.child("DayScheduleOverrides")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val preferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
                    val newOverrides: Map<String, Int>? = p0.value as? Map<String, Int>
                    val ogOverrides = mapOf(
                        "Seton" to preferences.getInt("SetonOverrides", 0),
                        "John" to preferences.getInt("JohnOverrides", 0),
                        "Saints" to preferences.getInt("SaintsOverrides", 0),
                        "James" to preferences.getInt("JamesOverrides", 0)
                    )
                    if (!newOverrides.isNullOrEmpty() && !ogOverrides.isNullOrEmpty()) {
                        if (newOverrides!! != ogOverrides) {
                            println("Saving Firebase overrides and reinitializing")
                            preferences.edit().putInt("SetonOverrides", newOverrides["Seton"]!!).apply()
                            preferences.edit().putInt("JohnOverrides", newOverrides["John"]!!).apply()
                            preferences.edit().putInt("SaintsOverrides", newOverrides["Saints"]!!).apply()
                            preferences.edit().putInt("JamesOverrides", newOverrides["James"]!!).apply()
                            shouldOverridesReinit = true
                            tryToReinit()
                        } else {
                            println("They are equal, no need to reinit")
                        }
                    } else if (newOverrides != null) {
                        println("Saving Firebase overrides and reinitializing")
                        preferences.edit().putInt("SetonOverrides", newOverrides["Seton"]!!).apply()
                        preferences.edit().putInt("JohnOverrides", newOverrides["John"]!!).apply()
                        preferences.edit().putInt("SaintsOverrides", newOverrides["Saints"]!!).apply()
                        preferences.edit().putInt("JamesOverrides", newOverrides["James"]!!).apply()
                        shouldOverridesReinit = true
                        tryToReinit()
                    }
                    dayOverridesChecked = true
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println(databaseError)
                }

            })
    }
    fun tryToReinit() {
        if (snowDatesChecked && dayOverridesChecked && (shouldSnowDatesReinit || shouldOverridesReinit)) {
            print("reinitializing Notifications")
            notificationController = null
            notificationController = NotificationController(this)
            notificationController?.subscribeToTopics()
            notificationController?.queueNotifications()
        }
    }

    fun showBannerAlert(withMessage : String) {
        val expandedParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val alertLabel = findViewById<TextView>(R.id.alertLabel)
        val header = findViewById<View>(R.id.header)

        // change status bar color
        val window : Window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.csbcAlertRed))

        alertLabel.text = withMessage
        alertLabel.layoutParams = expandedParams

        val headerLayoutParams = header.getLayoutParams() as ConstraintLayout.LayoutParams
        headerLayoutParams.topMargin = 10.toPx()
        header.setLayoutParams(headerLayoutParams)
    }
    fun removeBannerAlert() {

        val collapsedParams = ConstraintLayout.LayoutParams(0, 0)
        val alertLabel = findViewById<TextView>(R.id.alertLabel)
        val header = findViewById<View>(R.id.header)

        alertLabel.text = ""
        alertLabel.layoutParams = collapsedParams

        val headerLayoutParams = header.getLayoutParams() as ConstraintLayout.LayoutParams
        headerLayoutParams.topMargin = 0
        header.setLayoutParams(headerLayoutParams)

        val window : Window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }


        fun performSegue(withPosition : Int) {
            val tag = withPosition + 1
            when(tag) {
                1 -> {
                    val intent = Intent(baseContext, TodayActivity::class.java)
                    startActivityForResult(intent, START_TODAY_ACTIVITY_REQUEST_CODE)
                }
                3 -> {
                    val intent = Intent(baseContext, ContactActivity::class.java)
                    startActivity(intent)
                }
                4 -> {
                    val intent = Intent(baseContext, CalendarActivity::class.java)
                    startActivityForResult(intent, START_CALENDAR_ACTIVITY_REQUEST_CODE)
                }
                6 -> {
                    val intent = Intent(baseContext, LunchActivity::class.java)
                    startActivity(intent)
                }
                7 -> {
                    val intent = Intent(baseContext, AthleticsActivity::class.java)
                    startActivityForResult(intent, START_ATHLETICS_ACTIVITY_REQUEST_CODE)
                }
                9 -> {
                    val intent = Intent(baseContext, ConnectActivity::class.java)
                    startActivity(intent)
                }
                10 -> {
                    val intent = Intent(baseContext, DressCodeActivity::class.java)
                    startActivity(intent)
                }
                11 -> {
                    val intent = Intent(baseContext, DocsActivity::class.java)
                    startActivity(intent)
                }
                12 -> {
                    val intent = Intent(baseContext, OptionsActivity::class.java)
                    startActivity(intent)
                }
                else -> return
            }
        }
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            println("did tihis aeven come back")
            println(requestCode)
            if (requestCode == Activity.RESULT_OK) {
                println("and this")
                if (requestCode == START_ATHLETICS_ACTIVITY_REQUEST_CODE) {
                    athleticsParcelableArray = data!!.getParcelableArrayExtra("athleticsArray")
                    println("athleticsArray stored")
                }
                if (requestCode == START_CALENDAR_ACTIVITY_REQUEST_CODE) {
                    eventsParcelableArray = data!!.getParcelableArrayExtra("eventsArray")
                    println("eventsArray stored")
                } //else  {
                else {
                    super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }

        fun showWebPage(withURL : String?) {
            val builder : CustomTabsIntent.Builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
            val customTabsIntent : CustomTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(withURL!!))
        }
    }






