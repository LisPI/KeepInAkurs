package com.develop.room530.lis.akursnotify

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