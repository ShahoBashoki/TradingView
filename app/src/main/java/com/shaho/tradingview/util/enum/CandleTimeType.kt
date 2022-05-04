package com.shaho.tradingview.util.enum

enum class CandleTimeType(val value: String, val second: Long) {
    MIN_1("1min", 60),
    MIN_3("3min", 180),
    MIN_5("5min", 300),
    MIN_15("15min", 900),
    MIN_30("30min", 1800),
    HOUR_1("1hour", 3600),
    HOUR_2("2hour", 7200),
    HOUR_4("4hour", 14400),
    HOUR_6("6hour", 21600),
    HOUR_8("8hour", 28800),
    HOUR_12("12hour", 43200),
    DAY_1("1day", 86400),
    WEEK_1("1week", 604800)
}
