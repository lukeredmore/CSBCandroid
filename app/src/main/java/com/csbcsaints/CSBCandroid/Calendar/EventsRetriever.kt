package com.csbcsaints.CSBCandroid

import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.*
import com.csbcsaints.CSBCandroid.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.reflect.TypeToken

class EventsRetriever(val preferences: SharedPreferences?, val completion: (Set<EventsModel>, CSBCListDataType) -> Unit) {

    val dataParser = EventsDataParser()


    val localEventsSet : Set<EventsModel>
        get() {
            val json = preferences?.getString("eventsSet", "")
            val set : Set<EventsModel>? = Gson().fromJson(json, object : TypeToken<Set<EventsModel>>() {}.type )
            return set ?: setOf()
        }


    fun retrieveEventsArray(forceReturn : Boolean = false, forceRefresh: Boolean = false) {
        if (forceRefresh) {
            println("Events Data is being refreshed")
            completion(localEventsSet, CSBCListDataType.DUMMY)
            requestEventsDataFromFirebase()
        } else if (forceReturn) {
            val json = preferences?.getString("eventsSet", null)
            return if (json != null) {
                println("Force return found an old JSON value")
                completion(Gson().fromJson(json, object : TypeToken<Set<EventsModel>>() {}.type ), CSBCListDataType.COMPLETE)
            } else {
                println("Force return returned an empty array")
                completion(setOf(), CSBCListDataType.COMPLETE)
            }
        } else {
            println("Attempting to retrieve stored Events data.")
            val currentTime = Calendar.getInstance().time
            val eventsArrayTimeString : String? = preferences?.getString("eventsArrayTime", null)
            val eventsArrayTime = eventsArrayTimeString?.toDateWithTime()?.addHours(1)
            val json = preferences?.getString("eventsSet", null)

            if (eventsArrayTime != null && !json.isNullOrEmpty()) {
                if (eventsArrayTime < currentTime) {
                    println("Events data found, but is old. Will refresh online.")
                    completion(localEventsSet, CSBCListDataType.DUMMY)
                    requestEventsDataFromFirebase()
                } else {
                    println("Up-to-date Events data found, no need to look online.")
                    return completion(localEventsSet, CSBCListDataType.COMPLETE)
                }
            } else {
                println("No Events data found. Looking online.")
                completion(setOf(), CSBCListDataType.DUMMY)
                requestEventsDataFromFirebase()
            }
        }
    }
    private fun requestEventsDataFromFirebase() {
        FirebaseDatabase.getInstance().reference.child("Calendars").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                println("received data")
                val eventsArray : MutableList<Map<String,String>> = mutableListOf()
                p0.child("eventsArray").children.forEach {
                    val mapToAppend : MutableMap<String,String> = mutableMapOf()
                    it.children.forEach {
                        mapToAppend[it.key!!] = it.value as String
                    }
                    eventsArray.add(mapToAppend)
                }
                if (eventsArray.isNotEmpty()) {
                    println("Events array updated, new data returned")
                    completion(dataParser.parseJSON(eventsArray.toTypedArray(), preferences), CSBCListDataType.COMPLETE)
                } else {
                    println("unparseable data from firebase")
                    completion(setOf(), CSBCListDataType.COMPLETE)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) { println("Cancelled") }
        })
    }
}