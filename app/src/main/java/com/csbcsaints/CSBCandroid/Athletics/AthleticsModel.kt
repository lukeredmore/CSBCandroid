package com.csbcsaints.CSBCandroid

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AthleticsModel(
    val homeGame: MutableList<String>,
    val gender: MutableList<String>,
    val level: MutableList<String>,
    val sport: MutableList<String>,
    val opponent: MutableList<String>,
    val time: MutableList<String>,
    val date: String
) : Parcelable

data class AthleticsModelSingle (
    val homeGame: String,
    val gender: String,
    val level: String,
    val sport: String,
    val opponent: String,
    val time: String,
    val date: String
)