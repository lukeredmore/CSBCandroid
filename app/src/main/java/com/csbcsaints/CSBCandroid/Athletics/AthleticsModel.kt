package com.csbcsaints.CSBCandroid

data class AthleticsModel(
    val title: MutableList<String>,
    val level: MutableList<String>,
    val time: MutableList<String>,
    val date: String
)

data class AthleticsModelSingle (
    val title: String,
    val level: String,
    val time: String,
    val date: String
)