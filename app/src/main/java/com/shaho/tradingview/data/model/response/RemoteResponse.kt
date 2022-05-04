package com.shaho.tradingview.data.model.response

data class RemoteResponse<T>(
    val code: String? = null,
    val msg: String? = null,
    val data: T? = null
)
