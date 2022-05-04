package com.shaho.tradingview.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shaho.tradingview.data.model.response.CandleResponse

@Dao
interface HistoryDao {

    @Query(
        "SELECT * FROM candle_table " +
            "where (symbol LIKE :symbol)" +
            " AND ((:startAt IS NULL AND :endAt IS NULL ) OR time BETWEEN :startAt AND :endAt)" +
            " AND (:type LIKE :type)" +
            " ORDER BY time ASC"
    )
    suspend fun getCandles(
        symbol: String,
        startAt: Long? = null,
        endAt: Long? = null,
        type: String
    ): List<CandleResponse>

    @Query(
        "SELECT MIN(low) FROM candle_table " +
            "where ((:startAt IS NULL AND :endAt IS NULL ) OR time BETWEEN :startAt AND :endAt)"
    )
    suspend fun getMinCandle(
        startAt: Long? = null,
        endAt: Long? = null
    ): String

    @Query(
        "SELECT * FROM candle_table " +
            "LIMIT 2"
    )
    suspend fun getLastTwoCandle(): List<CandleResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCandles(candles: List<CandleResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandle(candle: CandleResponse)

    @Delete
    suspend fun deleteCandle(candle: CandleResponse)

    @Query("DELETE FROM candle_table")
    suspend fun deleteAllCandles()
}
