package com.csbcsaints.CSBCandroid

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AthleticsModel(
    val title: MutableList<String>,
    val level: MutableList<String>,
    val time: MutableList<String>,
    val date: String
) : Parcelable

data class AthleticsModelSingle (
    val title: String,
    val level: String,
    val time: String,
    val date: String
)