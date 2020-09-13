package com.csbcsaints.CSBCandroid

import android.app.AlarmManager
import android.app.NotificationManager
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.csbcsaints.CSBCandroid.Lunch.LunchMenuRetriever
import com.csbcsaints.CSBCandroid.Options.OptionsActivity
import com.google.firebase.FirebaseApp
import kotlin.collections.ArrayList
import com.csbcsaints.CSBCandroid.Covid.CovidCheckInActivity
import com.csbcsaints.CSBCandroid.Covid.CovidNotificationController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

//TODO - add launch screen works, fix spacing of icons

class MainActivity: CSBCAppCompatActivity() {
    private var myAdapter: MainIconGridAdapter? = null
    private var iconsList = ArrayList<Icon>()
    private val iconTitles = arrayOf("Today", "Portal","Contact","Calendar","News","Lunch","Athletics","Give","Connect","Dress Code","Docs","Options")
    private val iconImages = arrayOf(R.drawable.today,R.drawable.portal,R.drawable.contact,R.drawable.calendar,R.drawable.news,R.drawable.lunch,R.drawable.athletics,R.drawable.give,R.drawable.connect,R.drawable.dresscode,R.drawable.docs,R.drawable.options)
    private val urlMap = mapOf(1 to "https://www.plusportals.com/setoncchs", 4 to "https://csbcsaints.org/news", 7 to "https://app.mobilecause.com/form/N-9Y0w?vid=1hepr")

    private var notificationController : NotificationController? = null
    private var alertController : AlertController? = null

    private var yellowBar : View? = null

    //MARK - Activity Control
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("\nVersion ${BuildConfig.VERSION_NAME} has loaded in the ${BuildConfig.BUILD_TYPE} configuration.\n")

        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        setupGridView()

        FirebaseApp.initializeApp(this)
        notificationController = NotificationController(this)
        notificationController?.setupNotifications()
        LunchMenuRetriever().downloadLunchMenus(this)
        alertController = AlertController(this)


        StaticData.getDataFromFirebase() {
            setupGridView()
        }
    }
    override fun onStart() {
        super.onStart()
        alertController?.checkForAlert()
    }


    //MARK - UI methods
    fun showBannerAlert(withMessage : String) {
        val expandedParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        val alertLabel = findViewById<TextView>(R.id.alertLabel)
        val header = findViewById<View>(R.id.header)

        // change status bar color
        val window : Window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.csbcAlertRed)

        alertLabel.text = withMessage
        alertLabel.layoutParams = expandedParams

        val headerLayoutParams = header.layoutParams as ConstraintLayout.LayoutParams
        headerLayoutParams.topMargin = 10.toPx()
        header.layoutParams = headerLayoutParams
    }
    fun removeBannerAlert() {

        val collapsedParams = ConstraintLayout.LayoutParams(0, 0)
        val alertLabel = findViewById<TextView>(R.id.alertLabel)
        val header = findViewById<View>(R.id.header)

        alertLabel.text = ""
        alertLabel.layoutParams = collapsedParams

        val headerLayoutParams = header.layoutParams as ConstraintLayout.LayoutParams
        headerLayoutParams.topMargin = 0
        header.layoutParams = headerLayoutParams

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
    }
    private fun setupGridView() {
        for (i in iconTitles.indices) {
            iconsList.add(Icon(iconTitles[i], iconImages[i]))
        }
        myAdapter = MainIconGridAdapter(this, iconsList)
        iconGridView.adapter = myAdapter
        iconGridView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            println(position)
            val iconSelected = iconsList[position]
            iconSelected.announce()
            if(position != 1 && position != 4 && position != 7) {
                performSegue(position)
            } else if(position > -1 && position < 12) {
                showWebPage(urlMap[position])
            }

        }

        yellowBar = findViewById(R.id.bar)
        findViewById<TextView>(R.id.covidCheckInTextView)?.visibility = View.INVISIBLE
        yellowBar?.layoutParams?.height = 14.toPx()

        if (CovidCheckInActivity.showCovidCheckIn) {
            findViewById<TextView>(R.id.covidCheckInTextView)?.visibility = View.VISIBLE
            yellowBar?.layoutParams?.height = 58.toPx()
            yellowBar?.setOnClickListener {
                performSegue(12)
            }
        }
    }


    //MARK - Navigation
    private fun performSegue(withPosition : Int) {
        val destinationClass = when(withPosition + 1) {
            1 -> TodayActivity::class.java
            3 -> ContactActivity::class.java
            4 -> CalendarActivity::class.java
            6 -> LunchActivity::class.java
            7 -> AthleticsActivity::class.java
            9 -> ConnectActivity::class.java
            10 -> DressCodeActivity::class.java
            11 -> DocsActivity::class.java
            12 -> OptionsActivity::class.java
            13 -> CovidCheckInActivity::class.java
            else -> return
        }
        startActivity(Intent(baseContext, destinationClass))
    }
    private fun showWebPage(withURL : String?) {
        val builder : CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        val customTabsIntent : CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(withURL!!))
    }
}