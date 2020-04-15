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
        FirebaseDatabase.getInstance().reference.child("Schools").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                println("Received lunch menu links:")
                val lunchURLsDict = p0.value as Map<String,Map<String,Map<String,Any>>>
                var lunchURLs = listOf("seton" ,"john", "saints", "james")
                lunchURLs = lunchURLs.map {
                    (lunchURLsDict[it]?.get("info")?.get("lunchURL") ?: "") as String
                }
                println(lunchURLs)
                parent.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE).edit().putString("lunchURLs", Gson().toJson(lunchURLs)).apply()
            }
            override fun onCancelled(databaseError: DatabaseError) { println("Cancelled") }
        })
    }
}