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
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import com.csbcsaints.CSBCandroid.ui.HorizontalSwipeListener

//TODO: Ensure calendar data is correct

class TodayActivity : CSBCAppCompatActivity() {
    private var daySchedule: DaySchedule? = null
    private var dayIndicatorLabel: TextView? = null
    private var eventsSeparator: TextView? = null
    private var athleticsSeparator: TextView? = null
    private var scrollLayout: LinearLayout? = null
    private var dateChangerButton: TextView? = null
    private var activityTitle: TextView? = null
    private var loadingSymbol: ProgressBar? = null
    private var currentDate = Calendar.getInstance().time

    private var todayParser: TodayDataParser? = null

    private val cellParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )

    var sharedPreferences4: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_today)

        activityTitle = findViewById(R.id.activityTitle)
        dayIndicatorLabel = findViewById(R.id.dayIndicatorLabel)
        dayIndicatorLabel?.setCustomFont(UserFontFamilies.GOTHAM, UserFontStyles.BOLD)
        eventsSeparator = findViewById(R.id.eventsSeparator)
        athleticsSeparator = findViewById(R.id.athleticsSeparator)
        scrollLayout = findViewById(R.id.scrollLayout)
        dateChangerButton = findViewById(R.id.dateChangerButton)
        loadingSymbol = findViewById(R.id.loadingSymbol)

        currentDate = Calendar.getInstance().time //Should be current date, but can change to test
        activityTitle?.text = "Today"
        daySchedule = DaySchedule(this)


        val gestureDetector = GestureDetector(this, TodayGestureListener(this))
        dateChangerButton?.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event); }

        setupViewForTabbedActivity(R.layout.activity_today)

        sharedPreferences4 = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
        scrollLayout?.removeViews(1, 2)

        scrollLayout?.setOnTouchListener(object : HorizontalSwipeListener() {
            override fun onSwipeRight() {
                currentDate = currentDate.addDays(1)
                tabSelectedHandler()
                buildLinearLayoutAsTableView()
            }

            override fun onSwipeLeft() {
                currentDate = currentDate.addDays(-1)
                tabSelectedHandler()
                buildLinearLayoutAsTableView()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        loadingSymbol?.visibility = View.VISIBLE
        todayParser = TodayDataParser(this)
    }

    override fun tabSelectedHandler() {
        println("The date is: $currentDate")
        val day = daySchedule?.day(currentDate, schoolSelected)
        dayIndicatorLabel?.text = getDayOfCycle(day)

        if (currentDate.dateString() != Calendar.getInstance().time.dateString()) {
            val titleFormatter = SimpleDateFormat("MMM d")
            activityTitle?.text = titleFormatter.format(currentDate)
        } else {
            activityTitle?.text = "Today"
        }
    }


    //MARK - Date functions
    fun dateButtonTapped() {
        if (loadingSymbol?.visibility == View.INVISIBLE) {
            val yearFormatter = SimpleDateFormat("yyyy")
            val monthFormatter = SimpleDateFormat("MM")
            val dayFormatter = SimpleDateFormat("dd")
            val mYear = yearFormatter.format(currentDate).toInt()
            val mMonth = monthFormatter.format(currentDate).toInt()
            val mDay = dayFormatter.format(currentDate).toInt()
            val datePickerDialog = DatePickerDialog(this@TodayActivity, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val month = if (monthOfYear < 10) "b" else "" + "${monthOfYear + 1}"
                val day = if (dayOfMonth < 10) "a" else "" + "$dayOfMonth"
                println("setting date to $year-$month-$day")
                val formatter = SimpleDateFormat("yyyy-MM-dd")
                currentDate = formatter.parse("$year-$month-$day")
                tabSelectedHandler()
                buildLinearLayoutAsTableView()
            }, mYear, mMonth - 1, mDay)
            datePickerDialog.show()
        }
    }

    fun dateButtonDoubleTapped() {
        if (loadingSymbol?.visibility == View.INVISIBLE) {
            currentDate = Calendar.getInstance().time
            tabSelectedHandler()
            buildLinearLayoutAsTableView()
        }

    }

    private fun getDayOfCycle(day: Int?): String {
        return if (day != null && day != 0) {
            "Today is Day $day"
        } else {
            "There is no school today"
        }
    }


    //MARK - Table methods
    fun buildLinearLayoutAsTableView() {
        println("Building Today View as a LinearLayout impersonating a ListView")
        scrollLayout?.removeAllViews()

        scrollLayout?.addView(dayIndicatorLabel)
        scrollLayout?.addView(eventsSeparator)
        createCellForEventsModelAndAddToEndOfScrollView(todayParser?.events(currentDate), scrollLayout!!)
        scrollLayout?.addView(athleticsSeparator)
        createCellForAthleticsModelAndAddToEndOfScrollView(todayParser?.athletics(currentDate), scrollLayout!!)

        loadingSymbol?.visibility = View.INVISIBLE
    }

    private fun createCellForEventsModelAndAddToEndOfScrollView(
        model: Array<EventsModel>?,
        scrollLayout: LinearLayout
    ) {
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
                levelLabel.text = model[event].schools
                levelLabel.setPadding(24.toPx(), 10.toPx(), 24.toPx(), 0.toPx())


                val timeLabel = createBasicTextView(R.color.csbcGray, UserFontStyles.ITALIC)
                timeLabel.text = model[event].time
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

    private fun createCellForAthleticsModelAndAddToEndOfScrollView(model: AthleticsModel?, scrollLayout: LinearLayout) {
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

    private fun createBasicTextView(color: Int, style: UserFontStyles): TextView {
        val textView: TextView = TextView(this)
        textView.layoutParams = cellParams
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17F)
        textView.setTextColor(color)
        textView.setCustomFont(UserFontFamilies.GOTHAM, style)
        textView.setBackgroundColor(Color.WHITE)
        return textView
    }

    private fun createNoEventsTextView(): TextView {
        val textView = createBasicTextView(R.color.csbcGray, UserFontStyles.ITALIC)
        textView.text = "There are no events today"
        textView.setPadding(24.toPx(), 14.toPx(), 24.toPx(), 14.toPx())
        return textView
    }


}
