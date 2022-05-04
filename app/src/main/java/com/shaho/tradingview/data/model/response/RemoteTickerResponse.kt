package com.shaho.tradingview.data.model.response

data class RemoteTickerResponse<T>(
    val time: Long? = null,
    val ticker: List<T>? = null
)
