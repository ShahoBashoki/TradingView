package com.shaho.tradingview.util.extentions

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.fragment.app.Fragment
import java.util.*

fun Context.getCurrentTime(): String {
    return Calendar.getInstance().time.toString()
}
