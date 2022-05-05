package com.shaho.tradingview.util.extentions

import androidx.fragment.app.Fragment
import java.util.*

fun Fragment.getCurrentTime(): String {
    return Calendar.getInstance().time.toString()
}
