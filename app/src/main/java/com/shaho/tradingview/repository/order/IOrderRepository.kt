package com.shaho.tradingview.repository.order

import com.shaho.tradingview.data.model.request.LimitOrderCreateRequest
import com.shaho.tradingview.data.model.request.MarketOrderCreateRequest
import com.shaho.tradingview.data.model.response.CancelOrder
import com.shaho.tradingview.data.model.response.CancelOrderByClientOid
import com.shaho.tradingview.data.model.response.OrderCreateResponse
import com.shaho.tradingview.data.model.response.OrderResponse
import com.shaho.tradingview.data.model.response.RemotePaginationResponse
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.StatusOrderType
import com.shaho.tradingview.util.enum.TradeType
import kotlinx.coroutines.flow.Flow

interface IOrderRepository {

    suspend fun createLimitOrder(
        limitOrderCreateRequest: LimitOrderCreateRequest
    ): Flow<Resource<OrderCreateResponse>>

    suspend fun createMarketOrder(
        marketOrderCreateRequest: MarketOrderCreateRequest
    ): Flow<Resource<OrderCreateResponse>>

    suspend fun cancelAnOrder(
        orderId: String
    ): Flow<Resource<CancelOrder>>

    suspend fun cancelSingleOrderByClientOid(
        clientOid: String
    ): Flow<Resource<CancelOrderByClientOid>>

    suspend fun cancelAllOrders(
        symbol: String? = null,
        tradeType: TradeType? = null,
    ): Flow<Resource<CancelOrder>>

    suspend fun listOrders(
        currentPage: Int? = null,
        pageSize: Int? = null,
        status: StatusOrderType = StatusOrderType.ACTIVE,
        symbol: String? = null,
        side: String? = null,
        type: String? = null,
        tradeType: TradeType = TradeType.TRADE,
        startAt: Long? = null,
        endAt: Long? = null,
    ): Flow<Resource<RemotePaginationResponse<OrderResponse>>>
}
