package com.csbcsaints.CSBCandroid

import android.app.Application

class MyApplication : Application() {
    init{
        instance = this
    }
    companion object {
        lateinit var instance : MyApplication
    }
}

