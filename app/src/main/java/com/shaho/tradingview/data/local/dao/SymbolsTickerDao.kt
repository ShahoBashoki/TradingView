package com.shaho.tradingview.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shaho.tradingview.data.model.response.MarketResponse
import com.shaho.tradingview.data.model.response.SymbolResponse
import com.shaho.tradingview.data.model.response.SymbolTickResponse

@Dao
interface SymbolsTickerDao {

    @Query(
        "SELECT * FROM symbol_table " +
            "where (:market IS NULL OR market LIKE :market)"
    )
    suspend fun getSymbols(
        market: String?
    ): List<SymbolResponse>

    @Query(
        "SELECT * FROM symbol_table " +
            "where symbol = :symbol"
    )
    suspend fun getSymbol(
        symbol: String
    ): SymbolResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSymbols(symbols: List<SymbolResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymbol(symbol: SymbolResponse)

    @Delete
    suspend fun deleteSymbol(symbol: SymbolResponse)

    @Query("DELETE FROM symbol_table")
    suspend fun deleteAllSymbols()

    @Query("SELECT * FROM symbol_tick_table")
    suspend fun getTickers(): List<SymbolTickResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTickers(tickers: List<SymbolTickResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicker(ticker: SymbolTickResponse)

    @Delete
    suspend fun deleteTicker(ticker: SymbolTickResponse)

    @Query("DELETE FROM symbol_tick_table")
    suspend fun deleteAllTickers()

    @Query("SELECT * FROM market_table")
    suspend fun getMarkets(): List<MarketResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMarkets(markets: List<MarketResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarket(market: MarketResponse)

    @Delete
    suspend fun deleteMarket(market: MarketResponse)

    @Query("DELETE FROM market_table")
    suspend fun deleteAllMarkets()
}
