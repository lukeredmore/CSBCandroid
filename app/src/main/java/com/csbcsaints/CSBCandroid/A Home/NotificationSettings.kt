package com.csbcsaints.CSBCandroid

import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter

data class NotificationSettings(
    var shouldDeliver : Boolean,
    var deliveryTime : String,
    var schools : Array<Boolean>,
    var valuesChangedByUser : Boolean
)

fun NotificationSettings.printNotifData() {
    DeveloperPrinter().print("-----------NOTIFICATION SETTINGS-----------")
    DeveloperPrinter().print("shouldDeliver: ${shouldDeliver}")
    DeveloperPrinter().print("deliveryTime: " + deliveryTime)
    print("schools: [")
    print("${schools[0]}, ")
    print("${schools[1]}, ")
    print("${schools[2]}, ")
    DeveloperPrinter().print("${schools[3]}] ")
    DeveloperPrinter().print("valuesChangedByUser: ${valuesChangedByUser}")
    DeveloperPrinter().print("-------------------------------------------")
}