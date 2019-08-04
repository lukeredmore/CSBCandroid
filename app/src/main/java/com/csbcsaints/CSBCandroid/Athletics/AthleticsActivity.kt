package com.csbcsaints.CSBCandroid

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import eu.amirs.JSON
import okhttp3.*
import java.io.IOException

//TODO - Add search function

///Initial asker for athletics data and sets up table once it gets it
class AthleticsActivity : CSBCAppCompatActivity() {
    var listView : ListView? = null
    private var athleticsAdapter : AthleticsAdapter? = null
    var swipeRefreshLayout : SwipeRefreshLayout? = null
    var loadingSymbol : ProgressBar? = null

    var sharedPreferences3 : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_athletics)
        supportActionBar?.title = "Athletics"

        loadingSymbol = findViewById(R.id.loadingSymbol)
        athleticsAdapter = AthleticsAdapter(this)
        listView = findViewById(R.id.listView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

        sharedPreferences3 = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        loadingSymbol?.visibility = View.VISIBLE
        swipeRefreshLayout?.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
        swipeRefreshLayout?.setOnRefreshListener(object:SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                swipeRefreshLayout?.setRefreshing(true)
                AthleticsRetriever().retrieveAthleticsArray(sharedPreferences3!!, false, true) {
                    setupTable(it)
                }
            }
        })
        swipeRefreshLayout?.isEnabled = false
        AthleticsRetriever().retrieveAthleticsArray(sharedPreferences3!!, false, false) {
            setupTable(it)
        }
    }


    //MARK - Table methods
    private fun setupTable(athleticsModelArray: Array<AthleticsModel?>) {
        val adapter = AthleticsAdapter(this)

        if (!athleticsModelArray.isNullOrEmpty()) {
            for(dateWithEvents in athleticsModelArray) {
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



