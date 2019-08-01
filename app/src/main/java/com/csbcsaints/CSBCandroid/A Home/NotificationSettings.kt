package com.csbcsaints.CSBCandroid

data class NotificationSettings(
    var shouldDeliver : Boolean,
    var deliveryTime : String,
    var schools : Array<Boolean>,
    var valuesChangedByUser : Boolean
)

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