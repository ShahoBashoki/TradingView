package com.shaho.tradingview.repository.orderbook

import com.shaho.tradingview.data.model.response.OrderBookResponse
import com.shaho.tradingview.data.remote.OrderBookApi
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.OrderBookPiecesType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderBookRepository @Inject constructor(
    private val orderBookApi: OrderBookApi
) : IOrderBookRepository {

    override suspend fun getPartOrderBook(
        pieces: OrderBookPiecesType,
        symbol: String
    ): Flow<Resource<OrderBookResponse>> = callbackFlow {
        val response = orderBookApi.getPartOrderBook(
            pieces = pieces.value,
            symbol = symbol
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

    override suspend fun getFullOrderBook(
        symbol: String
    ): Flow<Resource<OrderBookResponse>> = callbackFlow {
        val response = orderBookApi.getFullOrderBook(
            symbol = symbol
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
