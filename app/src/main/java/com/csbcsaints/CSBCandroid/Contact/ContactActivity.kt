package com.csbcsaints.CSBCandroid

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import androidx.constraintlayout.widget.ConstraintLayout
import com.csbcsaints.CSBCandroid.*
import java.util.*


//TODO - add parallax effect

class ContactActivity : CSBCAppCompatActivity() {

    val MAP_IMAGE_ARRAY = arrayOf(R.drawable.setonmap, R.drawable.saintsmap, R.drawable.saintsmap, R.drawable.jamesmap)
    val BUILDING_IMAGE_ARRAY = arrayOf(R.drawable.setonbuilding, R.drawable.johnbuilding, R.drawable.saintsbuilding, R.drawable.jamesbuilding)
    val SCHOOL_NAMES = arrayOf("Seton Catholic Central", "St. John the Evangelist", "All Saints School", "St. James School")
    val BEFORE_TIME = arrayOf(null, "Before School Care: From 7:00 AM", "Before School Care: From 7:00 AM", "Before School Care: From 7:00 AM")
    val START_TIME = arrayOf("Morning Bell: 8:13 AM", "Start: 8:30 AM", "Start: 8:20 AM", "Start 8:20 AM")
    val DISMISSAL_TIME = arrayOf("Dismissal: 3:00 PM", "Dismissal: 2:45 PM", "Dismissal: 2:45 PM", "Dismissal: 3:00 PM")
    val AFTER_TIME = arrayOf(null, "After School Care: Until 5:45 PM", "After School Care: Until 6:00 PM", "After School Care: Until 6:00 PM")
    val SCHOOL_COORDINATES = arrayOf("42.098485,-75.928579", "42.092430,-75.908342", "42.100491,-76.050103", "42.115512,-75.969542")

    var normalParams : LinearLayout.LayoutParams? = null
    var collapsedParams : LinearLayout.LayoutParams? = null

    var imageView : ImageView? = null

    var mapCell : LinearLayout? = null
    var mapIcon : ImageView? = null
    var mapTextView : TextView? = null
    var addressTextView : TextView? = null
    var cityStateTextView : TextView? = null

    var mainPhoneLayout : ConstraintLayout? = null
    var districtPhoneLayout : ConstraintLayout? = null
    var schoolFaxLayout : ConstraintLayout? = null
    var schoolMailLayout : ConstraintLayout? = null

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

        mapCell = findViewById(R.id.mapCell)
        mapIcon = findViewById(R.id.mapIcon)
        mapTextView = findViewById(R.id.mapTextView)
        mapTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
        addressTextView = findViewById(R.id.addressTextView)
        addressTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)


        copyrightLabel = findViewById(R.id.copyrightLabel)
        copyrightLabel?.text = "© ${Calendar.getInstance().time.yearString()} Catholic Schools of Broome County"

        mapCell?.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${SCHOOL_COORDINATES[schoolSelectedInt]}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            }
        }

        mainPhoneLayout = findViewById(R.id.mainPhone)
        mainPhoneLayout?.setOnClickListener {
            println("attempting to call tel:" + StaticData.readData("$schoolSelected/info/phone")?.replace(" ", "")?.replace("(", "")?.replace(")", ""))
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1" + StaticData.readData("$schoolSelected/info/phone")?.replace(" ", "")?.replace("(", "")?.replace(")", "")))
            startActivity(intent)

        }
        districtPhoneLayout = findViewById(R.id.districtPhone)
        districtPhoneLayout?.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1" + StaticData.readData("general/districtPhone")?.replace(" ", "")?.replace("(", "")?.replace(")", "")))
            startActivity(intent)
        }
        schoolFaxLayout = findViewById(R.id.schoolFax)
        schoolFaxLayout?.setOnClickListener{
            if (StaticData.readData("$schoolSelected/info/fax") != null) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:1" + StaticData.readData("$schoolSelected/info/fax")?.replace(" ", "")?.replace("(", "")?.replace(")", "")))
                startActivity(intent)
            }
        }
        schoolMailLayout = findViewById(R.id.schoolMail)
        schoolMailLayout?.setOnClickListener{
            val email = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + StaticData.readData("$schoolSelected/info/email")))
            startActivity(email)
        }

        mainPhoneIcon = findViewById(R.id.mainPhoneIcon)
        mainPhoneIcon?.setImageResource(R.drawable.phoneicon)
        mainPhoneTextView = findViewById(R.id.mainPhoneTextView)
        mainPhoneTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        districtPhoneIcon = findViewById(R.id.districtPhoneIcon)
        districtPhoneIcon?.setImageResource(R.drawable.phoneicon)
        districtPhoneTextView = findViewById(R.id.districtPhoneTextView)
        districtPhoneTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        faxIcon = findViewById(R.id.schoolFaxIcon)
        faxIcon?.setImageResource(R.drawable.faxicon)
        faxTextView = findViewById(R.id.schoolFaxTextView)
        faxTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.REGULAR)
        mailIcon = findViewById(R.id.schoolMailIcon)
        mailIcon?.setImageResource(R.drawable.mailicon)
        mailTextView = findViewById(R.id.schoolMailTextView)
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
        mapTextView?.text = SCHOOL_NAMES[schoolSelectedInt]
        addressTextView?.text = StaticData.readData("$schoolSelected/info/address")
        cityStateTextView?.text = ""

        mainPhoneTextView?.text = "Main: ${StaticData.readData("$schoolSelected/info/phone")}"
        districtPhoneTextView?.text = "District: ${StaticData.readData("general/districtPhone")}"
        faxTextView?.text = "Fax: ${StaticData.readData("$schoolSelected/info/fax") ?: "N/A"}"
        mailTextView?.text = "${StaticData.readData("$schoolSelected/info/principal")}, Principal"

        normalParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        collapsedParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)

        if (BEFORE_TIME[schoolSelectedInt] == null) {
            beforeLayout?.layoutParams = collapsedParams
            afterLayout?.layoutParams = collapsedParams
        } else {
            beforeSchoolTextView?.text = BEFORE_TIME[schoolSelectedInt]
            beforeLayout?.layoutParams = normalParams
            afterSchoolTextView?.text = AFTER_TIME[schoolSelectedInt]
            afterLayout?.layoutParams = normalParams
        }
        startSchoolTextView?.text = START_TIME[schoolSelectedInt]
        dismissalSchoolTextView?.text = DISMISSAL_TIME[schoolSelectedInt]

    }


}
