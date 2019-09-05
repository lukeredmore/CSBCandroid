package com.csbcsaints.CSBCandroid

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.util.*
import com.csbcsaints.CSBCandroid.CSBCListDataType
import com.csbcsaints.CSBCandroid.ui.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.reflect.TypeToken
import eu.amirs.JSON
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

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
            DeveloperPrinter().print("Events Data is being refreshed")
            completion(localEventsSet, CSBCListDataType.DUMMY)
            tryToRequestEventsFromGCF(true)
            requestEventsDataFromFirebase()
        } else if (forceReturn) {
            val json = preferences?.getString("eventsSet", null)
            return if (json != null) {
                DeveloperPrinter().print("Force return found an old JSON value")
                completion(Gson().fromJson(json, object : TypeToken<Set<EventsModel>>() {}.type ), CSBCListDataType.COMPLETE)
            } else {
                DeveloperPrinter().print("Force return returned an empty array")
                completion(setOf(), CSBCListDataType.COMPLETE)
            }
        } else {
            DeveloperPrinter().print("Attempting to retrieve stored Events data.")
            val currentTime = Calendar.getInstance().time
            val eventsArrayTimeString : String? = preferences?.getString("eventsArrayTime", null)
            val eventsArrayTime = eventsArrayTimeString?.toDateWithTime()?.addHours(1)
            val json = preferences?.getString("eventsSet", null)

            if (eventsArrayTime != null && !json.isNullOrEmpty()) {
                if (eventsArrayTime < currentTime) {
                    DeveloperPrinter().print("Events data found, but is old. Will refresh online.")
                    completion(localEventsSet, CSBCListDataType.DUMMY)
                    requestEventsDataFromFirebase()
                } else {
                    DeveloperPrinter().print("Up-to-date Events data found, no need to look online.")
                    return completion(localEventsSet, CSBCListDataType.COMPLETE)
                }
            } else {
                DeveloperPrinter().print("No Events data found. Looking online.")
                completion(setOf(), CSBCListDataType.DUMMY)
                requestEventsDataFromFirebase()
            }
        }
    }
    fun requestEventsDataFromFirebase() {
        FirebaseDatabase.getInstance().reference.child("Calendars")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val eventsArrayUpdating = p0.child("eventsArrayUpdating").value as? String ?: return
                    val eventsArrayDict = p0.child("eventsSet").value as? Array<Map<String,String>> ?: return
                    if (eventsArrayUpdating == "true") {
                        print("Events array is currently updating, waiting to return updated value")
                        completion(localEventsSet, CSBCListDataType.DUMMY)
                        Timer(null, false).schedule(5000) {
                            requestEventsDataFromFirebase()
                        }
                    } else {
                        print("Events array updated, new data returned")
                        completion(dataParser.parseJSON(eventsArrayDict, preferences), CSBCListDataType.COMPLETE)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }

            })
    }
    companion object {

        val eventsURL : URL
            get() = if (BuildConfig.BUILD_TYPE == "debug") URL("https://us-central1-csbc-f4e43.cloudfunctions.net/retrieveEventsArray") else URL("https://us-central1-csbcprod.cloudfunctions.net/retrieveEventsArray")

        fun tryToRequestEventsFromGCF(forceRefresh : Boolean = false) {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            println("requesting events from GCF, maybe")
            FirebaseDatabase.getInstance().reference.child("Calendars/eventsArrayTime")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        val eventsArrayTimeString = p0.value as? String
                        println("Stringggg $eventsArrayTimeString")
                        val eventsArrayTime = dateFormatter.parse(eventsArrayTimeString)
                        val dateInOneHour = eventsArrayTime?.addMinutes(60)
                        if (Date() < dateInOneHour && !forceRefresh) {
                            print("Existing firebase data is okay")
                        } else {
                            print("Firebase data needs to be replaced")
                            requestEventsDataFromGCF()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        println("database checking failed with error: ${databaseError.message}")
                    }

                })
        }
        fun requestEventsDataFromGCF() {
            println("We are asking for Events data")
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(eventsURL)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    println("Error on request to Cloud Functions: ")
                    println(e)
                    FirebaseDatabase.getInstance().reference.child("Calendars/eventsArrayUpdating").setValue("false")
                }
                override fun onResponse(call: Call, response: Response) {
                    if ((200..299).contains(response.code)) {
                        println("Sucessfully updated firebase from GCF")
                    } else {
                        println("Returned invalid status code")
                        FirebaseDatabase.getInstance().reference.child("Calendars/eventsArrayUpdating").setValue("false")
                    }
                }
            })
            FirebaseDatabase.getInstance().reference.child("Calendars/eventsArrayUpdating").setValue("true")


        }
    }


}