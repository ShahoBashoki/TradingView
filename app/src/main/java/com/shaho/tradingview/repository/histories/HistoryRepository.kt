package com.shaho.tradingview.repository.histories

import com.shaho.tradingview.data.local.dao.HistoryDao
import com.shaho.tradingview.data.model.response.CandleResponse
import com.shaho.tradingview.data.model.response.TradeHistoriesResponse
import com.shaho.tradingview.data.remote.HistoryApi
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.CandleTimeType
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val historyApi: HistoryApi,
    private val historyDao: HistoryDao
) : IHistoryRepository {

    override suspend fun getTradeHistories(
        symbol: String
    ): Flow<Resource<List<TradeHistoriesResponse>>> = callbackFlow {
        val response = historyApi.getTradeHistories(
            symbol = symbol
        )
        response.msg?.let { itMessage ->
            trySend(
                Resource.Failure(
                    message = "code: ${response.code}, message: $itMessage"
                )
            )
        } ?: kotlin.run {
            trySend(
                Resource.Success(
                    data = response.data
                )
            )
        }
        awaitClose { close() }
    }

    override suspend fun getAllCandles(
        forceRemote: Boolean,
        symbol: String,
        startAt: Long,
        endAt: Long,
        type: CandleTimeType
    ): Flow<Resource<List<CandleResponse>>> = callbackFlow {
        if (forceRemote) {
            deleteAllCandles()
            getAllCandlesFromRemote(
                symbol = symbol,
                startAt = startAt,
                endAt = endAt,
                type = type
            ).catch {
                trySend(Resource.Failure(message = ""))
            }.collect {
                when (it) {
                    is Resource.Failure -> trySend(Resource.Failure(message = ""))
                    Resource.Loading -> trySend(Resource.Loading)
                    is Resource.Success -> {
                        it.data?.let { listCandle ->
                            listCandle.forEach { itCandle ->
                                itCandle.symbol = symbol
                                itCandle.candle = type.value
                            }
                            insertAllCandles(listCandle)
                            trySend(
                                Resource.Success(
                                    getAllCandlesFromLocal(
                                        symbol = symbol,
                                        startAt = startAt,
                                        endAt = endAt,
                                        type = type
                                    )
                                )
                            )
                        } ?: run {
                            trySend(Resource.Failure(message = "data is null"))
                        }
                    }
                }
            }
        } else {
            trySend(
                Resource.Success(
                    getAllCandlesFromLocal(
                        symbol = symbol,
                        startAt = startAt,
                        endAt = endAt,
                        type = type
                    )
                )
            )
        }
        awaitClose { cancel() }
    }

    override suspend fun getMinCandle(
        startAt: Long?,
        endAt: Long?
    ): Flow<Resource<String>> = callbackFlow {
        trySend(
            Resource.Success(
                data = historyDao.getMinCandle(
                    startAt = startAt,
                    endAt = endAt
                )
            )
        )
        awaitClose { close() }
    }

    override suspend fun getLastTwoCandle(): Flow<Resource<List<CandleResponse>>> = callbackFlow {
        trySend(
            Resource.Success(
                data = historyDao.getLastTwoCandle()
            )
        )
        awaitClose { close() }
    }

    override suspend fun getAllCandlesFromRemote(
        symbol: String,
        startAt: Long,
        endAt: Long,
        type: CandleTimeType
    ): Flow<Resource<List<CandleResponse>>> = callbackFlow {
        val repeat = ((endAt - startAt) / (type.second * 1500)) + 1
        var newStartAt = startAt
        var newEndAt = if (newStartAt + (type.second * 1500) < endAt)
            newStartAt + (type.second * 1500)
        else
            endAt
        for (i in 1..repeat) {
            val response = historyApi.getCandles(
                symbol = symbol,
                startAt = newStartAt,
                endAt = newEndAt,
                type = type.value
            )
            response.msg?.let { itMessage ->
                trySend(
                    Resource.Failure(
                        message = "code: ${response.code}, message: $itMessage"
                    )
                )
            } ?: kotlin.run {
                trySend(
                    Resource.Success(
                        data = response.data?.map {
                            CandleResponse(
                                time = it[0],
                                open = it[1],
                                close = it[2],
                                high = it[3],
                                low = it[4],
                                volume = it[5],
                                turnover = it[6],
                            )
                        }
                    )
                )

                newStartAt = newEndAt
                newEndAt = if (newStartAt + (type.second * 1500) < endAt)
                    newStartAt + (type.second * 1500)
                else
                    endAt
            }
        }
        awaitClose { close() }
    }

    override suspend fun getAllCandlesFromLocal(
        symbol: String,
        startAt: Long,
        endAt: Long,
        type: CandleTimeType
    ): List<CandleResponse> = historyDao.getCandles(
        symbol = symbol,
        startAt = startAt,
        endAt = endAt,
        type = type.value
    )

    override suspend fun insertAllCandles(
        candles: List<CandleResponse>
    ) = historyDao.insertAllCandles(candles)

    override suspend fun insertCandle(
        candle: CandleResponse
    ) = historyDao.insertCandle(candle)

    override suspend fun deleteCandle(
        candle: CandleResponse
    ) = historyDao.deleteCandle(candle)

    override suspend fun deleteAllCandles() = historyDao.deleteAllCandles()
}
