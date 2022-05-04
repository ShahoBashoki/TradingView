package com.shaho.tradingview.data.remote

import com.shaho.tradingview.data.model.response.RemoteResponse
import com.shaho.tradingview.data.model.response.TradeHistoriesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface HistoryApi {

    @GET("api/v1/market/histories")
    suspend fun getTradeHistories(
        @Query("symbol") symbol: String
    ): RemoteResponse<List<TradeHistoriesResponse>>

    @GET("api/v1/market/candles")
    suspend fun getCandles(
        @Query("symbol") symbol: String,
        @Query("startAt") startAt: Long = 0,
        @Query("endAt") endAt: Long = 0,
        @Query("type") type: String
    ): RemoteResponse<List<List<String>>>
}
