//package com.csbcsaints.CSBCandroid
//
//import android.os.Bundle
//import android.os.Parcelable
//import com.google.android.material.tabs.TabLayout
//import androidx.viewpager.widget.ViewPager
//import androidx.appcompat.app.AppCompatActivity
//import android.widget.TextView
////import com.csbcsaints.CSBCandroid.ui.main.SchoolSelectorAdapter
//import android.R.layout
//import com.google.android.material.tabs.TabItem
//import android.view.View
//import android.widget.Toast
//
//
//class TabControlActivity : AppCompatActivity() {
//
//    var schoolSelected : String = ""
//    val activityDictionary = mapOf(1 to TodayActivity(), 2 to PortalActivity(), 3 to ContactActivity(), 6 to LunchActivity(), 9 to ConnectActivity(), 10 to DressCodeActivity(), 11 to DocsActivity())
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_tab_control)
//        val activityTag : Int = intent.getIntExtra("ActivityTag", 0)
//        val eventsParcelableArray : Array<Parcelable>? = intent.getParcelableArrayExtra("eventsArray")
//        val athleticsParcelableArray : Array<Parcelable>? = intent.getParcelableArrayExtra("atleticsArray")
//
//        schoolSelected = intent.getStringExtra("School")
//        var title = findViewById<TextView>(R.id.title)
//        when(activityTag) {
//            1 -> title.text = "Today"
//            2 -> title.text = "Portal"
//            3 -> title.text = "Contact"
//            6 -> title.text = "Lunch"
//            9 -> title.text = "Connect"
//            10 -> title.text = "Dress Code"
//            11 -> title.text = "Docs"
//            else -> title.text = "Activity not found"
//        }
//
//        //val sectionsPagerAdapter = SchoolSelectorAdapter(this, supportFragmentManager, activityTag, eventsParcelableArray, athleticsParcelableArray)
////        val viewPager: ViewPager = findViewById(R.id.view_pager)
////        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = findViewById(R.id.tabLayout)
//        tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab : TabLayout.Tab) {
//                println(tab.text)
//            }
//
//        })
//
////
////        tabs.setOnClickListener(
////            View.OnClickListener {
////                println("tab touched")
////            }
////        )
////        val setonTab = findViewById<TabItem>(R.id.setonTab)
////        setonTab.setOnClickListener(View.OnClickListener {
////            println("touched")
////        })
////        tabs.setupWithViewPager(viewPager)
//
//
//
//
//
//    }
//}