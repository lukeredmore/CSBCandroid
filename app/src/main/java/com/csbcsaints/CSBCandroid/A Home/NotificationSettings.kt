package com.csbcsaints.CSBCandroid

data class NotificationSettings(
    var shouldDeliver : Boolean,
    var schools : Array<Boolean>
)

fun NotificationSettings.printNotifData() {
    println("-----------NOTIFICATION SETTINGS-----------")
    println("shouldDeliver: $shouldDeliver")
    print("schools: [")
    print("${schools[0]}, ")
    print("${schools[1]}, ")
    print("${schools[2]}, ")
    println("${schools[3]}] ")
    println("-------------------------------------------")
}