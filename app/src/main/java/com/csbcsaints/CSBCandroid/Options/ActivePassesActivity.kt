package com.csbcsaints.CSBCandroid.Options

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ListView
import com.csbcsaints.CSBCandroid.*
import com.csbcsaints.CSBCandroid.stringFromTimeInterval
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

data class StudentPassInfo(
    val name: String,
    val graduationYear: Int,
    val currentStatus: Pair<String, Date>,
    val previousStatuses: Array<Pair<String, Date>>
)

///Pulls all pass data from Firebase, formats and displays students who are out
class ActivePassesActivity : AppCompatActivity() {
    private var listView : ListView? = null

    private var signedOutStudentInfoArray : ArrayList<StudentPassInfo> = arrayListOf()
    private val passDataReference = FirebaseDatabase.getInstance().reference.child("PassSystem/Students")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_passes)
        supportActionBar?.title = "Active Passes"

        listView = findViewById(R.id.listView)


        startFirebase()

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                setupTable()
                mainHandler.postDelayed(this, 1000)
            }
        })


    }
    private fun startFirebase() {
        passDataReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                println("received data")
                val studentsDict = p0.value as? Map<String,Map<String,Any>> ?: return

                signedOutStudentInfoArray = arrayListOf()
                for ((_, student) in studentsDict) {
                    if ((student["currentStatus"] as? String)?.contains("Out") == false) { continue }

                    val studentPassInfo = parseSignedOutStudentForPassInfo(student)
                    signedOutStudentInfoArray.add(studentPassInfo)
                }
                setupTable(signedOutStudentInfoArray)
            }
            override fun onCancelled(databaseError: DatabaseError) { println("Cancelled") }
        })
    }

    private fun parseSignedOutStudentForPassInfo(student : Map<String, Any>) : StudentPassInfo {
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a")

        val studentLog = student["log"] as? Array<Map<String,String>> ?: arrayOf()
        val logToStore : ArrayList<Pair<String, Date>> = arrayListOf()
        for (logEntry in studentLog) {
            val statusFromLog = logEntry["status"] ?: continue
            val dateStringFromLog = logEntry["time"] ?: continue
            val dateFromLog = dateTimeFormatter.parse(dateStringFromLog) ?: continue

            val logToAdd = Pair(statusFromLog, dateFromLog)
            logToStore.add(logToAdd)
        }
        val timeString = student["timeOfStatusChange"] as String
        val time = dateTimeFormatter.parse(timeString)!!
        return StudentPassInfo(
            student["name"] as String,
            (student["graduationYear"] as Long).toInt(),
            Pair(student["currentStatus"] as String, time),
            logToStore.toTypedArray()
        )
    }

    fun setupTable(data: ArrayList<StudentPassInfo> = signedOutStudentInfoArray) {
        this.signedOutStudentInfoArray = data
        if (data.isEmpty()) {
            listView?.visibility = View.INVISIBLE
            return
        }
        val adapter = ActivePassesAdapter(this)
        for (student in data) {
            val month = Calendar.getInstance().get(Calendar.MONTH)
            val year = Calendar.getInstance().get(Calendar.YEAR)
            val seniorsGradYear = if (month >= 6) year + 1 else year
            val gradeLevelMap = mutableMapOf(seniorsGradYear to 12)
            for (i in seniorsGradYear + 1 downTo seniorsGradYear + 6)
                gradeLevelMap[i] = gradeLevelMap[i - 1]!! - 1
            val gradeLevelString = if (gradeLevelMap[student.graduationYear] != null) {
                " (" + gradeLevelMap[student.graduationYear]!! + ")"
            } else ""
            val interval = Calendar.getInstance().time.time - student.currentStatus.second.time
            val timeString = interval.stringFromTimeInterval()

            val statusArray = student.currentStatus.first.split(" - ")
            val location = if (statusArray.size > 1) statusArray[1] else ""
            adapter.addItem(Triple(student.name + gradeLevelString, timeString, location))
        }
        runOnUiThread {
            listView?.adapter = adapter
            listView?.visibility = View.VISIBLE
        }
    }
}
