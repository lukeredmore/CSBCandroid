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
import com.csbcsaints.CSBCandroid.Calendar.EventsModel
import com.csbcsaints.CSBCandroid.ui.*
import okhttp3.*
import java.io.IOException

//TODO - Add search function, add school filter

class CalendarActivity : CSBCAppCompatActivity() {
    private val client = OkHttpClient()
    var eventsData = EventsDataParser()
    var listView : ListView? = null
    var swipeRefreshLayout : SwipeRefreshLayout? = null
    var loadingSymbol : ProgressBar? = null

    var sharedPreferences3 : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        loadingSymbol = findViewById(R.id.loadingSymbol)
        listView = findViewById(R.id.listView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        sharedPreferences3 = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

        loadingSymbol?.visibility = View.VISIBLE
        swipeRefreshLayout?.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        swipeRefreshLayout?.setOnRefreshListener(object:SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                swipeRefreshLayout?.setRefreshing(true)
                getEventsData()
            }
        })

        supportActionBar?.title = "Calendar"
        tryToBuildExistingData()
    }


    //MARK - Data methods
    private fun tryToBuildExistingData() {
        swipeRefreshLayout?.setEnabled(false)
        val eventsArray: Array<EventsModel?> = retrieveEventsArrayFromUserDefaults(sharedPreferences3!!)
        if (eventsArray.size > 1) {
            println("Events Data already exists and we can use it")
            eventsData.eventsModelArray = eventsArray
            setupTable()
        } else {
            println("Fetching new events data")
            getEventsData()
        }
    }
    fun getEventsData() {
        println("We are asking for Events data")
        val request = Request.Builder()
            .url("https://csbcsaints.org/calendar/")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to CSBCSaints.org: ")
                println(e)
                eventsData.eventsModelArray = retrieveEventsArrayFromUserDefaults(sharedPreferences3!!, true)
                setupTable()
            }
            override fun onResponse(call: Call, response: Response) {
                println("Successfully received calendar data")
                val html = response.body?.string()
                if (html != null) {
                    eventsData.parseEventsData(html, sharedPreferences3!!)
                } else {
                    eventsData.eventsModelArray = retrieveEventsArrayFromUserDefaults(sharedPreferences3!!, true)
                }
                setupTable()

            }
        })
    }


    //MARK - Table methods
    fun setupTable() {
        val adapter = CalendarAdapter(this)
        if (!eventsData.eventsModelArray.contains(EventsModel("", "", "", "", "", ""))) {
            for (event in eventsData.eventsModelArray) {
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




