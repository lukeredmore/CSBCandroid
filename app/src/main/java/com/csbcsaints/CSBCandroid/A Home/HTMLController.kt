package com.csbcsaints.CSBCandroid

import android.content.Context
import com.csbcsaints.CSBCandroid.ui.DeveloperPrinter
import com.csbcsaints.CSBCandroid.ui.printAll
import com.google.gson.Gson
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException

/// Finds URLs of, download, and store all the lunch menus
class HTMLController(val parent : MainActivity) {

    private val client = OkHttpClient()
    private var lunchesReady : Array<Boolean> = arrayOf(false, false, false, false)
    private var lunchURLs : Array<String> = arrayOf("","","","")

    init {
        downloadAndStoreLunchMenus()
    }

    private fun downloadAndStoreLunchMenus() {
        lunchesReady = arrayOf(false, false, false, false)

        val setonRequest = Request.Builder()
            .url("https://csbcsaints.org/our-schools/seton-catholic-central/about-scc/about/")
            .build()
        client.newCall(setonRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DeveloperPrinter().print("Error on request to CSBCSaints.org: $e")
            }
            override fun onResponse(call: Call, response: Response) {
                DeveloperPrinter().print("Successfully received lunch data")
                val html = response.body?.string()
                if (html != null) {
                    parseSetonLunchHTML(html, parent)
                }
            }
        })

        val johnRequest = Request.Builder()
            .url("http://www.bcsdfs.org/menu.cfm?mid=1372")
            .build()
        client.newCall(johnRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DeveloperPrinter().print("Error on request to CSBCSaints.org: $e")
            }
            override fun onResponse(call: Call, response: Response) {
                DeveloperPrinter().print("Successfully received lunch data")
                val html = response.body?.string()
                if (html != null) {
                    parseJohnLunchHTML(html, parent)
                }
            }
        })

        val saintsRequest = Request.Builder()
            .url("https://csbcsaints.org/our-schools/all-saints-school/parent-resources/lunch-menu-meal-program/")
            .build()
        client.newCall(saintsRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DeveloperPrinter().print("Error on request to CSBCSaints.org: $e")
            }
            override fun onResponse(call: Call, response: Response) {
                DeveloperPrinter().print("Successfully received lunch data")
                val html = response.body?.string()
                if (html != null) {
                    parseSaintsLunchHTML(html, parent)
                }
            }
        })

        val jamesRequest = Request.Builder()
            .url("https://csbcsaints.org/our-schools/st-james-school/parent-resources/lunch-menu-meal-program/")
            .build()
        client.newCall(jamesRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DeveloperPrinter().print("Error on request to CSBCSaints.org: $e")
            }
            override fun onResponse(call: Call, response: Response) {
                DeveloperPrinter().print("Successfully received lunch data")
                val html = response.body?.string()
                if (html != null) {
                    parseJamesLunchHTML(html, parent)
                }
            }
        })
    }


    fun parseSetonLunchHTML(html: String?, parent: MainActivity) {
        if (html != null) {
            val doc = Jsoup.parse(html)
            doc.select(".mega-menu-link")
                .forEach {
                    if (it.text() == "Cafeteria Menu") {
                        DeveloperPrinter().print("Seton Lunch Menu Link: ${it.attr("href")}")
                        lunchURLs[0] = it.attr("href")
                        lunchesReady[0] = true
                        tryToLoadPDFs(parent)
                        return
                    }
                }
        }
    }
    fun parseJohnLunchHTML(html: String?, parent: MainActivity) {
        if (html != null) {
            val doc = Jsoup.parse(html)
            doc.select("a[href]")
                .forEach {
                    if (it.text().contains("Elementary") && it.text().contains("Lunch")) {
                        var urlWithJS = it.attr("href")
                        urlWithJS = urlWithJS.replace("javascript:popupGetMenu('", "")
                        urlWithJS = urlWithJS.replace("')", "")
                        DeveloperPrinter().print("Johns Lunch Menu Link: " + urlWithJS)
                        lunchURLs[1] = urlWithJS
                        lunchesReady[1] = true
                        tryToLoadPDFs(parent)
                        return
                    }
                }
        }
    }
    fun parseSaintsLunchHTML(html: String?, parent: MainActivity) {
        if (html != null) {
            val doc = Jsoup.parse(html)
            doc.select(".et_pb_blurb_description").select("p").select("a")
                .forEach {
                    if (it.text().toLowerCase().contains("lunch") || it.text().toLowerCase().contains("menu")) {
                        DeveloperPrinter().print("Saints Lunch Menu Link: ${it.attr("href")}")
                        lunchURLs[2] = it.attr("href")
                        lunchesReady[2] = true
                        tryToLoadPDFs(parent)
                        return
                    }
                }
        }
    }
    fun parseJamesLunchHTML(html: String?, parent: MainActivity) {
        if (html != null) {
            val doc = Jsoup.parse(html)
            doc.select(".et_pb_blurb_description").select("p").select("a")
                .forEach {
                    if (it.text().toLowerCase().contains("lunch") || it.text().toLowerCase().contains("menu")) {
                        DeveloperPrinter().print("James Lunch Menu Link: ${it.attr("href")}")
                        lunchURLs[3] = it.attr("href")
                        lunchesReady[3] = true
                        tryToLoadPDFs(parent)
                        return
                    }
                }
        }
    }

    fun tryToLoadPDFs(parent: MainActivity) {
        if (lunchesReady.contentEquals(arrayOf(true, true, true, true))) {
            DeveloperPrinter().print("Starting document downloads for these lunchURLs:")
            lunchURLs.printAll()
            parent.getSharedPreferences("UserDefaults", Context.MODE_PRIVATE).edit().putString("lunchURLs", Gson().toJson(lunchURLs)).apply()
        }
    }
}

