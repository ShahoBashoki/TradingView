package com.shaho.tradingview.repository.symbol

import com.shaho.tradingview.data.local.dao.SymbolsTickerDao
import com.shaho.tradingview.data.model.response.MarketResponse
import com.shaho.tradingview.data.model.response.SymbolResponse
import com.shaho.tradingview.data.model.response.SymbolTickResponse
import com.shaho.tradingview.data.model.response.TickerResponse
import com.shaho.tradingview.data.remote.SymbolsTickerApi
import com.shaho.tradingview.util.Resource
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolsTickerRepository @Inject constructor(
    private val symbolsTickerApi: SymbolsTickerApi,
    private val symbolsTickerDao: SymbolsTickerDao
) : ISymbolsTickerRepository {

    override suspend fun getAllSymbols(forceRemote: Boolean, market: String?): Flow<Resource<List<SymbolResponse>>> = callbackFlow {
        if (forceRemote) {
            getAllSymbolsFromRemote(
                market = market
            ).catch {
                trySend(Resource.Failure(message = ""))
            }.collect {
                when (it) {
                    is Resource.Failure -> trySend(Resource.Failure(message = ""))
                    Resource.Loading -> trySend(Resource.Loading)
                    is Resource.Success -> {
                        it.data?.let { listSymbol ->
                            insertAllSymbols(listSymbol)
                            trySend(Resource.Success(getAllSymbolsFromLocal(market = market)))
                        } ?: run {
                            trySend(Resource.Failure(message = "data is null"))
                        }
                    }
                }
            }
        } else {
            getAllSymbolsFromLocal(market = market).apply {
                if (this.isNullOrEmpty())
                    getAllSymbols(
                        forceRemote = true,
                        market = market
                    ).collect {
                        trySend(it)
                    }
                else
                    trySend(Resource.Success(getAllSymbolsFromLocal(market = market)))
            }
        }
        awaitClose { cancel() }
    }

    override suspend fun getAllSymbolsFromRemote(market: String?): Flow<Resource<List<SymbolResponse>>> = callbackFlow {
        val response = symbolsTickerApi.getSymbolsList(
            market = market
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

    override suspend fun getAllSymbolsFromLocal(market: String?): List<SymbolResponse> = symbolsTickerDao.getSymbols(
        market = market
    )

    override suspend fun getSymbolFromLocal(symbol: String): SymbolResponse = symbolsTickerDao.getSymbol(symbol = symbol)

    override suspend fun insertAllSymbols(symbols: List<SymbolResponse>) = symbolsTickerDao.insertAllSymbols(symbols)

    override suspend fun insertSymbol(symbol: SymbolResponse) = symbolsTickerDao.insertSymbol(symbol)

    override suspend fun deleteSymbol(symbol: SymbolResponse) = symbolsTickerDao.deleteSymbol(symbol)

    override suspend fun deleteAllSymbols() = symbolsTickerDao.deleteAllSymbols()

    override suspend fun getTicker(symbol: String): Flow<Resource<TickerResponse>> = callbackFlow {
        val response = symbolsTickerApi.getTicker(
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

    override suspend fun getAllTickers(forceRemote: Boolean): Flow<Resource<List<SymbolTickResponse>>> = callbackFlow {
        if (forceRemote) {
            getAllTickersFromRemote().catch {
                trySend(Resource.Failure(message = ""))
            }.collect {
                when (it) {
                    is Resource.Failure -> trySend(Resource.Failure(message = ""))
                    Resource.Loading -> trySend(Resource.Loading)
                    is Resource.Success -> {
                        it.data?.let { listTicker ->
                            insertAllTickers(listTicker)
                            trySend(Resource.Success(getAllTickersFromLocal()))
                        } ?: run {
                            trySend(Resource.Failure(message = "data is null"))
                        }
                    }
                }
            }
        } else {
            trySend(Resource.Success(getAllTickersFromLocal()))
        }
        awaitClose { cancel() }
    }

    override suspend fun getAllTickersFromRemote(): Flow<Resource<List<SymbolTickResponse>>> = callbackFlow {
        val response = symbolsTickerApi.getAllTickers()
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data?.ticker
                )
            )
        }
        awaitClose { close() }
    }

    override suspend fun getAllTickersFromLocal(): List<SymbolTickResponse> = symbolsTickerDao.getTickers()

    override suspend fun insertAllTickers(tickers: List<SymbolTickResponse>) = symbolsTickerDao.insertAllTickers(tickers)

    override suspend fun insertTicker(ticker: SymbolTickResponse) = symbolsTickerDao.insertTicker(ticker)

    override suspend fun deleteTicker(ticker: SymbolTickResponse) = symbolsTickerDao.deleteTicker(ticker)

    override suspend fun deleteAllTickers() = symbolsTickerDao.deleteAllTickers()

    override suspend fun get24hrStats(symbol: String): Flow<Resource<SymbolTickResponse>> = callbackFlow {
        val response = symbolsTickerApi.get24hrStats(
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

    override suspend fun getAllMarkets(
        forceRemote: Boolean
    ): Flow<Resource<List<MarketResponse>>> = callbackFlow {
        if (forceRemote) {
            getAllMarketsFromRemote().catch {
                trySend(Resource.Failure(message = ""))
            }.collect {
                when (it) {
                    is Resource.Failure -> trySend(Resource.Failure(message = ""))
                    Resource.Loading -> trySend(Resource.Loading)
                    is Resource.Success -> {
                        it.data?.let { listMarket ->
                            insertAllMarkets(listMarket)
                            trySend(Resource.Success(getAllMarketsFromLocal()))
                        } ?: run {
                            trySend(Resource.Failure(message = "data is null"))
                        }
                    }
                }
            }
        } else {
            trySend(Resource.Success(getAllMarketsFromLocal()))
        }
        awaitClose { cancel() }
    }

    override suspend fun getAllMarketsFromRemote(): Flow<Resource<List<MarketResponse>>> = callbackFlow {
        val response = symbolsTickerApi.getMarketList()
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data?.map { MarketResponse(it) }
                )
            )
        }
        awaitClose { close() }
    }

    override suspend fun getAllMarketsFromLocal(): List<MarketResponse> = symbolsTickerDao.getMarkets()

    override suspend fun insertAllMarkets(markets: List<MarketResponse>) = symbolsTickerDao.insertAllMarkets(markets)

    override suspend fun insertMarket(market: MarketResponse) = symbolsTickerDao.insertMarket(market)

    override suspend fun deleteMarket(market: MarketResponse) = symbolsTickerDao.deleteMarket(market)

    override suspend fun deleteAllMarkets() = symbolsTickerDao.deleteAllMarkets()
}
