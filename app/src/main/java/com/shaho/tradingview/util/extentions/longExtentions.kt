package com.shaho.tradingview.util.extentions

fun Long.takeOfHoursAgo(hour: Int): Long {
    val changeHourToMillisecond = hour * 60 * 60 * 1000
    return this - changeHourToMillisecond
}
