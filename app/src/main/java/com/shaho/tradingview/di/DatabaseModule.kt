package com.shaho.tradingview.di

import android.app.Application
import androidx.room.Room
import com.shaho.tradingview.data.local.TradingViewDatabase
import com.shaho.tradingview.data.local.dao.AccountDao
import com.shaho.tradingview.data.local.dao.CurrencyDao
import com.shaho.tradingview.data.local.dao.HistoryDao
import com.shaho.tradingview.data.local.dao.SymbolsTickerDao
import com.shaho.tradingview.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(application: Application, callback: TradingViewDatabase.Callback): TradingViewDatabase {
        return Room.databaseBuilder(application, TradingViewDatabase::class.java, "trading_view_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    }

    @Provides
    fun provideAccountDao(db: TradingViewDatabase): AccountDao {
        return db.getAccountDao()
    }

    @Provides
    fun provideUserDao(db: TradingViewDatabase): UserDao {
        return db.getUserDao()
    }

    @Provides
    fun provideSymbolsTickerDao(db: TradingViewDatabase): SymbolsTickerDao {
        return db.getSymbolsAndTickerDao()
    }

    @Provides
    fun provideHistoryDao(db: TradingViewDatabase): HistoryDao {
        return db.getHistoryDao()
    }

    @Provides
    fun provideCurrencyDao(db: TradingViewDatabase): CurrencyDao {
        return db.getCurrencyDao()
    }
}
