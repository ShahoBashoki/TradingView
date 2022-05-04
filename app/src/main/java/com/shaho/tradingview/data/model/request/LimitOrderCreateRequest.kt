package com.shaho.tradingview.data.model.request

import com.shaho.tradingview.util.enum.OrderType
import com.shaho.tradingview.util.enum.TradeType

data class LimitOrderCreateRequest(
    val clientOid: String,
    val side: String,
    val symbol: String,
    val type: String = OrderType.LIMIT.value,
    val remark: String? = null,
    val stp: String? = null,
    val tradeType: TradeType? = null,
    val price: String,
    val size: String,
    val timeInForce: String? = null,
    val cancelAfter: Long? = null,
    val postOnly: Boolean? = null,
    val hidden: Boolean? = null,
    val iceberg: Boolean? = null,
    val visibleSize: String? = null
)
