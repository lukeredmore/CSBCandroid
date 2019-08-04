package com.csbcsaints.CSBCandroid

import android.view.View
import java.text.SimpleDateFormat
import java.util.*

class TodayDataParser(val parent : TodayActivity) {
    private var eventsArray : Array<EventsModel?> = arrayOf()
    var athleticsArray : Array<AthleticsModel?> = arrayOf()

    private var eventsReady = false
    private var athleticsReady = false


    init {
        getSchedulesToSendToToday()
    }

    fun getSchedulesToSendToToday() {
        EventsRetriever().retrieveEventsArray(parent.sharedPreferences4!!, false, false) {
            eventsArray = it
            eventsReady = true
            tryToStartupPager()
        }
        AthleticsRetriever().retrieveAthleticsArray(parent.sharedPreferences4!!, false, false) {
            athleticsArray = it
            athleticsReady = true
            tryToStartupPager()
        }
    }
    private fun tryToStartupPager() {
        if (eventsReady && athleticsReady) {
            parent.runOnUiThread(object:Runnable {
                override fun run() {
                    parent.buildLinearLayoutAsTableView()
                }
            })
        }
    }

    //MARK - Parse schedules for TodayVC
    fun events(date : Date) : Array<EventsModel> {
        val allEventsToday : MutableList<EventsModel> = arrayListOf()
        val fmt = SimpleDateFormat("MMMdd")
        val dateShownForCalendar = fmt.format(date).toUpperCase()
        for (eventsModel in eventsArray) {
            if (eventsModel != null) {
                if (eventsModel.date == dateShownForCalendar) {
                    allEventsToday.add(eventsModel)
                    println("At leaset one event is today")
                }
            }
        }
        if (allEventsToday.isNullOrEmpty()) {
            print("There are no events today")
            return allEventsToday.toTypedArray()
        }

        //Filter for schoolSelected
        val filteredEventsForSchoolsToday : MutableList<EventsModel> = arrayListOf()
        for (i in 0 until allEventsToday.count()) {
            if (allEventsToday[i].schools.contains(parent.schoolSelected) || allEventsToday[i].schools == "") {
                filteredEventsForSchoolsToday.add(allEventsToday[i])
            }
        }
        return filteredEventsForSchoolsToday.toTypedArray()
    }
    fun athletics(date : Date) : AthleticsModel? {
        var allAthleticsToday : AthleticsModel? = null
        val athleticsDateFormatter = SimpleDateFormat("MMMM dd")
        val monthDayDateString = athleticsDateFormatter.format(date)
        for (dateWithEvents in athleticsArray) {
            if (dateWithEvents != null) {
                if (dateWithEvents.date.contains(monthDayDateString)) {
                    allAthleticsToday = dateWithEvents
                }
            }
        }
        return allAthleticsToday
    }
}