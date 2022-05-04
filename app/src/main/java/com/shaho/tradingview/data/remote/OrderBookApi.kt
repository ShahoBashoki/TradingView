package com.shaho.tradingview.data.remote

import com.shaho.tradingview.data.model.response.OrderBookResponse
import com.shaho.tradingview.data.model.response.RemoteResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderBookApi {

    @GET("api/v1/market/orderbook/level2_{pieces}")
    suspend fun getPartOrderBook(
        @Path("pieces") pieces: String,
        @Query("symbol") symbol: String
    ): RemoteResponse<OrderBookResponse>

    @GET("api/v3/market/orderbook/level2")
    suspend fun getFullOrderBook(
        @Query("symbol") symbol: String
    ): RemoteResponse<OrderBookResponse>
}
