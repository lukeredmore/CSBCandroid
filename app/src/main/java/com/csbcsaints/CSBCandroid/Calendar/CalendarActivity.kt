package com.csbcsaints.CSBCandroid

import android.os.Bundle
import com.csbcsaints.CSBCandroid.Calendar.CalendarAdapter
import com.csbcsaints.CSBCandroid.Calendar.EventsModel
import com.csbcsaints.CSBCandroid.ui.*
import eu.amirs.JSON
import kotlinx.android.synthetic.main.activity_athletics.*
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException

//TODO - Add search function, add loading symbol!, add refresh!, fix pulling existing data!, add school filter, add view more button!

class CalendarActivity : CSBCAppCompatActivity() {

    private val client = OkHttpClient()
    var eventsData = EventsDataParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        getSupportActionBar()?.setTitle("Calendar")
        tryToBuildExistingData()
    }

    private fun tryToBuildExistingData() {
        val eventsArray: Array<EventsModel?> = retrieveEventsArrayFromUserDefaults()
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
                eventsData.eventsModelArray = retrieveEventsArrayFromUserDefaults(true)
                setupTable()
            }
            override fun onResponse(call: Call, response: Response) {
                println("Successfully received calendar data")
                val html = response.body?.string()
                println(html)
                if (html != null) {
                    eventsData.parseEventsData(html)
                } else {
                    eventsData.eventsModelArray = retrieveEventsArrayFromUserDefaults(true)
                }
                setupTable()

            }
        })
    }

    fun setupTable() {
        val adapter = CalendarAdapter(this)
        var x = 0
        for (event in eventsData.eventsModelArray) {
            adapter.addItem(event!!)
        }

        runOnUiThread(object:Runnable {
            public override fun run() {
                listView.adapter = adapter
            }
        })
    }

}




