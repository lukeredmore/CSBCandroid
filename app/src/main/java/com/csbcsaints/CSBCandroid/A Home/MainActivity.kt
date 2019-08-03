package com.csbcsaints.CSBCandroid

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.res.ResourcesCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.csbcsaints.CSBCandroid.ui.dateString
import com.csbcsaints.CSBCandroid.ui.toPx
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import java.util.*
import com.csbcsaints.CSBCandroid.AlertController
import kotlin.collections.ArrayList
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity

//TODO - add launch screen works, start downloading lunch menus, consolidate intents

class MainActivity: CSBCAppCompatActivity() {
    companion object {
        const val START_CALENDAR_ACTIVITY_REQUEST_CODE = 0
        const val START_ATHLETICS_ACTIVITY_REQUEST_CODE = 1
        const val START_TODAY_ACTIVITY_REQUEST_CODE = 2
    }
    var myAdapter: MainIconGridAdapter? = null
    var iconsList = ArrayList<Icon>()
    val iconTitles = arrayOf("Today", "Portal","Contact","Calendar","News","Lunch","Athletics","Give","Connect","Dress Code","Docs","Options")
    val iconImages = arrayOf(R.drawable.today,R.drawable.portal,R.drawable.contact,R.drawable.calendar,R.drawable.news,R.drawable.lunch,R.drawable.athletics,R.drawable.give,R.drawable.connect,R.drawable.dresscode,R.drawable.docs,R.drawable.options)
    val urlMap = mapOf(1 to "https://www.plusportals.com/setoncchs", 4 to "https://csbcsaints.org/news", 7 to "https://app.mobilecause.com/form/N-9Y0w?vid=1hepr")

    var localAlerts : com.csbcsaints.CSBCandroid.AlertController? = null
    var notificationController : NotificationController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        println("\nVersion ${BuildConfig.VERSION_NAME} has loaded in the ${BuildConfig.BUILD_TYPE} configuration.\n")

        setupNotifications()

        val html = HTMLController()
        html.downloadAndStoreLunchMenus(this)

        localAlerts = com.csbcsaints.CSBCandroid.AlertController(this)

        setupGridView()


    }

    override fun onStart() {
        super.onStart()
        localAlerts?.checkForAlert()
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
                println("Device token: " + token)

                notificationController = NotificationController(this)
                notificationController?.subscribeToTopics()
                notificationController?.queueNotifications()
            })
    }


    fun reinitNotifications() {
        print("reinitializing Notifications")
        notificationController = null
        notificationController = NotificationController(this)
        notificationController?.subscribeToTopics()
        notificationController?.queueNotifications()
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

    private fun setupGridView() {
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
    }

    private fun performSegue(withPosition : Int) {
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

    private fun showWebPage(withURL : String?) {
        val builder : CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null))
        val customTabsIntent : CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(withURL!!))
    }
}