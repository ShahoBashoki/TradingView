package com.shaho.tradingview.data.remote

import com.shaho.tradingview.data.model.response.RemoteResponse
import com.shaho.tradingview.data.model.response.RemoteTickerResponse
import com.shaho.tradingview.data.model.response.SymbolResponse
import com.shaho.tradingview.data.model.response.SymbolTickResponse
import com.shaho.tradingview.data.model.response.TickerResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SymbolsTickerApi {

    @GET("api/v1/symbols")
    suspend fun getSymbolsList(
        @Query("market") market: String? = null
    ): RemoteResponse<List<SymbolResponse>>

    @GET("api/v1/market/orderbook/level1")
    suspend fun getTicker(
        @Query("symbol") symbol: String
    ): RemoteResponse<TickerResponse>

    @GET("api/v1/market/allTickers")
    suspend fun getAllTickers(): RemoteResponse<RemoteTickerResponse<SymbolTickResponse>>

    @GET("api/v1/market/stats")
    suspend fun get24hrStats(
        @Query("symbol") symbol: String
    ): RemoteResponse<SymbolTickResponse>

    @GET("api/v1/markets")
    suspend fun getMarketList(): RemoteResponse<List<String>>
}
