package com.shaho.tradingview.repository.order

import com.shaho.tradingview.data.model.request.LimitOrderCreateRequest
import com.shaho.tradingview.data.model.request.MarketOrderCreateRequest
import com.shaho.tradingview.data.model.response.CancelOrder
import com.shaho.tradingview.data.model.response.CancelOrderByClientOid
import com.shaho.tradingview.data.model.response.OrderCreateResponse
import com.shaho.tradingview.data.model.response.OrderResponse
import com.shaho.tradingview.data.model.response.RemotePaginationResponse
import com.shaho.tradingview.data.remote.OrderApi
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.StatusOrderType
import com.shaho.tradingview.util.enum.TradeType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderApi: OrderApi
) : IOrderRepository {

    override suspend fun createLimitOrder(
        limitOrderCreateRequest: LimitOrderCreateRequest
    ): Flow<Resource<OrderCreateResponse>> = callbackFlow {
        val response = orderApi.createLimitOrder(
            request = limitOrderCreateRequest
        )
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data
                )
            )
        }
        awaitClose { close() }
    }

    override suspend fun createMarketOrder(
        marketOrderCreateRequest: MarketOrderCreateRequest
    ): Flow<Resource<OrderCreateResponse>> = callbackFlow {
        val response = orderApi.createMarketOrder(
            request = marketOrderCreateRequest
        )
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data
                )
            )
        }
        awaitClose { close() }
    }

    override suspend fun cancelAnOrder(
        orderId: String
    ): Flow<Resource<CancelOrder>> = callbackFlow {
        val response = orderApi.cancelAnOrder(
            orderId = orderId
        )
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data
                )
            )
        }
        awaitClose { close() }
    }

    override suspend fun cancelSingleOrderByClientOid(
        clientOid: String
    ): Flow<Resource<CancelOrderByClientOid>> = callbackFlow {
        val response = orderApi.cancelSingleOrderByClientOid(
            clientOid = clientOid
        )
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data
                )
            )
        }
        awaitClose { close() }
    }

    override suspend fun cancelAllOrders(
        symbol: String?,
        tradeType: TradeType?
    ): Flow<Resource<CancelOrder>> = callbackFlow {
        val response = orderApi.cancelAllOrders(
            symbol = symbol,
            tradeType = tradeType?.value
        )
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data
                )
            )
        }
        awaitClose { close() }
    }

    override suspend fun listOrders(
        currentPage: Int?,
        pageSize: Int?,
        status: StatusOrderType,
        symbol: String?,
        side: String?,
        type: String?,
        tradeType: TradeType,
        startAt: Long?,
        endAt: Long?
    ): Flow<Resource<RemotePaginationResponse<OrderResponse>>> = callbackFlow {
        val response = orderApi.listOrders(
            currentPage = currentPage,
            pageSize = pageSize,
            status = status.value,
            symbol = symbol,
            side = side,
            type = type,
            tradeType = tradeType.value,
            startAt = startAt,
            endAt = endAt
        )
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data
                )
            )
        }
        awaitClose { close() }
    }
}
