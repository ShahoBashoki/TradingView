package com.shaho.tradingview.ui.second

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shaho.tradingview.data.model.response.CandleResponse
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.CandleTimeType
import com.shaho.tradingview.util.extentions.getCurrentTime
import com.shaho.tradingview.util.extentions.takeOfHoursAgo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

lateinit var viewModel: SecondViewModel

private val hourAgo = 12
private val symbol = "XRP-USDT"
private val typeOfCandle = CandleTimeType.MIN_5
private var loopDelay = typeOfCandle.second * 1000
private val errorDelay: Long = 10 * 1000
private var minOpenCandleCurrent = 0.0
private var firstPossiblePointOfPurchase = 0.0
private var secondPossiblePointOfPurchase = 0.0
private var loopLocked = false
private var bought = false
private var purchasedPrice = 0.0
private var interestRates = 1.005
private var percentageOfLoss = 0.995
private lateinit var logList: SnapshotStateList<String>

@Composable
fun SecondFragmentComposable() {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    if (::viewModel.isInitialized.not()) {
        viewModel = hiltViewModel()

        logList = remember {
            mutableStateListOf()
        }

        getAllSymbols(context = context)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(state = listState) {

            itemsIndexed(logList) { _, item ->
                LogCard(description = item)
            }
        }

        AnimatedVisibility(visible = true) {
            LaunchedEffect(logList.size) {
                listState.animateScrollToItem(logList.size)
            }
        }
    }
}

@Composable
fun LogCard(description: String) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = 5.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}

private fun getAllSymbols(context: Context) {
    viewModel.getAllSymbols().observeForever {
        when (it) {
            is Resource.Failure -> {
                logList.add("${context.getCurrentTime()} (getAllSymbols) Failure: ${it.message}")
                logList.add("${context.getCurrentTime()} (getAllSymbols) retry after $errorDelay second")

                CoroutineScope(Dispatchers.Default).launch {
                    delay(errorDelay)

                    CoroutineScope(Dispatchers.Main).launch {
                        getAllSymbols(context = context)
                    }
                }
            }
            Resource.Loading -> logList.add("${context.getCurrentTime()} (getAllSymbols) Loading")
            is Resource.Success -> {
                logList.add("${context.getCurrentTime()} (getAllSymbols) Success")
                getAllAccounts(context = context)
            }
        }
    }
}

private fun getAllAccounts(context: Context) {
    viewModel.getAllAccounts().observeForever {
        when (it) {
            is Resource.Failure -> {
                logList.add("${context.getCurrentTime()} (getAllAccounts) Failure: ${it.message}")
                logList.add("${context.getCurrentTime()} (getAllAccounts) retry after $errorDelay second")

                CoroutineScope(Dispatchers.Default).launch {
                    delay(errorDelay)

                    CoroutineScope(Dispatchers.Main).launch {
                        getAllAccounts(context = context)
                    }
                }
            }
            Resource.Loading -> logList.add("${context.getCurrentTime()} (getAllAccounts) Loading")
            is Resource.Success -> {
                logList.add("${context.getCurrentTime()} (getAllAccounts) Success")
                startLoop(context = context)
            }
        }
    }
}

private fun getAllCandles(context: Context) {
    viewModel.getAllCandles(
        symbol = symbol,
        startAt = System.currentTimeMillis().takeOfHoursAgo(hourAgo) / 1000,
        endAt = System.currentTimeMillis() / 1000,
        type = typeOfCandle
    ).observeForever {
        when (it) {
            is Resource.Failure -> {
                logList.add("${context.getCurrentTime()} (getAllCandles) Failure: ${it.message}")
                logList.add("${context.getCurrentTime()} (getAllCandles) retry after $errorDelay second")

                CoroutineScope(Dispatchers.Default).launch {
                    delay(errorDelay)

                    CoroutineScope(Dispatchers.Main).launch {
                        getAllCandles(context = context)
                    }
                }
            }
            Resource.Loading -> logList.add("${context.getCurrentTime()} (getAllCandles) Loading")
            is Resource.Success -> {
                logList.add("${context.getCurrentTime()} (getAllCandles) Success")
                if (bought) {
                    CoroutineScope(Dispatchers.Main).launch {
                        getLastTwoCandle(context = context)
                    }
                } else
                    getMinOpenCandle(context = context)
            }
        }
    }
}

private fun getMinOpenCandle(context: Context) {
    viewModel.getMinOpenCandle(
        hourAgo = hourAgo
    ).observeForever {
        when (it) {
            is Resource.Failure -> {
                logList.add("${context.getCurrentTime()} (getMinOpenCandle) Failure: ${it.message}")
                logList.add("${context.getCurrentTime()} (getMinOpenCandle) retry after $errorDelay second")

                CoroutineScope(Dispatchers.Default).launch {
                    delay(errorDelay)

                    CoroutineScope(Dispatchers.Main).launch {
                        getMinOpenCandle(context = context)
                    }
                }
            }
            Resource.Loading -> logList.add("${context.getCurrentTime()} (getMinOpenCandle) Loading")
            is Resource.Success -> {
                logList.add("${context.getCurrentTime()} (getMinOpenCandle) Success: ${it.data}")
                minOpenCandleCurrent = it.data?.toDouble() ?: 0.0

                CoroutineScope(Dispatchers.Main).launch {
                    getLastTwoCandle(context = context)
                }
            }
        }
    }
}

private fun startLoop(context: Context) {
    logList.add("${context.getCurrentTime()} (startLoop)")
    CoroutineScope(Dispatchers.Default).launch {
        while (true) {
            CoroutineScope(Dispatchers.Main).launch {
                getAllCandles(context = context)
            }
            val millisecondTypeOfCandle = typeOfCandle.second * 1000
            loopDelay = millisecondTypeOfCandle - (System.currentTimeMillis() % millisecondTypeOfCandle) + 5000
            delay(loopDelay)
        }
    }
}

private suspend fun getLastTwoCandle(context: Context) {
    if (!loopLocked) {
        loopLocked = true
        viewModel.getLastTwoCandle().observeForever {
            when (it) {
                is Resource.Failure -> {
                    logList.add("${context.getCurrentTime()} (getLastCandle) Failure: ${it.message}")
                    logList.add("${context.getCurrentTime()} (getLastCandle) retry after $errorDelay second")

                    CoroutineScope(Dispatchers.Default).launch {
                        delay(errorDelay)
                        CoroutineScope(Dispatchers.Main).launch {
                            getLastTwoCandle(context = context)
                        }
                    }
                }
                Resource.Loading -> logList.add("${context.getCurrentTime()} (getLastCandle) Loading")
                is Resource.Success -> {
                    if ((it.data?.size ?: 0) > 1) {

                        val lastCandle = it.data?.get(1)
                        logList.add("${context.getCurrentTime()} (getLastCandle) Success: $lastCandle")
                        if (bought) {
                            checkPointOfSale(lastCandle = lastCandle, context = context)
                        } else {
                            when {
                                firstPossiblePointOfPurchase == 0.0 -> {
                                    checkFirstPossiblePointOfPurchase(lastCandle = lastCandle, context = context)
                                    loopLocked = false
                                }
                                secondPossiblePointOfPurchase == 0.0 -> {
                                    checkSecondPossiblePointOfPurchase(lastCandle = lastCandle, context = context)
                                    loopLocked = false
                                }
                                else -> {
                                    finalCheck(lastCandle = lastCandle, context = context)
                                }
                            }
                        }
                        logList.add("${context.getCurrentTime()} wait for $loopDelay millisecond")
                    } else {
                        loopLocked = false
                    }
                }
            }
        }
    }
}

private fun checkFirstPossiblePointOfPurchase(lastCandle: CandleResponse?, context: Context) {
    if (minOpenCandleCurrent < (lastCandle?.low?.toDouble() ?: Double.MAX_VALUE)) {
        logList.add("${context.getCurrentTime()} (checkFirstPossiblePointOfPurchase) Has not reached the minimum value")
        resetPossiblePoints()
    } else {
        logList.add("${context.getCurrentTime()} (checkFirstPossiblePointOfPurchase) Has reached the minimum value. Please wait for next candle")
        firstPossiblePointOfPurchase = lastCandle?.low?.toDouble() ?: 0.0
    }
}

private fun checkSecondPossiblePointOfPurchase(lastCandle: CandleResponse?, context: Context) {
    if ((lastCandle?.open?.toDouble() ?: Double.MAX_VALUE) < (lastCandle?.close?.toDouble() ?: 0.0)) {
        logList.add("${context.getCurrentTime()} (checkSecondPossiblePointOfPurchase) Has reached the second possible point value. Please wait for next candle")
        secondPossiblePointOfPurchase = lastCandle?.low?.toDouble() ?: 0.0
    } else {
        logList.add("${context.getCurrentTime()} (checkSecondPossiblePointOfPurchase) Has not reached the minimum value")
        checkFirstPossiblePointOfPurchase(lastCandle, context = context)
    }
}

private fun finalCheck(lastCandle: CandleResponse?, context: Context) {
    if ((lastCandle?.open?.toDouble() ?: Double.MAX_VALUE) > (lastCandle?.close?.toDouble() ?: 0.0)) {
        logList.add("${context.getCurrentTime()} (finalCheck) reset state")
        resetPossiblePoints()
        loopLocked = false
    } else {
        logList.add("${context.getCurrentTime()} (finalCheck) ************** buy ************** price: ${lastCandle?.open}")
        purchasedPrice = lastCandle?.open?.toDouble() ?: 0.0
        createBuyMarketOrder(context = context)
        resetPossiblePoints()
    }
}

private fun resetPossiblePoints() {
    firstPossiblePointOfPurchase = 0.0
    secondPossiblePointOfPurchase = 0.0
}

private fun createBuyMarketOrder(context: Context) {
    viewModel.createBuyMarketOrder(
        symbol = symbol
    ).observeForever {
        when (it) {
            is Resource.Failure -> {
                logList.add("${context.getCurrentTime()} (createBuyMarketOrder) Failure: ${it.message}")
                logList.add("${context.getCurrentTime()} (createBuyMarketOrder) retry after $errorDelay second")

                CoroutineScope(Dispatchers.Default).launch {
                    delay(errorDelay)

                    CoroutineScope(Dispatchers.Main).launch {
                        createBuyMarketOrder(context = context)
                    }
                }
            }
            Resource.Loading -> logList.add("${context.getCurrentTime()} (createBuyMarketOrder) Loading")
            is Resource.Success -> {
                logList.add("${context.getCurrentTime()} (createBuyMarketOrder) Success")
                bought = true
                loopLocked = false
            }
        }
    }
}

private fun createSellMarketOrder(damage: Boolean, context: Context) {
    viewModel.createSellMarketOrder(
        symbol = symbol
    ).observeForever {
        when (it) {
            is Resource.Failure -> {
                logList.add("${context.getCurrentTime()} (createSellMarketOrder) Failure: ${it.message}")
                logList.add("${context.getCurrentTime()} (createSellMarketOrder) retry after $errorDelay second")

                CoroutineScope(Dispatchers.Default).launch {
                    delay(errorDelay)

                    CoroutineScope(Dispatchers.Main).launch {
                        createSellMarketOrder(damage = damage, context = context)
                    }
                }
            }
            Resource.Loading -> logList.add("${context.getCurrentTime()} (createSellMarketOrder) Loading")
            is Resource.Success -> {
                logList.add("${context.getCurrentTime()} (createSellMarketOrder) Success")
                if (damage) {
                    logList.add("${context.getCurrentTime()} (createSellMarketOrder) ************** Unfortunately, you lost 2% **************")
                } else {
                    logList.add("${context.getCurrentTime()} (createSellMarketOrder) ************** Congratulations, you earned 2% **************")
                }
                bought = false
                loopLocked = false
            }
        }
    }
}

private fun checkPointOfSale(lastCandle: CandleResponse?, context: Context) {
    lastCandle?.open?.toDouble()?.let { itOpen ->
        when {
            itOpen > (purchasedPrice * interestRates) -> {
                createSellMarketOrder(damage = false, context = context)
            }
            itOpen < (purchasedPrice * percentageOfLoss) -> {
                createSellMarketOrder(damage = true, context = context)
            }
            else -> {
                loopLocked = false
                logList.add("${context.getCurrentTime()} (checkPointOfSale) wait next candle for sale")
            }
        }
    } ?: run {
        loopLocked = false
    }
}

@Composable
@Preview
fun ProfileScreenPreview() {
    SecondFragmentComposable()
}
