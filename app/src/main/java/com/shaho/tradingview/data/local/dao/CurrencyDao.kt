package com.shaho.tradingview.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shaho.tradingview.data.model.response.CurrencyResponse

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency_table")
    suspend fun getCurrencies(): List<CurrencyResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCurrencies(currencies: List<CurrencyResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: CurrencyResponse)

    @Delete
    suspend fun deleteCurrency(currency: CurrencyResponse)

    @Query("DELETE FROM currency_table")
    suspend fun deleteAllCurrencies()
}
