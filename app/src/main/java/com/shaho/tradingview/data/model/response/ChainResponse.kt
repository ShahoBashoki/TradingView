package com.shaho.tradingview.data.model.response

data class ChainResponse(
    val chainName: String? = null,
    val withdrawalMinSize: String? = null,
    val withdrawalMinFee: String? = null,
    val isWithdrawEnabled: Boolean? = null,
    val isDepositEnabled: Boolean? = null,
    val confirms: Long? = null,
    val contractAddress: String? = null
)
