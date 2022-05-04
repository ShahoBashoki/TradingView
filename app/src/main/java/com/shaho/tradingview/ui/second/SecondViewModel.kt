package com.shaho.tradingview.ui.second

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.shaho.tradingview.data.model.request.LimitOrderCreateRequest
import com.shaho.tradingview.data.model.request.MarketOrderCreateRequest
import com.shaho.tradingview.repository.account.AccountRepository
import com.shaho.tradingview.repository.histories.HistoryRepository
import com.shaho.tradingview.repository.order.OrderRepository
import com.shaho.tradingview.repository.symbol.SymbolsTickerRepository
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.AccountType
import com.shaho.tradingview.util.enum.CandleTimeType
import com.shaho.tradingview.util.enum.OrderSideType
import com.shaho.tradingview.util.extentions.takeOfHoursAgo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SecondViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val symbolsTickerRepository: SymbolsTickerRepository,
    private val orderRepository: OrderRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {
    private val defaultBaseMaxSize = "99999999"

    fun getAllSymbols() = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            symbolsTickerRepository.getAllSymbols().collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    private suspend fun getSymbol(
        symbol: String
    ) = symbolsTickerRepository.getSymbolFromLocal(symbol = symbol)

    fun getAllCandles(
        symbol: String,
        startAt: Long = 0,
        endAt: Long = 0,
        type: CandleTimeType
    ) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            historyRepository.getAllCandles(
                forceRemote = true,
                symbol = symbol,
                startAt = startAt,
                endAt = endAt,
                type = type
            ).collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    fun getMinOpenCandle(
        hourAgo: Int
    ) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            historyRepository.getMinCandle(
                startAt = System.currentTimeMillis().takeOfHoursAgo(hourAgo),
                endAt = System.currentTimeMillis()
            ).collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    fun getLastTwoCandle() = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            historyRepository.getLastTwoCandle().collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    fun createBuyMarketOrder(
        symbol: String
    ) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            orderRepository.createMarketOrder(
                marketOrderCreateRequest = MarketOrderCreateRequest(
                    clientOid = UUID.randomUUID().toString(),
                    side = OrderSideType.BUY.value,
                    symbol = symbol,
                    size = getSymbol(
                        symbol = symbol
                    ).baseMaxSize ?: defaultBaseMaxSize
                )
            ).collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    fun createSellMarketOrder(
        symbol: String
    ) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            orderRepository.createMarketOrder(
                marketOrderCreateRequest = MarketOrderCreateRequest(
                    clientOid = UUID.randomUUID().toString(),
                    side = OrderSideType.SELL.value,
                    symbol = symbol,
                    size = getSymbol(
                        symbol = symbol
                    ).baseMaxSize ?: defaultBaseMaxSize
                )
            ).collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    fun createSellLimitOrder(
        symbol: String,
        price: String,
        currency: String
    ) = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            orderRepository.createLimitOrder(
                limitOrderCreateRequest = LimitOrderCreateRequest(
                    clientOid = UUID.randomUUID().toString(),
                    side = OrderSideType.SELL.value,
                    symbol = symbol,
                    price = price,
                    size = "%.${getSymbol(symbol = symbol).baseIncrement?.split('.')?.get(1)?.length}f".format(getAccountsFromLocal(currency).first().balance?.toDouble())
                )
            ).collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    fun cancelAllOrders() = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            orderRepository.cancelAllOrders().collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    fun getAllAccounts() = liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
        emit(Resource.Loading)
        try {
            accountRepository.getAllAccounts(
                forceRemote = true
            ).collect {
                emit(it)
            }
        } catch (e: Exception) {
            emit(Resource.Failure(message = ""))
        }
    }

    private suspend fun getAccountsFromLocal(
        currency: String
    ) = accountRepository.getAllAccountsFromLocal(
        accountType = AccountType.TRADE,
        accountCurrency = currency
    )
}
