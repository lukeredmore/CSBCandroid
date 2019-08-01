package com.csbcsaints.CSBCandroid

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import eu.amirs.JSON
import okhttp3.*
import java.io.IOException

//TODO - Add search function

class AthleticsActivity : CSBCAppCompatActivity() {

    private val client = OkHttpClient()
    var athleticsData = AthleticsDataParser()
    var listView : ListView? = null
    var athleticsAdapter : AthleticsAdapter? = null
    var swipeRefreshLayout : SwipeRefreshLayout? = null
    var loadingSymbol : ProgressBar? = null

    var sharedPreferences3 : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_athletics)

        loadingSymbol = findViewById(R.id.loadingSymbol)
        athleticsAdapter = AthleticsAdapter(this)
        listView = findViewById(R.id.listView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        loadingSymbol?.visibility = View.VISIBLE
        swipeRefreshLayout?.setColorSchemeColors(getResources().getColor(R.color.colorAccent))
        swipeRefreshLayout?.setOnRefreshListener(object:SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                swipeRefreshLayout?.setRefreshing(true)
                getAthleticsData()
            }
        })
        sharedPreferences3 = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        getSupportActionBar()?.setTitle("Athletics")
        tryToBuildExistingData()
    }

    private fun tryToBuildExistingData() {
        swipeRefreshLayout?.setEnabled(false)
        val athleticsArray : Array<AthleticsModel?> = retrieveAthleticsArrayFromUserDefaults(sharedPreferences3!!)
        println(athleticsArray)
        if (athleticsArray.size > 1) {
            println("Athletics Data already exists and we can use it")
            athleticsData.athleticsModelArray = athleticsArray
            setupTable()
        } else {
            println("Fetching new athletics data")
            getAthleticsData()
        }
    }

    fun getAthleticsData() {
        println("We are asking for Athletics data")
        val request = Request.Builder()
            .url("https://www.schedulegalaxy.com/api/v1/schools/163/activities")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to ScheduleGalaxy: ")
                println(e)
                athleticsData.athleticsModelArray = retrieveAthleticsArrayFromUserDefaults(sharedPreferences3!!, true)
                setupTable()
            }
            override fun onResponse(call: Call, response: Response) {
                println("success")
                val json = JSON(response.body?.string())
                athleticsData.parseAthleticsData(json, sharedPreferences3!!)
                setupTable()
            }
        })
    }

    fun setupTable() {
        val adapter = AthleticsAdapter(this)

        if (!athleticsData.athleticsModelArray.isNullOrEmpty()) {
            for(dateWithEvents in athleticsData.athleticsModelArray) {
                if (dateWithEvents != null) {
                    adapter.addSectionHeaderItem(dateWithEvents.date)
                    for (event in 0 until dateWithEvents.title.size) {
                        adapter.addItem(dateWithEvents, event)
                    }
                }
            }
            runOnUiThread(object:Runnable {
                override fun run() {
                    listView?.adapter = adapter
                    loadingSymbol?.visibility = View.INVISIBLE
                    swipeRefreshLayout?.setRefreshing(false)
                    swipeRefreshLayout?.setEnabled(true)
                }
            })
        }
    }

}



