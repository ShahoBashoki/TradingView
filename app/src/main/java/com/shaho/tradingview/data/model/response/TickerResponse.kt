package com.shaho.tradingview.data.model.response

data class TickerResponse(
    val sequence: String? = null,
    val bestAsk: String? = null,
    val size: String? = null,
    val price: String? = null,
    val bestBidSize: String? = null,
    val bestBid: String? = null,
    val bestAskSize: String? = null,
    val time: Long? = null
)
