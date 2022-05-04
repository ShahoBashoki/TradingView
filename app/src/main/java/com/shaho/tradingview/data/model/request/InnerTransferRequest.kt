package com.shaho.tradingview.data.model.request

data class InnerTransferRequest(
    val clientOid: String,
    val currency: String,
    val from: String,
    val to: String,
    val amount: String
)
