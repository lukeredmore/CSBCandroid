package com.csbcsaints.CSBCandroid

import android.content.Context
import com.csbcsaints.CSBCandroid.Calendar.EventsModel
import com.csbcsaints.CSBCandroid.ui.CSBCAppCompatActivity
import com.csbcsaints.CSBCandroid.ui.abbrvMonthString
import com.csbcsaints.CSBCandroid.ui.printAll
import com.google.gson.Gson
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

//import Foundation
//import SwiftSoup
//import SafariServices
//import Alamofire
//import PDFKit
//
//protocol LoadPDFDelegate: class {
//    func tryToLoadPDFs()
//}

/// Finds URLs of, download, and store all the lunch menus
class HTMLController : CSBCAppCompatActivity() {

    private val client = OkHttpClient()
    //weak var delegate : LoadPDFDelegate? = nil
    var lunchesReady : Array<Boolean> = arrayOf(false, false, false, false)
    var lunchURLs : Array<String> = arrayOf("","","","")
    var i = 0
    var loadedPDFURLs : MutableMap<Int,String> = mutableMapOf()
    var loadedWordURLs : MutableMap<Int,String> = mutableMapOf()

    fun downloadAndStoreLunchMenus() {
        lunchesReady = arrayOf(false, false, false, false)

        val setonRequest = Request.Builder()
            .url("https://csbcsaints.org/our-schools/seton-catholic-central/about-scc/about/")
            .build()
        client.newCall(setonRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to CSBCSaints.org: ")
                println(e)
//                eventsData.eventsModelArray = retrieveEventsArrayFromUserDefaults(true)
//                setupTable()
            }
            override fun onResponse(call: Call, response: Response) {
                println("Successfully received lunch data")
                val html = response.body?.string()
                println(html)
                if (html != null) {
                    parseSetonLunchHTML(html)
                } else {
//                    eventsData.eventsModelArray = retrieveEventsArrayFromUserDefaults(true)
                }
//                setupTable()

            }
        })

        val johnRequest = Request.Builder()
            .url("http://www.bcsdfs.org/menu.cfm?mid=1372")
            .build()
        client.newCall(johnRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to CSBCSaints.org: ")
                println(e)
            }
            override fun onResponse(call: Call, response: Response) {
                println("Successfully received lunch data")
                val html = response.body?.string()
                if (html != null) {
                    parseJohnLunchHTML(html)
                }
            }
        })

        val saintsRequest = Request.Builder()
            .url("https://csbcsaints.org/our-schools/all-saints-school/parent-resources/lunch-menu-meal-program/")
            .build()
        client.newCall(saintsRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to CSBCSaints.org: ")
                println(e)
            }
            override fun onResponse(call: Call, response: Response) {
                println("Successfully received lunch data")
                val html = response.body?.string()
                if (html != null) {
                    parseSaintsLunchHTML(html)
                }
            }
        })

        val jamesRequest = Request.Builder()
            .url("https://csbcsaints.org/our-schools/st-james-school/parent-resources/lunch-menu-meal-program/")
            .build()
        client.newCall(jamesRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Error on request to CSBCSaints.org: ")
                println(e)
            }
            override fun onResponse(call: Call, response: Response) {
                println("Successfully received lunch data")
                val html = response.body?.string()
                if (html != null) {
                    parseJamesLunchHTML(html)
                }
            }
        })
    }


    fun parseSetonLunchHTML(html: String?) {
        if (html != null) {
            val doc = Jsoup.parse(html)
            doc.select(".mega-menu-link")
                .forEach {
                    if (it.text() == "Cafeteria Menu") {
                        println("Seton Lunch Menu Link: ${it.attr("href")}")
                        lunchURLs[0] = it.attr("href")
                        lunchesReady[0] = true
                        tryToLoadPDFs()
                        return
                    }
                }
        }
    }
    fun parseJohnLunchHTML(html: String?) {
        if (html != null) {
            val doc = Jsoup.parse(html)
            doc.select("a[href]")
                .forEach {
                    if (it.text().contains("Elementary") && it.text().contains("Lunch")) {
                        var urlWithJS = it.attr("href")
                        urlWithJS = urlWithJS.replace("javascript:popupGetMenu('", "")
                        urlWithJS = urlWithJS.replace("')", "")
                        println("Johns Lunch Menu Link: " + urlWithJS)
                        lunchURLs[1] = urlWithJS
                        lunchesReady[1] = true
                        tryToLoadPDFs()
                        return
                    }
                }
        }
    }
    fun parseSaintsLunchHTML(html: String?) {
        if (html != null) {
            val doc = Jsoup.parse(html)
            doc.select(".et_pb_blurb_description").select("p").select("a")
                .forEach {
                    println(it.text())
//                    if (it.text().toLowerCase().contains("lunch") || it.text().toLowerCase().contains("menu")) {
                        println("Saints Lunch Menu Link: ${it.attr("href")}")
                        lunchURLs[2] = it.attr("href")
                        lunchesReady[2] = true
                        tryToLoadPDFs()
                        return
//                    }
                }
        }
    }
    fun parseJamesLunchHTML(html: String?) {
        if (html != null) {
            val doc = Jsoup.parse(html)
            doc.select(".et_pb_blurb_description").select("p").select("a")
                .forEach {
                    println(it.text())
                    if (it.text().toLowerCase().contains("lunch") || it.text().toLowerCase().contains("menu")) {
                        println("James Lunch Menu Link: ${it.attr("href")}")
                        lunchURLs[3] = it.attr("href")
                        lunchesReady[3] = true
                        tryToLoadPDFs()
                        return
                    }
                }
        }
    }

    fun tryToLoadPDFs() {
        if (lunchesReady.contentEquals(arrayOf(true, true, true, true))) {
            println("Starting document downloads for these lunchURLs:")
            lunchURLs.printAll()
//            i = 0
            val sharedPreferences = getSharedPreferences("UserDefaults", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("lunchURLs", Gson().toJson(lunchURLs)).apply()
//            downloadPDFs()
        }
    }
}

