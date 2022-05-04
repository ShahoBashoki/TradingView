package com.shaho.tradingview.data.model.response

data class OrderBookResponse(
    val sequence: String? = null,
    val time: Long? = null,
    val bids: List<List<String>>? = null,
    val asks: List<List<String>>? = null
)
