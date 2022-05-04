package com.shaho.tradingview.ui.second

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shaho.tradingview.R
import com.shaho.tradingview.data.model.response.CandleResponse
import com.shaho.tradingview.databinding.FragmentSecondBinding
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.CandleTimeType
import com.shaho.tradingview.util.extentions.takeOfHoursAgo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SecondFragment : Fragment(R.layout.fragment_second) {
    private val hourAgo = 12
    private val symbol = "XRP-USDT"
    private val currency = "XRP"
    private val typeOfCandle = CandleTimeType.MIN_5
    private var loopDelay = typeOfCandle.second * 1000
    private val errorDelay: Long = 10 * 1000
    private var minOpenCandleCurrent = 0.0
    private var firstPossiblePointOfPurchase = 0.0
    private var secondPossiblePointOfPurchase = 0.0
    private var loopLocked = false
    private var bought = false
    private var purchasedPrice = 0.0
    private var interestRates = 1.02
    private var percentageOfLoss = 0.98

    private val viewModel: SecondViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentSecondBinding.bind(view)
        binding.apply {
        }

        getAllSymbols()
    }

    private fun getAllSymbols() {
        viewModel.getAllSymbols().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Log.i("shahoLog", "(getAllSymbols) Failure: ${it.message}")
                    Log.i("shahoLog", "(getAllSymbols) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            getAllSymbols()
                        }
                    }
                }
                Resource.Loading -> Log.i("shahoLog", "(getAllSymbols) Loading: ")
                is Resource.Success -> {
                    Log.i("shahoLog", "(getAllSymbols) Success")
                    getAllAccounts()
                }
            }
        }
    }

    private fun getAllAccounts() {
        viewModel.getAllAccounts().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Log.i("shahoLog", "(getAllAccounts) Failure: ${it.message}")
                    Log.i("shahoLog", "(getAllAccounts) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            getAllAccounts()
                        }
                    }
                }
                Resource.Loading -> Log.i("shahoLog", "(getAllAccounts) Loading: ")
                is Resource.Success -> {
                    Log.i("shahoLog", "(getAllAccounts) Success")
                    startLoop()
                }
            }
        }
    }

    private fun getAllCandles() {
        viewModel.getAllCandles(
            symbol = symbol,
            startAt = System.currentTimeMillis().takeOfHoursAgo(hourAgo) / 1000,
            endAt = System.currentTimeMillis() / 1000,
            type = typeOfCandle
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Log.i("shahoLog", "(getAllCandles) Failure: ${it.message}")
                    Log.i("shahoLog", "(getAllCandles) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            getAllCandles()
                        }
                    }
                }
                Resource.Loading -> Log.i("shahoLog", "(getAllCandles) Loading: ")
                is Resource.Success -> {
                    Log.i("shahoLog", "(getAllCandles) Success: ${it.data}")
                    if (bought) {
                        CoroutineScope(Dispatchers.Main).launch {
                            getLastTwoCandle()
                        }
                    } else
                        getMinOpenCandle()
                }
            }
        }
    }

    private fun getMinOpenCandle() {
        viewModel.getMinOpenCandle(
            hourAgo = hourAgo
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Log.i("shahoLog", "(getMinOpenCandle) Failure: ${it.message}")
                    Log.i("shahoLog", "(getMinOpenCandle) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            getMinOpenCandle()
                        }
                    }
                }
                Resource.Loading -> Log.i("shahoLog", "(getMinOpenCandle) Loading: ")
                is Resource.Success -> {
                    Log.i("shahoLog", "(getMinOpenCandle) Success: ${it.data}")
                    minOpenCandleCurrent = it.data?.toDouble() ?: 0.0

                    CoroutineScope(Dispatchers.Main).launch {
                        getLastTwoCandle()
                    }
                }
            }
        }
    }

    private fun startLoop() {
        Log.i("shahoLog", "(startLoop)")
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                CoroutineScope(Dispatchers.Main).launch {
                    getAllCandles()
                }
                val millisecondTypeOfCandle = typeOfCandle.second * 1000
                loopDelay = millisecondTypeOfCandle - (System.currentTimeMillis() % millisecondTypeOfCandle) + 5000
                delay(loopDelay)
            }
        }
    }

    private suspend fun getLastTwoCandle() {
        if (!loopLocked) {
            loopLocked = true
            viewModel.getLastTwoCandle().observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Failure -> {
                        Log.i("shahoLog", "(getLastCandle) Failure: ${it.message}")
                        Log.i("shahoLog", "(getLastCandle) retry after $errorDelay second")

                        CoroutineScope(Dispatchers.Default).launch {
                            delay(errorDelay)
                            CoroutineScope(Dispatchers.Main).launch {
                                getLastTwoCandle()
                            }
                        }
                    }
                    Resource.Loading -> Log.i("shahoLog", "(getLastCandle) Loading: ")
                    is Resource.Success -> {
                        if ((it.data?.size ?: 0) > 1) {
//                            loopDelay = ((((it.data?.get(0)?.time?.toLong() ?: 0) + typeOfCandle.second) - (System.currentTimeMillis() / 1000)) + 30) * 1000

                            val lastCandle = it.data?.get(1)
                            Log.i("shahoLog", "(getLastCandle) Success: $lastCandle")
                            if (bought) {
                                checkPointOfSale(lastCandle)
                            } else {
                                when {
                                    firstPossiblePointOfPurchase == 0.0 -> {
                                        checkFirstPossiblePointOfPurchase(lastCandle)
                                        loopLocked = false
                                    }
                                    secondPossiblePointOfPurchase == 0.0 -> {
                                        checkSecondPossiblePointOfPurchase(lastCandle)
                                        loopLocked = false
                                    }
                                    else -> {
                                        finalCheck(lastCandle)
                                    }
                                }
                            }
                            Log.i("shahoLog", " wait for $loopDelay millisecond")
                        }
                    }
                }
            }
        }
    }

    private fun checkFirstPossiblePointOfPurchase(lastCandle: CandleResponse?) {
        if (minOpenCandleCurrent < (lastCandle?.low?.toDouble() ?: Double.MAX_VALUE)) {
            Log.i("shahoLog", "(checkFirstPossiblePointOfPurchase) Has not reached the minimum value")
            resetPossiblePoints()
        } else {
            Log.i("shahoLog", "(checkFirstPossiblePointOfPurchase) Has reached the minimum value. Please wait for next candle")
            firstPossiblePointOfPurchase = lastCandle?.low?.toDouble() ?: 0.0
            checkSecondPossiblePointOfPurchase(lastCandle)
        }
    }

    private fun checkSecondPossiblePointOfPurchase(lastCandle: CandleResponse?) {
        if ((lastCandle?.open?.toDouble() ?: Double.MAX_VALUE) < (lastCandle?.close?.toDouble() ?: 0.0)) {
            Log.i("shahoLog", "(checkSecondPossiblePointOfPurchase) Has reached the second possible point value. Please wait for next candle")
            secondPossiblePointOfPurchase = lastCandle?.low?.toDouble() ?: 0.0
        } else {
            Log.i("shahoLog", "(checkSecondPossiblePointOfPurchase) Has not reached the minimum value")
            checkFirstPossiblePointOfPurchase(lastCandle)
        }
    }

    private fun finalCheck(lastCandle: CandleResponse?) {
        if ((lastCandle?.open?.toDouble() ?: Double.MAX_VALUE) > (lastCandle?.close?.toDouble() ?: 0.0)) {
            Log.i("shahoLog", "(finalCheck) reset state")
            resetPossiblePoints()
            loopLocked = false
        } else {
            Log.i("shahoLog", "(finalCheck) ************** buy ************** price: ${lastCandle?.open}")
            purchasedPrice = lastCandle?.open?.toDouble() ?: 0.0
            createBuyMarketOrder()
            resetPossiblePoints()
        }
    }

    private fun resetPossiblePoints() {
        firstPossiblePointOfPurchase = 0.0
        secondPossiblePointOfPurchase = 0.0
    }

    private fun createBuyMarketOrder() {
        viewModel.createBuyMarketOrder(
            symbol = symbol
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Log.i("shahoLog", "(createBuyMarketOrder) Failure: ${it.message}")
                    Log.i("shahoLog", "(createBuyMarketOrder) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.createBuyMarketOrder(
                                symbol = symbol
                            )
                        }
                    }
                }
                Resource.Loading -> Log.i("shahoLog", "(createBuyMarketOrder) Loading: ")
                is Resource.Success -> {
                    Log.i("shahoLog", "(createBuyMarketOrder) Success")
                    bought = true
                    loopLocked = false
                }
            }
        }
    }

    private fun createSellMarketOrder(damage: Boolean) {
        viewModel.createSellMarketOrder(
            symbol = symbol
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Log.i("shahoLog", "(createSellMarketOrder) Failure: ${it.message}")
                    Log.i("shahoLog", "(createSellMarketOrder) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.createSellMarketOrder(
                                symbol = symbol
                            )
                        }
                    }
                }
                Resource.Loading -> Log.i("shahoLog", "(createSellMarketOrder) Loading: ")
                is Resource.Success -> {
                    Log.i("shahoLog", "(createSellMarketOrder) Success")
                    if (damage) {
                        Log.i("shahoLog", "(sellLog) ************** Unfortunately, you lost 2% **************")
                        Log.i("shahoLog", "(createSellMarketOrder) ************** Unfortunately, you lost 2% **************")
                    } else {
                        Log.i("shahoLog", "(sellLog) ************** Congratulations, you earned 2% **************")
                        Log.i("shahoLog", "(createSellMarketOrder) ************** Congratulations, you earned 2% **************")
                    }
                    bought = false
                    loopLocked = false
                }
            }
        }
    }

    private fun createSellLimitOrder(price: String) {
        viewModel.createSellLimitOrder(
            symbol = symbol,
            price = price,
            currency = currency
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Log.i("shahoLog", "(createSellLimitOrder) Failure: ${it.message}")
                    Log.i("shahoLog", "(createSellLimitOrder) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.createSellLimitOrder(
                                symbol = symbol,
                                price = "0.1",
                                currency = currency
                            )
                        }
                    }
                }
                Resource.Loading -> Log.i("shahoLog", "(createSellLimitOrder) Loading: ")
                is Resource.Success -> {
                    Log.i("shahoLog", "(createSellLimitOrder) Success")
                }
            }
        }
    }

    private fun cancelAllOrders() {
        viewModel.cancelAllOrders().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Failure -> {
                    Log.i("shahoLog", "(cancelAllOrders) Failure: ${it.message}")
                    Log.i("shahoLog", "(cancelAllOrders) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.cancelAllOrders()
                        }
                    }
                }
                Resource.Loading -> Log.i("shahoLog", "(cancelAllOrders) Loading: ")
                is Resource.Success -> {
                    Log.i("shahoLog", "(cancelAllOrders) Success")
                }
            }
        }
    }

    private fun checkPointOfSale(lastCandle: CandleResponse?) {
        lastCandle?.open?.toDouble()?.let { itOpen ->
            when {
                itOpen > (purchasedPrice * interestRates) -> {
                    createSellMarketOrder(false)
                }
                itOpen < (purchasedPrice * percentageOfLoss) -> {
                    createSellMarketOrder(true)
                }
                else -> {
                    loopLocked = false
                    Log.i("shahoLog", "(checkPointOfSale) wait next candle for sale")
                }
            }
        } ?: kotlin.run {
            loopLocked = false
        }
    }
}
