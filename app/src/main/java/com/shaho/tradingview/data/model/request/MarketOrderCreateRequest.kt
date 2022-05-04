package com.shaho.tradingview.data.model.request

import com.shaho.tradingview.util.enum.OrderType
import com.shaho.tradingview.util.enum.TradeType

data class MarketOrderCreateRequest(
    val clientOid: String,
    val side: String,
    val symbol: String,
    val type: String = OrderType.MARKET.value,
    val remark: String? = null,
    val stp: String? = null,
    val tradeType: TradeType? = null,
    val size: String? = null,
    val funds: String? = null
)
