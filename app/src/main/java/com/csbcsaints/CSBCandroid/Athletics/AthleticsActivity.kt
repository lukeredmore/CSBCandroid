package com.csbcsaints.CSBCandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import eu.amirs.JSON
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException

//TODO - Add search function, add loading symbol!, add refresh!, fix pulling existing data!

class AthleticsActivity : CSBCAppCompatActivity() {

    private val client = OkHttpClient()
    var athleticsData = AthleticsDataParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_athletics)
        getSupportActionBar()?.setTitle("Athletics")
        tryToBuildExistingData()
    }

    private fun tryToBuildExistingData() {
        val athleticsArray : Array<AthleticsModel?> = retrieveAthleticsArrayFromUserDefaults()
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
                athleticsData.athleticsModelArray = retrieveAthleticsArrayFromUserDefaults(true)
                setupTable()
            }
            override fun onResponse(call: Call, response: Response) {
                println("success")
                val json = JSON(response.body?.string())
                athleticsData.parseAthleticsData(json)
                setupTable()
            }
        })
    }

    fun setupTable() {
        if (!athleticsData.athleticsModelArray.isNullOrEmpty()) {
            val listView : ListView = findViewById(R.id.listView)

            val athleticsAdapter = AthleticsAdapter(this)
            for(dateWithEvents in athleticsData.athleticsModelArray) {
                if (dateWithEvents != null) {
                    athleticsAdapter.addSectionHeaderItem(dateWithEvents!!.date)
                    for (event in 0 until dateWithEvents!!.sport.size) {
                        athleticsAdapter.addItem(dateWithEvents!!, event)
                    }
                }
            }
            runOnUiThread(object:Runnable {
                override fun run() {
                    listView.adapter = athleticsAdapter
                }
            })
        }
    }

}



