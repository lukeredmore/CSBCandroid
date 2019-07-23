package com.csbcsaints.CSBCandroid

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationSettings(
    var shouldDeliver : Boolean,
    var deliveryTime : String,
    var schools : Array<Boolean>,
    var valuesChangedByUser : Boolean
) : Parcelable

fun NotificationSettings.printNotifData() {
    println("-----------NOTIFICATION SETTINGS-----------")
    println("shouldDeliver: ${shouldDeliver}")
    println("deliveryTime: " + deliveryTime)
    print("schools: [")
    print("${schools[0]}, ")
    print("${schools[1]}, ")
    print("${schools[2]}, ")
    println("${schools[3]}] ")
    println("valuesChangedByUser: ${valuesChangedByUser}")
    println("-------------------------------------------")
}