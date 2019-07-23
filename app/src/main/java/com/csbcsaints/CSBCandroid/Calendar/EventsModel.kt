package com.csbcsaints.CSBCandroid.Calendar

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EventsModel(
    val event: String,
    val date: String,
    val day: String,
    val month: String,
    val time: String,
    val schools: String
) : Parcelable

//data class EventsModel(
//    val event: String,
//    val date: String,
//    val day: String,
//    val month: String,
//    val time: String,
//    val schools: String
//) : Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString())
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(event)
//        parcel.writeString(date)
//        parcel.writeString(day)
//        parcel.writeString(month)
//        parcel.writeString(time)
//        parcel.writeString(schools)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<EventsModel> {
//        override fun createFromParcel(parcel: Parcel): EventsModel {
//            return EventsModel(parcel)
//        }
//
//        override fun newArray(size: Int): Array<EventsModel?> {
//            return arrayOfNulls(size)
//        }
//    }
//}

