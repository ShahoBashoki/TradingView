package com.shaho.tradingview.repository.histories

import com.shaho.tradingview.data.model.response.CandleResponse
import com.shaho.tradingview.data.model.response.TradeHistoriesResponse
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.CandleTimeType
import kotlinx.coroutines.flow.Flow

interface IHistoryRepository {

    suspend fun getTradeHistories(
        symbol: String
    ): Flow<Resource<List<TradeHistoriesResponse>>>

    suspend fun getAllCandles(
        forceRemote: Boolean = false,
        symbol: String,
        startAt: Long = 0,
        endAt: Long = 0,
        type: CandleTimeType,
    ): Flow<Resource<List<CandleResponse>>>

    suspend fun getMinCandle(
        startAt: Long? = null,
        endAt: Long? = null
    ): Flow<Resource<String>>

    suspend fun getLastTwoCandle(): Flow<Resource<List<CandleResponse>>>

    suspend fun getAllCandlesFromRemote(
        symbol: String,
        startAt: Long = 0,
        endAt: Long = 0,
        type: CandleTimeType,
    ): Flow<Resource<List<CandleResponse>>>

    suspend fun getAllCandlesFromLocal(
        symbol: String,
        startAt: Long = 0,
        endAt: Long = 0,
        type: CandleTimeType,
    ): List<CandleResponse>

    suspend fun insertAllCandles(
        candles: List<CandleResponse>
    )

    suspend fun insertCandle(
        candle: CandleResponse
    )

    suspend fun deleteCandle(
        candle: CandleResponse
    )

    suspend fun deleteAllCandles()
}
