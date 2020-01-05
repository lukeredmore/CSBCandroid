package com.csbcsaints.CSBCandroid

import android.content.res.Resources
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import okhttp3.Request
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.ParseException




//MARK - Dates
fun Date.yearString() : String {
    val dateFormatter = SimpleDateFormat("yyyy")
    return dateFormatter.format(this)
}
fun Date.fullMonthString() : String {
    val dateFormatter = SimpleDateFormat("MMMM")
    return dateFormatter.format(this)
}
fun Date.abbrvMonthString() : String {
    val dateFormatter = SimpleDateFormat("MMM")
    return dateFormatter.format(this)
}
fun Date.numMonthString() : String {
    val dateFormatter = SimpleDateFormat("MM")
    return dateFormatter.format(this)
}
fun Date.dayString() : String {
    val dateFormatter = SimpleDateFormat("dd")
    return dateFormatter.format(this)
}
fun Date.dateString() : String {
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy")
    return dateFormatter.format(this)
}
fun Date.dateStringWithTime() : String {
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
    return dateFormatter.format(this)
}
fun String.toDateWithTime() : Date {
    val dateFormatter = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
    return dateFormatter.parse(this)
}
fun Date.addDays(daysToAdd: Int) : Date {
    val calendar = Calendar.getInstance()
    try {
        calendar.time = this
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    calendar.add(Calendar.DAY_OF_MONTH, daysToAdd)
    return calendar.time
}
fun Date.addHours(hoursToAdd: Int) : Date {
    val calendar = Calendar.getInstance()
    try {
        calendar.time = this
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    calendar.add(Calendar.HOUR_OF_DAY, hoursToAdd)
    return calendar.time
}
fun Date.addMinutes(minutesToAdd: Int) : Date {
    val calendar = Calendar.getInstance()
    try {
        calendar.time = this
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    calendar.add(Calendar.MINUTE, minutesToAdd)
    return calendar.time
}


//MARK - Fonts
enum class UserFontFamilies {
    GOTHAM, MONTSERRAT
}
enum class UserFontStyles {
    XLIGHT, XLIGHTITALIC, LIGHT, ULTRALIGHTITALIC, THIN, THINITALIC, REGULAR, ITALIC, SEMIBOLD, BOLD, BLACK
}
fun TextView.setCustomFont(fontFamily: UserFontFamilies, fontStyle: UserFontStyles) {
    var fontName : String = "gotham.otf"
    when (fontFamily) {
        UserFontFamilies.GOTHAM -> {
            when (fontStyle) {
                UserFontStyles.XLIGHT -> fontName = "Gotham-XLight.otf"
                UserFontStyles.XLIGHTITALIC -> fontName = "XLightItalic.otf"
                UserFontStyles.LIGHT -> fontName = "Gotham-Light.otf"
                UserFontStyles.ULTRALIGHTITALIC -> fontName = "UltraItalic.otf"
                UserFontStyles.THIN -> fontName = "Gotham-Thin.otf"
                UserFontStyles.THINITALIC -> fontName = "Gotham-ThinItalic.otf"
                UserFontStyles.REGULAR -> fontName = "GothamBookRegular.otf"
                UserFontStyles.ITALIC -> fontName = "Gotham-BookItalic.otf"
                UserFontStyles.SEMIBOLD -> fontName = "gotham.otf"
                UserFontStyles.BOLD -> fontName = "Gotham-Bold.otf"
                UserFontStyles.BLACK -> fontName = "Gtham-Black.otf"
            }
        }
        UserFontFamilies.MONTSERRAT -> {
            when (fontStyle) {
                UserFontStyles.REGULAR -> fontName = "Montserrat-Regular.otf"
                UserFontStyles.ITALIC -> fontName = "Montserrat-Italic.otf"
                UserFontStyles.SEMIBOLD -> fontName = "Montserrat-SemiBold.otf"
            }
        }
    }
    val tf : Typeface = Typeface.createFromAsset(context.assets, "fonts/$fontName")
    typeface = tf
}


//MARK - Debugging
fun AppCompatActivity.writeToScreen(text: Any) {
    Toast.makeText(this, text.toString(), Toast.LENGTH_LONG).show()
}
fun Array<String>.printAll() {
    print("[")
    for (each in 0 until size - 1) {
        print(this[each] + ", ")
    }
    println(this.last() + "]")
}
fun MutableList<String>.printAll() {
    print("[")
    for (each in 0 until size - 1) {
        print(this[each] + ", ")
    }
    println(this.last() + "]")
}


//MARK - Display
/**
 * Takes the runtime device px value and returns the corresponding dp value
 */
fun Int.toDp() : Int {
    return this / Resources.getSystem().getDisplayMetrics().density.toInt()
}

/**
 * Takes platform-wide dp value and returns the corresponding value in px for the runtime device
 */
fun Int.toPx() : Int {
    return this * Resources.getSystem().getDisplayMetrics().density.toInt()
}

fun Long.stringFromTimeInterval() : String {
    var seconds = this/1000
    val hours = seconds/3600
    seconds -= hours * 3600
    val minutes = seconds/60
    seconds -= minutes * 60

    return if (hours > 0) {
        "$hours:" + "%02d".format(minutes) + ":" + "%02d".format(seconds)
    } else {
        "$minutes:" + "%02d".format(seconds)
    }
}

fun Request.Builder.createWithParameters(fromURLString: String, parameters: Map<String,String>) : Request? {
    var urlToSend = "$fromURLString?"
    for (param in parameters) {
        urlToSend += "${param.key}=${URLEncoder.encode(param.value, StandardCharsets.UTF_8.toString())}&"
    }
    urlToSend = urlToSend.dropLast(1)
    urlToSend = urlToSend.replace("\n", "<br>")

    return Request.Builder()
        .url(urlToSend)
        .build()
}


