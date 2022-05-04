package com.shaho.tradingview.data.model.response

data class CurrencyDetailResponse(
    val currency: String? = null,
    val name: String? = null,
    val fullName: String? = null,
    val precision: Long? = null,
    val confirms: Long? = null,
    val contractAddress: String? = null,
    val withdrawalMinSize: String? = null,
    val withdrawalMinFee: String? = null,
    val isMarginEnabled: Boolean? = null,
    val isDebitEnabled: Boolean? = null,
    val chains: List<ChainResponse>? = null
)
