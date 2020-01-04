package com.csbcsaints.CSBCandroid.Lunch

import android.app.Activity
import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class LunchMenuRetriever {

    fun downloadLunchMenus(parent : Activity) {
        FirebaseDatabase.getInstance().reference.child("Lunch/Links").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                println("Received lunch menu links:")
                val lunchURLsDict = p0.value as Map<String,String>
                val lunchURLs : Array<String> = arrayOf("" ,"", "", "")
                for (each in lunchURLsDict) {
                    when {
                        each.key == "seton" -> lunchURLs[0] = each.value
                        each.key == "johnjames" -> {
                            lunchURLs[3] = each.value
                            lunchURLs[1] = each.value
                        }
                        each.key == "saints" -> lunchURLs[2] = each.value
                    }
                    println(each)
                }
                parent.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE).edit().putString("lunchURLs", Gson().toJson(lunchURLs)).apply()
            }
            override fun onCancelled(databaseError: DatabaseError) { println("Cancelled") }
        })
    }
}