package com.shaho.tradingview.repository.currency

import com.google.gson.JsonObject
import com.shaho.tradingview.data.local.dao.CurrencyDao
import com.shaho.tradingview.data.model.response.CurrencyDetailResponse
import com.shaho.tradingview.data.model.response.CurrencyResponse
import com.shaho.tradingview.data.remote.CurrencyApi
import com.shaho.tradingview.util.Resource
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val currencyDao: CurrencyDao
) : ICurrencyRepository {

    override suspend fun getAllCurrencies(
        forceRemote: Boolean
    ): Flow<Resource<List<CurrencyResponse>>> = callbackFlow {
        if (forceRemote) {
            getAllCurrenciesFromRemote().catch {
                trySend(Resource.Failure(message = ""))
            }.collect {
                when (it) {
                    is Resource.Failure -> trySend(Resource.Failure(message = ""))
                    Resource.Loading -> trySend(Resource.Loading)
                    is Resource.Success -> {
                        it.data?.let { listCurrencies ->
                            insertAllCurrencies(listCurrencies)
                            trySend(
                                Resource.Success(
                                    getAllCurrenciesFromLocal()
                                )
                            )
                        } ?: run {
                            trySend(Resource.Failure(message = "data is null"))
                        }
                    }
                }
            }
        } else {
            trySend(
                Resource.Success(
                    getAllCurrenciesFromLocal()
                )
            )
        }
        awaitClose { cancel() }
    }

    override suspend fun getAllCurrenciesFromRemote(): Flow<Resource<List<CurrencyResponse>>> = callbackFlow {
        val response = currencyApi.getCurrencies()
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

    override suspend fun getAllCurrenciesFromLocal(): List<CurrencyResponse> = currencyDao.getCurrencies()

    override suspend fun insertAllCurrencies(
        currencies: List<CurrencyResponse>
    ) = currencyDao.insertAllCurrencies(currencies)

    override suspend fun insertCurrency(
        currency: CurrencyResponse
    ) = currencyDao.insertCurrency(currency)

    override suspend fun deleteCurrency(
        currency: CurrencyResponse
    ) = currencyDao.deleteCurrency(currency)

    override suspend fun deleteAllCurrencies() = currencyDao.deleteAllCurrencies()

    override suspend fun getCurrencyDetail(
        currency: String,
        chain: String?
    ): Flow<Resource<CurrencyDetailResponse>> = callbackFlow {
        val response = currencyApi.getCurrencyDetail(
            currency = currency,
            chain = chain
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

    override suspend fun getFiatPrice(
        base: String?,
        currencies: String?
    ): Flow<Resource<JsonObject>>  = callbackFlow {
        val response = currencyApi.getFiatPrice(
            base = base,
            currencies = currencies
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
