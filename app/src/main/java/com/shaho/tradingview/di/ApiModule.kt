package com.shaho.tradingview.di

import com.shaho.tradingview.BuildConfig
import com.shaho.tradingview.data.remote.AccountApi
import com.shaho.tradingview.data.remote.CurrencyApi
import com.shaho.tradingview.data.remote.HistoryApi
import com.shaho.tradingview.data.remote.OrderApi
import com.shaho.tradingview.data.remote.OrderBookApi
import com.shaho.tradingview.data.remote.SymbolsTickerApi
import com.shaho.tradingview.data.remote.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor, secretFields: SecretFields): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logging)
        }

        builder.interceptors().add(
            Interceptor { chain ->
                val request = chain.request()
                val timestamp = System.currentTimeMillis().toString()
                val requestBuilder = request.newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("KC-API-KEY", secretFields.apiKey)
                    .addHeader("KC-API-SIGN", secretFields.getApiSign(timestamp = timestamp, request = request))
                    .addHeader("KC-API-PASSPHRASE", secretFields.getApiPassphrase())
                    .addHeader("KC-API-TIMESTAMP", timestamp)
                    .addHeader("KC-API-KEY-VERSION", secretFields.apiKeyVersion)
                    .method(request.method, request.body)
                return@Interceptor chain.proceed(requestBuilder.build())
            }
        )
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, secretFields: SecretFields): Retrofit {
        return Retrofit.Builder().client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(secretFields.baseUrl)
            .build()
    }

    @Provides
    @Singleton
    fun provideAccountApi(retrofit: Retrofit): AccountApi {
        return retrofit.create(AccountApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSymbolsTickerApi(retrofit: Retrofit): SymbolsTickerApi {
        return retrofit.create(SymbolsTickerApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderBookApi(retrofit: Retrofit): OrderBookApi {
        return retrofit.create(OrderBookApi::class.java)
    }

    @Provides
    @Singleton
    fun provideHistoryApi(retrofit: Retrofit): HistoryApi {
        return retrofit.create(HistoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi {
        return retrofit.create(CurrencyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrderApi(retrofit: Retrofit): OrderApi {
        return retrofit.create(OrderApi::class.java)
    }
}
