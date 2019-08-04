package com.csbcsaints.CSBCandroid.ui

import android.util.Log

class DeveloperPrinter {
    fun print(text : String) {
        println(text)
        Log.i("DeveloperMessage", text)
    }
}