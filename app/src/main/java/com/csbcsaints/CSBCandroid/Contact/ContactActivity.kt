package com.csbcsaints.CSBCandroid

import android.app.ActionBar
import android.content.Context
import android.media.Image
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import android.R.id.message
import com.csbcsaints.CSBCandroid.ui.*
import java.net.URI
import java.util.*

//TODO - Add map link, add parallax effect

class ContactActivity : CSBCAppCompatActivity() { //Fragment() {

    val MAP_IMAGE_ARRAY = arrayOf(R.drawable.setonmap, R.drawable.saintsmap, R.drawable.saintsmap, R.drawable.jamesmap)
    val BUILDING_IMAGE_ARRAY = arrayOf(R.drawable.setonbuilding, R.drawable.johnbuilding, R.drawable.saintsbuilding, R.drawable.jamesbuilding)
    val SCHOOL_NAMES = arrayOf("Seton Catholic Central", "St. John the Evangelist", "All Saints School", "St. James School")
    val SCHOOL_ADDRESSES = arrayOf("70 Seminary Avenue", "9 Livingston Street", "1112 Broad Street", "143 Main Street")
    val SCHOOL_CITY_STATE = arrayOf("Binghamton, NY 13905", "Binghamton NY 13903", "Endicott NY 13760", "Johnson City NY 13790")
    val SCHOOL_PHONE : Array<String> = arrayOf("607.723.5307", "607.723.0703", "607.748.7423", "607.797.5444")
    val DISTRICT_PHONE = "607.723.1547"
    val SCHOOL_FAX = arrayOf("607.723.4811", "607.772.6210", "N/A", "N/A")
    val SCHOOL_PRINCIPALS = arrayOf("Matthew Martinkovic", "James Fountaine", "Angela Tierno-Sherwood", "Susan Kitchen")
    val PRINCIPAL_EMAILS = arrayOf("mmartinkovic","jfountaine","atierno","skitchen")
    val BEFORE_TIME = arrayOf(null, "Before School Care: From 7:00 AM", "Before School Care: From 7:00 AM", "Before School Care: From 7:00 AM")
    val START_TIME = arrayOf("Morning Bell: 8:13 AM", "Start: 8:30 AM", "Start: 8:20 AM", "Start 8:20 AM")
    val DISMISSAL_TIME = arrayOf("Dismissal: 3:00 PM", "Dismissal: 2:45 PM", "Dismissal: 2:45 PM", "Dismissal: 3:00 PM")
    val AFTER_TIME = arrayOf(null, "After School Care: Until 5:45 PM", "After School Care: Until 6:00 PM", "After School Care: Until 6:00 PM")

    var normalParams : LinearLayout.LayoutParams? = null
    var collapsedParams : LinearLayout.LayoutParams? = null

    var imageView : ImageView? = null

    var mapIcon : ImageView? = null
    var mapTextView : TextView? = null
    var addressTextView : TextView? = null
    var cityStateTextView : TextView? = null

    var mainPhoneLayout : LinearLayout? = null
    var districtPhoneLayout : LinearLayout? = null
    var schoolFaxLayout : LinearLayout? = null
    var schoolMailLayout : LinearLayout? = null

    var mainPhoneIcon : ImageView? = null
    var mainPhoneTextView : TextView? = null
    var districtPhoneIcon : ImageView? = null
    var districtPhoneTextView : TextView? = null
    var faxIcon : ImageView? = null
    var faxTextView : TextView? = null
    var mailIcon : ImageView? = null
    var mailTextView : TextView? = null
    var copyrightLabel : TextView? = null

    var beforeLayout : LinearLayout? = null
    var afterLayout : LinearLayout? = null

    var beforeSchoolTextView : TextView? = null
    var startSchoolTextView : TextView? = null
    var dismissalSchoolTextView : TextView? = null
    var afterSchoolTextView : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        imageView = findViewById(R.id.imageView)

        mapIcon = findViewById(R.id.mapIcon)
        mapTextView = findViewById(R.id.mapTextView)
        mapTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
        addressTextView = findViewById(R.id.addressTextView)
        addressTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        cityStateTextView = findViewById(R.id.cityStateTextView)
        cityStateTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)


        copyrightLabel = findViewById(R.id.copyrightLabel)
        copyrightLabel?.text = "© ${Calendar.getInstance().time.yearString()} Catholic Schools of Broome County"

        mainPhoneLayout = findViewById(R.id.mainPhone)
        mainPhoneLayout?.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + SCHOOL_PHONE[schoolSelectedInt].replace(".", "")))
            startActivity(intent)
        })
        districtPhoneLayout = findViewById(R.id.districtPhone)
        districtPhoneLayout?.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + DISTRICT_PHONE.replace(".", "")))
            startActivity(intent)
        })
        schoolFaxLayout = findViewById(R.id.schoolFax)
        schoolFaxLayout?.setOnClickListener(View.OnClickListener {
            if (SCHOOL_FAX[schoolSelectedInt] != "N/A") {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + SCHOOL_FAX[schoolSelectedInt].replace(".", "")))
                startActivity(intent)
            }
        })
        schoolMailLayout = findViewById(R.id.schoolMail)
        schoolMailLayout?.setOnClickListener(View.OnClickListener {
            val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + PRINCIPAL_EMAILS[schoolSelectedInt]))
            //email.putExtra(Intent.EXTRA_EMAIL, )
            startActivity(email)
        })

        mainPhoneIcon = findViewById(R.id.mainPhoneIcon)
        mainPhoneIcon?.setImageResource(R.drawable.phoneicon)
        mainPhoneTextView = findViewById(R.id.mainPhoneTextView)
        mainPhoneTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        districtPhoneIcon = findViewById(R.id.districtPhoneIcon)
        districtPhoneIcon?.setImageResource(R.drawable.phoneicon)
        districtPhoneTextView = findViewById(R.id.districtPhoneTextView)
        districtPhoneTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        faxIcon = findViewById(R.id.faxIcon)
        faxIcon?.setImageResource(R.drawable.faxicon)
        faxTextView = findViewById(R.id.faxTextView)
        faxTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        mailIcon = findViewById(R.id.mailIcon)
        mailIcon?.setImageResource(R.drawable.mailicon)
        mailTextView = findViewById(R.id.mailTextView)
        mailTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)

        beforeLayout = findViewById(R.id.beforeCell)
        afterLayout = findViewById(R.id.afterCell)

        beforeSchoolTextView = findViewById(R.id.beforeSchoolTextView)
        beforeSchoolTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        startSchoolTextView = findViewById(R.id.startSchoolTextView)
        startSchoolTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        dismissalSchoolTextView = findViewById(R.id.dismissalSchoolTextView)
        dismissalSchoolTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        afterSchoolTextView = findViewById(R.id.afterSchoolTextView)
        afterSchoolTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)


        setupViewForTabbedActivity(R.layout.activity_contact)
    }

    override fun tabSelectedHandler() {
        imageView?.setImageResource(BUILDING_IMAGE_ARRAY[schoolSelectedInt])

        mapIcon?.setImageResource(MAP_IMAGE_ARRAY[schoolSelectedInt])
        mapTextView?.setText(SCHOOL_NAMES[schoolSelectedInt])
        addressTextView?.setText(SCHOOL_ADDRESSES[schoolSelectedInt])
        cityStateTextView?.setText(SCHOOL_CITY_STATE[schoolSelectedInt])

        mainPhoneTextView?.setText("Main: " + SCHOOL_PHONE[schoolSelectedInt])
        districtPhoneTextView?.setText("District: " + DISTRICT_PHONE)
        faxTextView?.setText("Fax: " + SCHOOL_FAX[schoolSelectedInt])
        mailTextView?.setText(SCHOOL_PRINCIPALS[schoolSelectedInt] + ", Principal")

        normalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        collapsedParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)

        if (BEFORE_TIME[schoolSelectedInt] == null) {
            beforeLayout?.setLayoutParams(collapsedParams)
            afterLayout?.setLayoutParams(collapsedParams)

//            beforeLayout?.updateViewLayout(beforeLayout, collapsedParams)
//            afterLayout?.updateViewLayout(afterLayout, collapsedParams)
        } else {
            beforeSchoolTextView?.setText(BEFORE_TIME[schoolSelectedInt])
            beforeLayout?.setLayoutParams(normalParams)
            afterSchoolTextView?.setText(AFTER_TIME[schoolSelectedInt])
            afterLayout?.setLayoutParams(normalParams)
//            beforeLayout?.setLayoutParams(normalParams)
//            afterLayout?.setLayoutParams(normalParams)
//
//            afterSchoolTextView?.setText(AFTER_TIME[schoolSelectedInt])

        }
        startSchoolTextView?.setText(START_TIME[schoolSelectedInt])
        dismissalSchoolTextView?.setText(DISMISSAL_TIME[schoolSelectedInt])

    }


}
