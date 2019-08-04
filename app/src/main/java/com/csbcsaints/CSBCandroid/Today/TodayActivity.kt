package com.csbcsaints.CSBCandroid

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.csbcsaints.CSBCandroid.ui.*
import java.text.SimpleDateFormat
import java.util.*

//TODO: Swipe gesture, make sure correct events show up for date (events)!

class TodayActivity : CSBCAppCompatActivity() {
    var listView : ListView? = null
    var daySchedule: DaySchedule? = null
    var dayIndicatorLabel : TextView? = null
    var eventsSeparator : TextView? = null
    var athleticsSeparator : TextView? = null
    var scrollLayout : LinearLayout? = null
    var dateChangerButton : TextView? = null
    var activityTitle : TextView? = null
    var loadingSymbol : ProgressBar? = null

    var todayParser : TodayDataParser? = null

    val cellParams : LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    var dateString = ""

    var sharedPreferences4 : SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today)

        activityTitle = findViewById(R.id.activityTitle)
        dayIndicatorLabel = findViewById(R.id.dayIndicatorLabel)
        dayIndicatorLabel?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.BOLD)
        eventsSeparator = findViewById(R.id.eventsSeparator)
        athleticsSeparator = findViewById(R.id.athleticsSeparator)
        listView = findViewById(R.id.listView)
        scrollLayout = findViewById(R.id.scrollLayout)
        dateChangerButton = findViewById(R.id.dateChangerButton)
        loadingSymbol = findViewById(R.id.loadingSymbol)

        dateString = Calendar.getInstance().time.dateString() //Should be current date string, but can change to test
        activityTitle?.text = "Today"
        daySchedule = DaySchedule(this, true, true, true, true)


        val gestureDetector = GestureDetector(this, TodayGestureListener(this))
        dateChangerButton?.setOnTouchListener(object:View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return gestureDetector.onTouchEvent(event);
            }
        })

        setupViewForTabbedActivity(R.layout.activity_today)

        sharedPreferences4 = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        scrollLayout?.removeViews(1, 2)

    }

    override fun onStart() {
        super.onStart()
        loadingSymbol?.visibility = View.VISIBLE
        todayParser = TodayDataParser(this)
    }
    override fun tabSelectedHandler() {
        DeveloperPrinter().print("The dateString is: " + dateString)
        val day : Int? = daySchedule?.dateDayDict!![schoolSelected]!![dateString]
        dayIndicatorLabel?.text = getDayOfCycle(day)
    }


    //MARK - Date functions
    fun dateButtonTapped() {
        if (loadingSymbol?.visibility == View.INVISIBLE) {
            val yearFormatter = SimpleDateFormat("yyyy")
            val monthFormatter = SimpleDateFormat("MM")
            val dayFormatter = SimpleDateFormat("dd")
            val date = dateStringFormatter.parse(dateString)
            val mYear = yearFormatter.format(date).toInt()
            val mMonth = monthFormatter.format(date).toInt()
            val mDay = dayFormatter.format(date).toInt()
            val datePickerDialog = DatePickerDialog(this@TodayActivity,
                object:DatePickerDialog.OnDateSetListener {
                    override fun onDateSet(view:DatePicker, year:Int, monthOfYear:Int, dayOfMonth:Int) {
                        var month = "${monthOfYear+1}"
                        var day = "$dayOfMonth"
                        if (monthOfYear < 10) {
                            month = "0$month"
                        }
                        if (dayOfMonth < 10) {
                            day = "0$day"
                        }
                        dateString = "$month/$day/$year"
                        if (dateString != Calendar.getInstance().time.dateString()) {
                            val titleFormatter = SimpleDateFormat("MMM d")
                            activityTitle?.text = titleFormatter.format(dateStringFormatter.parse(dateString))
                        } else {
                            activityTitle?.text = "Today"
                        }
                        tabSelectedHandler()
                        buildLinearLayoutAsTableView()
                    }
                }, mYear, mMonth - 1, mDay)
            datePickerDialog.show()
        }
    }
    fun dateButtonDoubleTapped() {
        if (loadingSymbol?.visibility == View.INVISIBLE) {
            dateString = Calendar.getInstance().time.dateString()
            activityTitle?.text = "Today"
            tabSelectedHandler()
            buildLinearLayoutAsTableView()
        }

    }
    fun getDayOfCycle(day : Int?) : String {
        if (day != null && day != 0) {
            return "Today is Day $day"
        } else {
            return "There is no school today"
        }
    }


    //MARK - Table methods
    fun buildLinearLayoutAsTableView() {
        DeveloperPrinter().print("Building Today View as a LinearLayout impersonating a ListView")
        scrollLayout?.removeAllViews()

        scrollLayout?.addView(dayIndicatorLabel)
        scrollLayout?.addView(eventsSeparator)
        createCellForEventsModelAndAddToEndOfScrollView(todayParser?.events(dateStringFormatter.parse(dateString)), scrollLayout!!)
        scrollLayout?.addView(athleticsSeparator)
        createCellForAthleticsModelAndAddToEndOfScrollView(todayParser?.athletics(dateStringFormatter.parse(dateString)), scrollLayout!!)

        loadingSymbol?.visibility = View.INVISIBLE
    }
    private fun createCellForEventsModelAndAddToEndOfScrollView(model : Array<EventsModel>?, scrollLayout : LinearLayout) {
        if (!model.isNullOrEmpty()) {
            for (event in 0 until model.count()) {
                val separatorLine = View(this)
                val lineParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.toPx()
                )
                separatorLine.layoutParams = lineParams
                separatorLine.setBackgroundColor(ContextCompat.getColor(this, R.color.csbcSuperLightGray))

                val titleLabel = createBasicTextView(Color.BLACK, UserFontStyles.SEMIBOLD)
                titleLabel.text = model[event].event
                titleLabel.setPadding(24.toPx(), 14.toPx(), 24.toPx(), 0.toPx())


                val levelLabel = createBasicTextView(R.color.csbcGray, UserFontStyles.REGULAR)
                levelLabel.text = model[event]!!.schools
                levelLabel.setPadding(24.toPx(), 10.toPx(), 24.toPx(), 0.toPx())


                val timeLabel = createBasicTextView(R.color.csbcGray, UserFontStyles.ITALIC)
                timeLabel.text = model[event]!!.time
                timeLabel.setPadding(24.toPx(), 8.toPx(), 24.toPx(), 14.toPx())

                scrollLayout.addView(separatorLine)
                scrollLayout.addView(titleLabel)
                scrollLayout.addView(levelLabel)
                scrollLayout.addView(timeLabel)
            }
        } else {
            val eventsNoEvents = createNoEventsTextView()
            scrollLayout.addView(eventsNoEvents)
        }
    }
    private fun createCellForAthleticsModelAndAddToEndOfScrollView(model : AthleticsModel?, scrollLayout : LinearLayout) {
        if (model != null) {
            for (event in 0 until model.title.count()) {
                val separatorLine = View(this)
                val lineParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.toPx()
                )
                separatorLine.layoutParams = lineParams
                separatorLine.setBackgroundColor(ContextCompat.getColor(this, R.color.csbcSuperLightGray))

                val titleLabel = createBasicTextView(Color.BLACK, UserFontStyles.SEMIBOLD)
                titleLabel.text = model.title[event]
                titleLabel.setPadding(24.toPx(), 14.toPx(), 24.toPx(), 0.toPx())


                val levelLabel = createBasicTextView(R.color.csbcGray, UserFontStyles.REGULAR)
                levelLabel.text = model.level[event]
                levelLabel.setPadding(24.toPx(), 10.toPx(), 24.toPx(), 0.toPx())


                val timeLabel = createBasicTextView(R.color.csbcGray, UserFontStyles.ITALIC)
                timeLabel.text = model.time[event]
                timeLabel.setPadding(24.toPx(), 8.toPx(), 24.toPx(), 14.toPx())

                scrollLayout.addView(separatorLine)
                scrollLayout.addView(titleLabel)
                scrollLayout.addView(levelLabel)
                scrollLayout.addView(timeLabel)
            }
        } else {
            val athleticsNoEvents = createNoEventsTextView()
            scrollLayout.addView(athleticsNoEvents)
        }
    }
    private fun createBasicTextView(color: Int, style: UserFontStyles) : TextView {
        val textView: TextView = TextView(this)
        textView.layoutParams = cellParams
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17F)
        textView.setTextColor(color)
        textView.setCustomFont(UserFontFamilies.GOTHAM, style)
        textView.setBackgroundColor(Color.WHITE)
        return textView
    }
    private fun createNoEventsTextView() : TextView {
        val textView = createBasicTextView(R.color.csbcGray, UserFontStyles.ITALIC)
        textView.text = "There are no events today"
        textView.setPadding(24.toPx(),14.toPx(),24.toPx(),14.toPx())
        return textView
    }
}
