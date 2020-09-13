package com.csbcsaints.CSBCandroid

data class NotificationSettings(
    var shouldDeliver : Boolean,
    var schools : Array<Boolean>,
    var notifyFamilyCheckIn: Boolean
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationSettings

        if (shouldDeliver != other.shouldDeliver) return false
        if (!schools.contentEquals(other.schools)) return false
        if (notifyFamilyCheckIn != other.notifyFamilyCheckIn) return false

        return true
    }

    override fun hashCode(): Int {
        var result = shouldDeliver.hashCode()
        result = 31 * result + schools.contentHashCode()
        result = 31 * result + notifyFamilyCheckIn.hashCode()
        return result
    }
}

fun NotificationSettings.printNotifData() {
    println("-----------NOTIFICATION SETTINGS-----------")
    println("shouldDeliver: $shouldDeliver")
    print("schools: [")
    print("${schools[0]}, ")
    print("${schools[1]}, ")
    print("${schools[2]}, ")
    println("${schools[3]}] ")
    println("notifyFamilyCheckIn: $notifyFamilyCheckIn")
    println("-------------------------------------------")
}