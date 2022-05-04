package com.shaho.tradingview.repository.orderbook

import com.shaho.tradingview.data.model.response.OrderBookResponse
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.OrderBookPiecesType
import kotlinx.coroutines.flow.Flow

interface IOrderBookRepository {

    suspend fun getPartOrderBook(
        pieces: OrderBookPiecesType,
        symbol: String
    ): Flow<Resource<OrderBookResponse>>

    suspend fun getFullOrderBook(
        symbol: String
    ): Flow<Resource<OrderBookResponse>>
}
