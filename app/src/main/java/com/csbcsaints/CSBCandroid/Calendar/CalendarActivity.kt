package com.csbcsaints.CSBCandroid

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.csbcsaints.CSBCandroid.Calendar.CalendarAdapter
import com.csbcsaints.CSBCandroid.ui.*
import okhttp3.*
import java.io.IOException

//TODO - Add search function, add school filter

class CalendarActivity : CSBCAppCompatActivity() {
    var listView : ListView? = null
    var swipeRefreshLayout : SwipeRefreshLayout? = null
    var loadingSymbol : ProgressBar? = null

    var sharedPreferences3 : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        supportActionBar?.title = "Calendar"

        loadingSymbol = findViewById(R.id.loadingSymbol)
        listView = findViewById(R.id.listView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        sharedPreferences3 = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

        loadingSymbol?.visibility = View.VISIBLE
        swipeRefreshLayout?.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        swipeRefreshLayout?.setOnRefreshListener {
            swipeRefreshLayout?.isRefreshing = true
            EventsRetriever().retrieveEventsArray(sharedPreferences3, false, true) {
                setupTable(it)
            }
        }

        EventsRetriever().retrieveEventsArray(sharedPreferences3, false, false) {
            setupTable(it)
        }
    }


    //MARK - Table methods
    fun setupTable(eventsArray : Array<EventsModel?>) {
        val adapter = CalendarAdapter(this)
        if (!eventsArray.contains(EventsModel("", "", "", "", "", ""))) {
            for (event in eventsArray) {
                adapter.addItem(event!!)
            }
        } else {
            adapter.addSectionHeaderItem("There are no more events this month")
        }
        adapter.addSectionHeaderItem("View More >")

        runOnUiThread(object:Runnable {
            override fun run() {
                listView?.adapter = adapter
                loadingSymbol?.visibility = View.INVISIBLE
                swipeRefreshLayout?.isRefreshing = false
                swipeRefreshLayout?.isEnabled = true
            }
        })
    }
}




