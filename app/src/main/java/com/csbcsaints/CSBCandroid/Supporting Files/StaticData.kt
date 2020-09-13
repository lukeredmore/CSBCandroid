package com.csbcsaints.CSBCandroid

import android.R.attr.data
import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import me.akatkov.kotlinyjson.JSON
import java.io.*


object StaticData {
    fun readData(path : String): String?  {


        try {
            val inputStream: InputStream? = MyApplication.instance.openFileInput("static-data.txt")
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                var receiveString: String? = ""
                val stringBuilder = StringBuilder()
                while (bufferedReader.readLine().also { receiveString = it } != null) {
                    stringBuilder.append("\n").append(receiveString)
                }
                inputStream.close()
                val jsonString = stringBuilder.toString()

                val jsonObj = JSON(jsonString)

                val pathArr = path.split('/')

                var level : JSON? = jsonObj
                for (nextLevel in pathArr) {
                        if (level != null) {
                            level = level[nextLevel]
                        } else {
                        level = null
                        break
                    }
                }
                println(path + ": " + level?.rawString())
                return level?.rawString()
            }
        } catch (e: FileNotFoundException) {
            Log.e("login activity", "File not found: " + e.toString())
        } catch (e: IOException) {
            Log.e("login activity", "Can not read file: $e")
        }

        return null
        }

    fun getDataFromFirebase(completion : (() -> Unit)? = null) {
        FirebaseDatabase.getInstance().getReference("Schools").addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val jsonString = Gson().toJson(p0.value)
                println("Static data updating: " + jsonString)

                try {
                    val outputStreamWriter = OutputStreamWriter(
                       MyApplication.instance.openFileOutput(
                            "static-data.txt",
                            Context.MODE_PRIVATE
                        )
                    )
                    outputStreamWriter.write(jsonString)
                    outputStreamWriter.close()
                } catch (e: IOException) {
                    Log.e("Exception", "File write failed: " + e.toString())
                }
                if (completion != null) {
                    completion()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) { println("Cancelled")
                if (completion != null) {
                    completion()
                }
            }
        })
    }
}

