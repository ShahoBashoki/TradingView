package com.shaho.tradingview.repository.symbol

import com.shaho.tradingview.data.model.response.MarketResponse
import com.shaho.tradingview.data.model.response.SymbolResponse
import com.shaho.tradingview.data.model.response.SymbolTickResponse
import com.shaho.tradingview.data.model.response.TickerResponse
import com.shaho.tradingview.util.Resource
import kotlinx.coroutines.flow.Flow

interface ISymbolsTickerRepository {

    suspend fun getAllSymbols(
        forceRemote: Boolean = false,
        market: String? = null
    ): Flow<Resource<List<SymbolResponse>>>

    suspend fun getAllSymbolsFromRemote(
        market: String? = null
    ): Flow<Resource<List<SymbolResponse>>>

    suspend fun getAllSymbolsFromLocal(
        market: String? = null
    ): List<SymbolResponse>

    suspend fun getSymbolFromLocal(
        symbol: String
    ): SymbolResponse

    suspend fun insertAllSymbols(
        symbols: List<SymbolResponse>
    )

    suspend fun insertSymbol(
        symbol: SymbolResponse
    )

    suspend fun deleteSymbol(
        symbol: SymbolResponse
    )

    suspend fun deleteAllSymbols()

    suspend fun getTicker(
        symbol: String
    ): Flow<Resource<TickerResponse>>

    suspend fun getAllTickers(
        forceRemote: Boolean = false
    ): Flow<Resource<List<SymbolTickResponse>>>

    suspend fun getAllTickersFromRemote(): Flow<Resource<List<SymbolTickResponse>>>

    suspend fun getAllTickersFromLocal(): List<SymbolTickResponse>

    suspend fun insertAllTickers(
        tickers: List<SymbolTickResponse>
    )

    suspend fun insertTicker(
        ticker: SymbolTickResponse
    )

    suspend fun deleteTicker(
        ticker: SymbolTickResponse
    )

    suspend fun deleteAllTickers()

    suspend fun get24hrStats(
        symbol: String
    ): Flow<Resource<SymbolTickResponse>>

    suspend fun getAllMarkets(
        forceRemote: Boolean = false
    ): Flow<Resource<List<MarketResponse>>>

    suspend fun getAllMarketsFromRemote(): Flow<Resource<List<MarketResponse>>>

    suspend fun getAllMarketsFromLocal(): List<MarketResponse>

    suspend fun insertAllMarkets(
        markets: List<MarketResponse>
    )

    suspend fun insertMarket(
        market: MarketResponse
    )

    suspend fun deleteMarket(
        market: MarketResponse
    )

    suspend fun deleteAllMarkets()
}
