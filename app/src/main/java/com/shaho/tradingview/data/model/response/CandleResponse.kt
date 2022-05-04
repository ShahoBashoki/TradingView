package com.shaho.tradingview.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candle_table")
data class CandleResponse(
    @PrimaryKey
    val time: String,
    var symbol: String? = null,
    var candle: String? = null,
    val open: String? = null,
    val close: String? = null,
    val high: String? = null,
    val low: String? = null,
    val volume: String? = null,
    val turnover: String? = null
)
