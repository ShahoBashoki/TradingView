package com.shaho.tradingview.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symbol_table")
data class SymbolResponse(
    @PrimaryKey
    val symbol: String,
    val name: String? = null,
    val baseCurrency: String? = null,
    val quoteCurrency: String? = null,
    val market: String? = null,
    val baseMinSize: String? = null,
    val quoteMinSize: String? = null,
    val baseMaxSize: String? = null,
    val quoteMaxSize: String? = null,
    val baseIncrement: String? = null,
    val quoteIncrement: String? = null,
    val priceIncrement: String? = null,
    val feeCurrency: String? = null,
    val priceLimitRate: String? = null,
    val enableTrading: Boolean? = null,
    val isMarginEnabled: Boolean? = null
)
