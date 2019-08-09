package com.csbcsaints.CSBCandroid.ui

import android.util.Log
import com.crashlytics.android.Crashlytics

class DeveloperPrinter {
    fun print(text : String) {
        Log.i("DeveloperMessage", text)
        Crashlytics.log(Log.DEBUG, "DeveloperMessage", text)
    }
}