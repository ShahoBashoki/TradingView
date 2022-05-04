package com.shaho.tradingview.ui.first

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.shaho.tradingview.data.model.request.LimitOrderCreateRequest
import com.shaho.tradingview.data.model.request.MarketOrderCreateRequest
import com.shaho.tradingview.data.model.response.AccountResponse
import com.shaho.tradingview.repository.account.AccountRepository
import com.shaho.tradingview.repository.currency.CurrencyRepository
import com.shaho.tradingview.repository.histories.HistoryRepository
import com.shaho.tradingview.repository.order.OrderRepository
import com.shaho.tradingview.repository.orderbook.OrderBookRepository
import com.shaho.tradingview.repository.symbol.SymbolsTickerRepository
import com.shaho.tradingview.repository.user.UserRepository
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.OrderSideType
import com.shaho.tradingview.util.enum.OrderType
import com.shaho.tradingview.util.enum.StatusOrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FirstViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userRepository: UserRepository,
    private val symbolsTickerRepository: SymbolsTickerRepository,
    private val historyRepository: HistoryRepository,
    private val orderBookRepository: OrderBookRepository,
    private val currencyRepository: CurrencyRepository,
    private val orderRepository: OrderRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val account: MutableLiveData<Resource<AccountResponse>> = MutableLiveData()

//    fun getAccounts() = viewModelScope.launch {
//        safeGetAccounts()
//    }

    fun getAccounts() =
        liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(Resource.Loading)
            try {
//                userRepository.getAllUsers(true).collect {
//                    emit(it)
//                }
//                accountRepository.getAllAccounts(
//                    forceRemote = true
//                ).collect {
//                    emit(it)
//                }
//                accountRepository.createAccount(
//                    createAccountRequest = CreateAccountRequest(
//                        currency = "USDT",
//                        type = AccountType.TRADE.value
//                    )
//                ).collect {
//                    emit(it)
//                }
//                accountRepository.getAccount(
//                    accountId = "61900af4d5a5cf0001278f4b"
//                ).collect {
//                    emit(it)
//                }
//                accountRepository.getAllAccountLedgers(
//                    forceRemote = true
//                ).collect {
//                    emit(it)
//                }
//                accountRepository.getTransferable(
//                    accountCurrency = "USDT",
//                    accountType = AccountType.MAIN
//                ).collect {
//                    emit(it)
//                }
//                accountRepository.innerTransfer(
//                    InnerTransferRequest(
//                        clientOid = UUID.randomUUID().toString(),
//                        currency = "USDT",
//                        from = AccountType.MAIN.value,
//                        to = AccountType.TRADE.value,
//                        amount = "30000"
//                    )
//                ).collect {
//                    emit(it)
//                }
//                symbolsTickerRepository.getAllSymbols(
//                    forceRemote = true
//                ).collect {
//                    emit(it)
//                }
//                symbolsTickerRepository.getTicker(
//                    symbol = "BTC-USDT"
//                ).collect {
//                    emit(it)
//                }
//                symbolsTickerRepository.getAllTickers(
//                    forceRemote = true
//                ).collect {
//                    emit(it)
//                }
//                symbolsTickerRepository.get24hrStats(
//                    symbol = "BTC-USDT"
//                ).collect {
//                    emit(it)
//                }
//                symbolsTickerRepository.getAllMarkets(
//                    forceRemote = true
//                ).collect {
//                    emit(it)
//                }
//                orderBookRepository.getPartOrderBook(
//                    pieces = OrderBookPiecesType.PIECES_100,
//                    symbol = "BTC-USDT"
//                ).collect {
//                    emit(it)
//                }
//                orderBookRepository.getFullOrderBook(
//                    symbol = "BTC-USDT"
//                ).collect {
//                    emit(it)
//                }
//                symbolsTickerRepository.getTradeHistories(
//                    symbol = "BTC-USDT"
//                ).collect {
//                    emit(it)
//                }
//                historyRepository.getAllCandles(
//                    forceRemote = true,
//                    symbol = "ETH-BTC",
//                    startAt = 1646035516,
//                    endAt = 1651133116,
//                    type = CandleTimeType.MIN_5
//                ).collect {
//                    emit(it)
//                }
//                historyRepository.getMinCandle(
//                    startAt = 1646035800,
//                    endAt = 1646036400
//                ).collect {
//                    emit(it)
//                }
//                currencyRepository.getAllCurrencies(
//                    forceRemote = true
//                ).collect {
//                    emit(it)
//                }
//                currencyRepository.getCurrencyDetail(
//                    currency = "BTC"
//                ).collect {
//                    emit(it)
//                }
//                currencyRepository.getFiatPrice().collect {
//                    emit(it)
//                }
//                orderRepository.createLimitOrder(
//                    limitOrderCreateRequest = LimitOrderCreateRequest(
//                        clientOid = UUID.randomUUID().toString(),
//                        side = OrderSideType.SELL.value,
//                        symbol = "TOWER-USDT",
//                        price = "0.1",
//                        size = "242.3102"
//                    )
//                ).collect {
//                    emit(it)
//                }
//                orderRepository.createMarketOrder(
//                    marketOrderCreateRequest = MarketOrderCreateRequest(
//                        clientOid = UUID.randomUUID().toString(),
//                        side = OrderSideType.BUY.value,
//                        symbol = "TOWER-USDT",
//                        size = "300"
//                    )
//                ).collect {
//                    emit(it)
//                }
//                orderRepository.listOrders(
//                    status = StatusOrderType.ACTIVE
//                ).collect {
//                    emit(it)
//                }
//                orderRepository.cancelAnOrder(
//                    orderId = "626fe261e2ef7400019794fd"
//                ).collect {
//                    emit(it)
//                }
//                orderRepository.cancelSingleOrderByClientOid(
//                    clientOid = "837a2832-6400-40c4-9d3f-eea03962e289"
//                ).collect {
//                    emit(it)
//                }
//                orderRepository.cancelAllOrders().collect {
//                    emit(it)
//                }
            } catch (e: Exception) {
                emit(Resource.Failure(message = e.message ?: "empty error"))
            }
        }

//    private suspend fun safeGetAccounts() {
//        account.postValue(Resource.Loading())
//        try {
//            if (hasInternetConnection(context)) {
//                val response = accountRepository.getAccounts()
//                account.postValue(handleSearchNewsResponse(response))
//            } else
//                account.postValue(Resource.Error("No Internet Connection"))
//        } catch (ex: Exception) {
//            when (ex) {
//                is IOException -> account.postValue(Resource.Error("Network Failure"))
//                else -> account.postValue(Resource.Error("Conversion Error"))
//            }
//        }
//    }

//    private fun handleSearchNewsResponse(response: Response<Account>): Resource<Account> {
//        if (response.isSuccessful) {
//            response.body()?.let { resultResponse ->
//                return Resource.Success(resultResponse)
//            }
//        }
//        return Resource.Error(response.message())
//    }
}
