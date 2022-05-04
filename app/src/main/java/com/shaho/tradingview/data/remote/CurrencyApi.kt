package com.shaho.tradingview.data.remote

import com.google.gson.JsonObject
import com.shaho.tradingview.data.model.response.CurrencyDetailResponse
import com.shaho.tradingview.data.model.response.CurrencyResponse
import com.shaho.tradingview.data.model.response.RemoteResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApi {

    @GET("api/v1/currencies")
    suspend fun getCurrencies(): RemoteResponse<List<CurrencyResponse>>

    @GET("api/v2/currencies/{currency}")
    suspend fun getCurrencyDetail(
        @Path("currency") currency: String,
        @Query("chain") chain: String? = null
    ): RemoteResponse<CurrencyDetailResponse>

    @GET("api/v1/prices")
    suspend fun getFiatPrice(
        @Query("base") base: String? = null,
        @Query("currencies") currencies: String? = null
    ): RemoteResponse<JsonObject>
}
