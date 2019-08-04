package com.csbcsaints.CSBCandroid

import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter

class Icon {
    var name: String? = null
    var image: Int? = null

    constructor(name: String, image: Int) {
        this.name = name
        this.image = image
    }

    fun announce() {
        DeveloperPrinter().print("$name")
    }
}