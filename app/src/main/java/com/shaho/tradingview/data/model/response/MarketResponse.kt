package com.shaho.tradingview.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_table")
data class MarketResponse(
    @PrimaryKey
    val market: String
)
