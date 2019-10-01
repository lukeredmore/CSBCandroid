package com.csbcsaints.CSBCandroid

class Icon {
    var name: String? = null
    var image: Int? = null

    constructor(name: String, image: Int) {
        this.name = name
        this.image = image
    }

    fun announce() {
        println("$name")
    }
}