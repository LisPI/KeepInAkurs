package com.develop.room530.lis.akursnotify

import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

fun getDateWithOffset(offset: Int, date: Date = Date()): Date {
    val cal = Calendar.getInstance()
    cal.time = date
    cal.add(Calendar.DATE, offset)
    return cal.time
}

fun getDateMinusFormat(date: Date): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale("ru"))
    return sdf.format(date)
}

fun getDateDotFormat(date: Date): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
    return sdf.format(date)
}

fun getDateDDMMFormat(date: Date): String {
    val sdf = SimpleDateFormat("dd.MM", Locale("ru"))
    return sdf.format(date)
}

fun getDateHHMMDDMMFormat(date: Date): String {
    val sdf = SimpleDateFormat("HH:mm dd.MM", Locale("ru"))
    return sdf.format(date)
}

fun getDateDDMMFormatFromLong(millis: Long): String {
    val sdf = SimpleDateFormat("HH:mm dd.MM", Locale("ru"))
    return sdf.format(Date(millis))
}

fun Float.format(digits: Int) = "%.${digits}f".format(this)

fun TextView.setRateComparingState(comparingResult: Float) {
    when {
        comparingResult < 0 -> {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_polygon_1,//R.drawable.ic_triangle_up,
                0
            )
        }
        comparingResult > 0 -> {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_polygon_2,//R.drawable.ic_triangle_down,
                0
            )
        }
        comparingResult == 0F -> {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                0,
                0
            )
        }
    }
}