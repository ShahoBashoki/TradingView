package com.shaho.tradingview.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "symbol_tick_table")
data class SymbolTickResponse(
    @PrimaryKey
    val symbol: String,
    val name: String? = null,
    val buy: String? = null,
    val sell: String? = null,
    val changeRate: String? = null,
    val changePrice: String? = null,
    val high: String? = null,
    val low: String? = null,
    val vol: String? = null,
    val volValue: String? = null,
    val last: String? = null,
    val averagePrice: String? = null,
    val takerFeeRate: String? = null,
    val makerFeeRate: String? = null,
    val takerCoefficient: String? = null,
    val makerCoefficient: String? = null,
    val time: Long? = null
)
