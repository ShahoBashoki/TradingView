package com.shaho.tradingview.ui.second

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.shaho.tradingview.R
import com.shaho.tradingview.data.model.response.CandleResponse
import com.shaho.tradingview.databinding.FragmentSecondBinding
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.CandleTimeType
import com.shaho.tradingview.util.extentions.getCurrentTime
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
    private var showLog = ""
    private var showHeaderLog = ""

    private val viewModel: SecondViewModel by viewModels()
    private lateinit var binding: FragmentSecondBinding

    private var startFragment = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (::binding.isInitialized.not())
            binding = FragmentSecondBinding.inflate(inflater)
        else
            startFragment = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (startFragment)
            getAllSymbols()
    }

    private fun getAllSymbols() {
        viewModel.getAllSymbols().observeForever {
            when (it) {
                is Resource.Failure -> {
                    showLog += "${getCurrentTime()} (getAllSymbols) Failure: ${it.message} \n\n"
                    showLog += "${getCurrentTime()} (getAllSymbols) retry after $errorDelay second \n\n"

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            getAllSymbols()
                        }
                    }
                }
                Resource.Loading -> showLog += "${getCurrentTime()} (getAllSymbols) Loading \n\n"
                is Resource.Success -> {
                    showLog += "${getCurrentTime()} (getAllSymbols) Success \n\n"
                    getAllAccounts()
                }
            }
        }
    }

    private fun getAllAccounts() {
        viewModel.getAllAccounts().observeForever {
            when (it) {
                is Resource.Failure -> {
                    showLog += "${getCurrentTime()} (getAllAccounts) Failure: ${it.message} \n\n"
                    showLog += "${getCurrentTime()} (getAllAccounts) retry after $errorDelay second \n\n"

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            getAllAccounts()
                        }
                    }
                }
                Resource.Loading -> showLog += "${getCurrentTime()} (getAllAccounts) Loading \n\n"
                is Resource.Success -> {
                    showLog += "${getCurrentTime()} (getAllAccounts) Success \n\n"
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
        ).observeForever {
            when (it) {
                is Resource.Failure -> {
                    showLog += "${getCurrentTime()} (getAllCandles) Failure: ${it.message} \n\n"
                    showLog += "${getCurrentTime()} (getAllCandles) retry after $errorDelay second \n\n"

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            getAllCandles()
                        }
                    }
                }
                Resource.Loading -> showLog += "${getCurrentTime()} (getAllCandles) Loading \n\n"
                is Resource.Success -> {
                    showLog += "${getCurrentTime()} (getAllCandles) Success \n\n"
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
        ).observeForever {
            when (it) {
                is Resource.Failure -> {
                    showLog += "${getCurrentTime()} (getMinOpenCandle) Failure: ${it.message} \n\n"
                    showLog += "${getCurrentTime()} (getMinOpenCandle) retry after $errorDelay second \n\n"

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            getMinOpenCandle()
                        }
                    }
                }
                Resource.Loading -> showLog += "${getCurrentTime()} (getMinOpenCandle) Loading \n\n"
                is Resource.Success -> {
                    showLog += "${getCurrentTime()} (getMinOpenCandle) Success: ${it.data} \n\n"
                    minOpenCandleCurrent = it.data?.toDouble() ?: 0.0

                    CoroutineScope(Dispatchers.Main).launch {
                        getLastTwoCandle()
                    }
                }
            }
        }
    }

    private fun startLoop() {
        showLog += "${getCurrentTime()} (startLoop) \n\n"
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
            viewModel.getLastTwoCandle().observeForever {
                when (it) {
                    is Resource.Failure -> {
                        showLog += "${getCurrentTime()} (getLastCandle) Failure: ${it.message} \n\n"
                        showLog += "${getCurrentTime()} (getLastCandle) retry after $errorDelay second \n\n"

                        CoroutineScope(Dispatchers.Default).launch {
                            delay(errorDelay)
                            CoroutineScope(Dispatchers.Main).launch {
                                getLastTwoCandle()
                            }
                        }
                    }
                    Resource.Loading -> showLog += "${getCurrentTime()} (getLastCandle) Loading \n\n"
                    is Resource.Success -> {
                        if ((it.data?.size ?: 0) > 1) {

                            val lastCandle = it.data?.get(1)
                            showLog += "${getCurrentTime()} (getLastCandle) Success: $lastCandle \n\n"
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
                            showLog += "${getCurrentTime()} wait for $loopDelay millisecond \n\n"

                            CoroutineScope(Dispatchers.Main).launch {
                                binding.showLogTextView.text = showLog
                                binding.headerLogTextView.text = showHeaderLog
                                showLog = ""
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkFirstPossiblePointOfPurchase(lastCandle: CandleResponse?) {
        if (minOpenCandleCurrent < (lastCandle?.low?.toDouble() ?: Double.MAX_VALUE)) {
            showLog += "${getCurrentTime()} (checkFirstPossiblePointOfPurchase) Has not reached the minimum value \n\n"
            resetPossiblePoints()
        } else {
            showLog += "${getCurrentTime()} (checkFirstPossiblePointOfPurchase) Has reached the minimum value. Please wait for next candle \n\n"
            firstPossiblePointOfPurchase = lastCandle?.low?.toDouble() ?: 0.0
            checkSecondPossiblePointOfPurchase(lastCandle)
        }
    }

    private fun checkSecondPossiblePointOfPurchase(lastCandle: CandleResponse?) {
        if ((lastCandle?.open?.toDouble() ?: Double.MAX_VALUE) < (lastCandle?.close?.toDouble() ?: 0.0)) {
            showLog += "${getCurrentTime()} (checkSecondPossiblePointOfPurchase) Has reached the second possible point value. Please wait for next candle \n\n"
            secondPossiblePointOfPurchase = lastCandle?.low?.toDouble() ?: 0.0
        } else {
            showLog += "${getCurrentTime()} (checkSecondPossiblePointOfPurchase) Has not reached the minimum value \n\n"
            checkFirstPossiblePointOfPurchase(lastCandle)
        }
    }

    private fun finalCheck(lastCandle: CandleResponse?) {
        if ((lastCandle?.open?.toDouble() ?: Double.MAX_VALUE) > (lastCandle?.close?.toDouble() ?: 0.0)) {
            showLog += "${getCurrentTime()} (finalCheck) reset state \n\n"
            resetPossiblePoints()
            loopLocked = false
        } else {
            showLog += "${getCurrentTime()} (finalCheck) ************** buy ************** price: ${lastCandle?.open} \n\n"
            showHeaderLog += "${getCurrentTime()} ${getString(R.string.bought, lastCandle?.open)} \n\n"
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
        ).observeForever {
            when (it) {
                is Resource.Failure -> {
                    showLog += "${getCurrentTime()} (createBuyMarketOrder) Failure: ${it.message} \n\n"
                    showLog += "${getCurrentTime()} (createBuyMarketOrder) retry after $errorDelay second \n\n"

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.createBuyMarketOrder(
                                symbol = symbol
                            )
                        }
                    }
                }
                Resource.Loading -> showLog += "${getCurrentTime()} (createBuyMarketOrder) Loading \n\n"
                is Resource.Success -> {
                    showLog += "${getCurrentTime()} (createBuyMarketOrder) Success \n\n"
                    bought = true
                    loopLocked = false
                }
            }
        }
    }

    private fun createSellMarketOrder(damage: Boolean) {
        viewModel.createSellMarketOrder(
            symbol = symbol
        ).observeForever {
            when (it) {
                is Resource.Failure -> {
                    showLog += "${getCurrentTime()} (createSellMarketOrder) Failure: ${it.message} \n\n"
                    showLog += "${getCurrentTime()} (createSellMarketOrder) retry after $errorDelay second \n\n"

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.createSellMarketOrder(
                                symbol = symbol
                            )
                        }
                    }
                }
                Resource.Loading -> showLog += "${getCurrentTime()} (createSellMarketOrder) Loading \n\n"
                is Resource.Success -> {
                    showLog += "${getCurrentTime()} (createSellMarketOrder) Success \n\n"
                    if (damage) {
                        showLog += "${getCurrentTime()} (createSellMarketOrder) ************** Unfortunately, you lost 2% ************** \n\n"
                        showHeaderLog += "${getCurrentTime()} ${getString(R.string.sell_at_a_loss)} \n\n"
                    } else {
                        showLog += "${getCurrentTime()} (createSellMarketOrder) ************** Congratulations, you earned 2% ************** \n\n"
                        showHeaderLog += "${getCurrentTime()} ${getString(R.string.sell_with_profit)} \n\n"
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
        ).observeForever {
            when (it) {
                is Resource.Failure -> {
                    showLog += "${getCurrentTime()} (createSellLimitOrder) Failure: ${it.message} \n\n"
                    showLog += "${getCurrentTime()} (createSellLimitOrder) retry after $errorDelay second \n\n"

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
                Resource.Loading -> showLog += "${getCurrentTime()} (createSellLimitOrder) Loading \n\n"
                is Resource.Success -> {
                    showLog += "${getCurrentTime()} (createSellLimitOrder) Success \n\n"
                }
            }
        }
    }

    private fun cancelAllOrders() {
        viewModel.cancelAllOrders().observeForever {
            when (it) {
                is Resource.Failure -> {
                    showLog += "${getCurrentTime()} (cancelAllOrders) Failure: ${it.message} \n\n"
                    showLog += "${getCurrentTime()} (cancelAllOrders) retry after $errorDelay second \n\n"

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)

                        CoroutineScope(Dispatchers.Main).launch {
                            viewModel.cancelAllOrders()
                        }
                    }
                }
                Resource.Loading -> showLog += "${getCurrentTime()} (cancelAllOrders) Loading \n\n"
                is Resource.Success -> {
                    showLog += "${getCurrentTime()} (cancelAllOrders) Success \n\n"
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
                    showLog += "${getCurrentTime()} (checkPointOfSale) wait next candle for sale \n\n"
                }
            }
        } ?: kotlin.run {
            loopLocked = false
        }
    }
}
