package com.csbcsaints.CSBCandroid

import android.app.ActionBar
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.csbcsaints.CSBCandroid.Calendar.CalendarAdapter
import com.csbcsaints.CSBCandroid.ui.*
import okhttp3.*
import java.io.IOException
import java.util.*

//TODO - Add search function, add school filter
enum class CSBCListDataType {
    ///Data is a placeholder for now, expect later complete
    DUMMY,
    ///Data returned is fully up-to-date, and nothing further will be returned
    COMPLETE
}
class CalendarActivity : CSBCAppCompatActivity() {
    private val eventsRetriever : EventsRetriever by lazy {
        EventsRetriever(sharedPreferences3, ::setupTable)
    }

        private var loadingSymbolActionItem : ProgressBar? = null

        var listView : ListView? = null
        var swipeRefreshLayout : SwipeRefreshLayout? = null
        var loadingSymbol : ProgressBar? = null

        private var sharedPreferences3 : SharedPreferences? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_calendar)

            //MARK - Define Action Bar elements
            val inflator = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customActionBar = inflator.inflate(R.layout.action_bar_with_loading_symbol, null)
            val titleTextView = customActionBar?.findViewById<TextView>(R.id.textView)
            loadingSymbolActionItem = customActionBar?.findViewById(R.id.progressBar)
            titleTextView?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.SEMIBOLD)
            titleTextView?.text = "Calendar"

            loadingSymbolActionItem?.visibility = View.INVISIBLE


            supportActionBar?.customView = customActionBar
            supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

            loadingSymbol = findViewById(R.id.loadingSymbol)
            listView = findViewById(R.id.listView)
            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)

            sharedPreferences3 = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)

            loadingSymbol?.visibility = View.VISIBLE
            swipeRefreshLayout?.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorAccent))
            swipeRefreshLayout?.setOnRefreshListener {
                swipeRefreshLayout?.isRefreshing = true
                eventsRetriever.retrieveEventsArray(false, true)
            }
            eventsRetriever.retrieveEventsArray()
        }


        //MARK - Table methods
        private fun setupTable(eventsSet : Set<EventsModel>, ofType : CSBCListDataType) {
            val adapter = CalendarAdapter(this)
            val eventsArray = eventsSet.sortedBy { it.date }
            if (eventsArray.count() > 0) {
                for (event in eventsArray) {
                    adapter.addItem(event)
                }
            } else {
                adapter.addSectionHeaderItem("There are no more events this month")
            }
            adapter.addSectionHeaderItem("View More >")



            runOnUiThread {
                when (ofType) {
                    CSBCListDataType.DUMMY -> loadingSymbolActionItem?.visibility = View.VISIBLE
                    CSBCListDataType.COMPLETE -> loadingSymbolActionItem?.visibility = View.INVISIBLE
                }
                listView?.adapter = adapter
                loadingSymbol?.visibility = View.INVISIBLE
                swipeRefreshLayout?.isRefreshing = false
                swipeRefreshLayout?.isEnabled = true
            }
        }
    }






