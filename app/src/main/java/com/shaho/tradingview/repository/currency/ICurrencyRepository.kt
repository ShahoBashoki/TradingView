package com.shaho.tradingview.repository.currency

import com.google.gson.JsonObject
import com.shaho.tradingview.data.model.response.CurrencyDetailResponse
import com.shaho.tradingview.data.model.response.CurrencyResponse
import com.shaho.tradingview.util.Resource
import kotlinx.coroutines.flow.Flow

interface ICurrencyRepository {

    suspend fun getAllCurrencies(
        forceRemote: Boolean = false
    ): Flow<Resource<List<CurrencyResponse>>>

    suspend fun getAllCurrenciesFromRemote(): Flow<Resource<List<CurrencyResponse>>>

    suspend fun getAllCurrenciesFromLocal(): List<CurrencyResponse>

    suspend fun insertAllCurrencies(
        currencies: List<CurrencyResponse>
    )

    suspend fun insertCurrency(
        currency: CurrencyResponse
    )

    suspend fun deleteCurrency(
        currency: CurrencyResponse
    )

    suspend fun deleteAllCurrencies()

    suspend fun getCurrencyDetail(
        currency: String,
        chain: String? = null
    ): Flow<Resource<CurrencyDetailResponse>>

    suspend fun getFiatPrice(
        base: String? = null,
        currencies: String? = null
    ): Flow<Resource<JsonObject>>
}
