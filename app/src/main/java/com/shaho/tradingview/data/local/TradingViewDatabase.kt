package com.shaho.tradingview.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shaho.tradingview.data.local.dao.AccountDao
import com.shaho.tradingview.data.local.dao.CurrencyDao
import com.shaho.tradingview.data.local.dao.HistoryDao
import com.shaho.tradingview.data.local.dao.SymbolsTickerDao
import com.shaho.tradingview.data.local.dao.UserDao
import com.shaho.tradingview.data.model.response.AccountLedgerResponse
import com.shaho.tradingview.data.model.response.AccountResponse
import com.shaho.tradingview.data.model.response.CandleResponse
import com.shaho.tradingview.data.model.response.CurrencyResponse
import com.shaho.tradingview.data.model.response.MarketResponse
import com.shaho.tradingview.data.model.response.SymbolResponse
import com.shaho.tradingview.data.model.response.SymbolTickResponse
import com.shaho.tradingview.data.model.response.UserResponse
import com.shaho.tradingview.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Provider

@Database(
    entities = [
        UserResponse::class,
        AccountResponse::class,
        AccountLedgerResponse::class,
        SymbolResponse::class,
        SymbolTickResponse::class,
        MarketResponse::class,
        CandleResponse::class,
        CurrencyResponse::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class TradingViewDatabase : RoomDatabase() {

    abstract fun getAccountDao(): AccountDao
    abstract fun getUserDao(): UserDao
    abstract fun getSymbolsAndTickerDao(): SymbolsTickerDao
    abstract fun getHistoryDao(): HistoryDao
    abstract fun getCurrencyDao(): CurrencyDao

    class Callback @Inject constructor(
        private val database: Provider<TradingViewDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback()
}
