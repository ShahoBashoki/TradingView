package com.shaho.tradingview.repository.account

import com.shaho.tradingview.data.local.dao.AccountDao
import com.shaho.tradingview.data.model.request.CreateAccountRequest
import com.shaho.tradingview.data.model.request.InnerTransferRequest
import com.shaho.tradingview.data.model.response.AccountLedgerResponse
import com.shaho.tradingview.data.model.response.AccountResponse
import com.shaho.tradingview.data.model.response.CreateAccountResponse
import com.shaho.tradingview.data.model.response.InnerTransferResponse
import com.shaho.tradingview.data.model.response.RemotePaginationResponse
import com.shaho.tradingview.data.remote.AccountApi
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.AccountType
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountApi: AccountApi,
    private val accountDao: AccountDao
) : IAccountRepository {

    override suspend fun createAccount(
        createAccountRequest: CreateAccountRequest
    ): Flow<Resource<CreateAccountResponse>> = callbackFlow {
        val response = accountApi.createAccount(
            request = createAccountRequest
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
            getAllAccounts(forceRemote = true).collect {}
        }
        awaitClose { close() }
    }

    override suspend fun getAllAccounts(
        forceRemote: Boolean,
        accountType: AccountType?,
        accountCurrency: String?
    ): Flow<Resource<List<AccountResponse>>> = callbackFlow {
        if (forceRemote) {
            getAllAccountsFromRemote(
                accountType = accountType,
                accountCurrency = accountCurrency
            ).catch {
                trySend(Resource.Failure(message = ""))
            }.collect {
                when (it) {
                    is Resource.Failure -> trySend(Resource.Failure(message = ""))
                    Resource.Loading -> trySend(Resource.Loading)
                    is Resource.Success -> {
                        it.data?.let { listAccount ->
                            insertAllAccounts(listAccount)
                            trySend(Resource.Success(getAllAccountsFromLocal(accountType = accountType, accountCurrency = accountCurrency)))
                        } ?: run {
                            trySend(Resource.Failure(message = ""))
                        }
                    }
                }
            }
        } else {
            trySend(Resource.Success(getAllAccountsFromLocal(accountType = accountType, accountCurrency = accountCurrency)))
        }
        awaitClose { cancel() }
    }

    override suspend fun getAllAccountsFromRemote(
        accountType: AccountType?,
        accountCurrency: String?
    ): Flow<Resource<List<AccountResponse>>> = callbackFlow {
        val response = accountApi.getAccounts(
            type = accountType?.value,
            currency = accountCurrency
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

    override suspend fun getAllAccountsFromLocal(
        accountType: AccountType?,
        accountCurrency: String?
    ): List<AccountResponse> = accountDao.getAccounts(
        type = accountType?.value,
        currency = accountCurrency
    )

    override suspend fun getAccount(
        accountId: String
    ): Flow<Resource<AccountResponse>> =
        callbackFlow {
            val response = accountApi.getAccount(
                accountId = accountId
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

    override suspend fun insertAllAccounts(accounts: List<AccountResponse>) = accountDao.insertAllAccounts(accounts)

    override suspend fun insertAccount(account: AccountResponse) = accountDao.insertAccount(account)

    override suspend fun deleteAccount(account: AccountResponse) = accountDao.deleteAccount(account)

    override suspend fun deleteAllAccounts() = accountDao.deleteAllAccounts()

    override suspend fun getAllAccountLedgers(
        forceRemote: Boolean,
        currency: String?,
        direction: String?,
        bizType: String?,
        startAt: Long?,
        endAt: Long?,
        currentPage: Int,
        pageSize: Int
    ): Flow<Resource<List<AccountLedgerResponse>>> = callbackFlow {
        if (forceRemote) {
            getAllAccountLedgersFromRemote(
                currency = currency,
                direction = direction,
                bizType = bizType,
                startAt = startAt,
                endAt = endAt,
                currentPage = currentPage,
                pageSize = pageSize
            ).catch {
                trySend(Resource.Failure(message = ""))
            }.collect {
                when (it) {
                    is Resource.Failure -> trySend(Resource.Failure(message = ""))
                    Resource.Loading -> trySend(Resource.Loading)
                    is Resource.Success -> {
                        it.data?.let { listAccountLedgers ->
                            listAccountLedgers.items?.let { it1 -> insertAllAccountLedgers(it1) }
                            trySend(
                                Resource.Success(
                                    getAllAccountLedgersFromLocal(
                                        currency = currency,
                                        direction = direction,
                                        bizType = bizType,
                                        startAt = startAt,
                                        endAt = endAt,
                                        currentPage = currentPage,
                                        pageSize = pageSize
                                    )
                                )
                            )
                        } ?: run {
                            trySend(Resource.Failure(message = ""))
                        }
                    }
                }
            }
        } else {
            trySend(
                Resource.Success(
                    getAllAccountLedgersFromLocal(
                        currency = currency,
                        direction = direction,
                        bizType = bizType,
                        startAt = startAt,
                        endAt = endAt,
                        currentPage = currentPage,
                        pageSize = pageSize
                    )
                )
            )
        }
        awaitClose { cancel() }
    }

    override suspend fun getAllAccountLedgersFromRemote(
        currency: String?,
        direction: String?,
        bizType: String?,
        startAt: Long?,
        endAt: Long?,
        currentPage: Int,
        pageSize: Int
    ): Flow<Resource<RemotePaginationResponse<AccountLedgerResponse>>> = callbackFlow {
        val response = accountApi.getAccountLedgers(
            currency = currency,
            direction = direction,
            bizType = bizType,
            startAt = startAt,
            endAt = endAt,
            currentPage = currentPage,
            pageSize = pageSize
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

    override suspend fun getAllAccountLedgersFromLocal(
        currency: String?,
        direction: String?,
        bizType: String?,
        startAt: Long?,
        endAt: Long?,
        currentPage: Int,
        pageSize: Int
    ): List<AccountLedgerResponse> = accountDao.getAccountLedgers(
        currency = currency,
        direction = direction,
        bizType = bizType,
        startAt = startAt,
        endAt = endAt,
        currentPage = currentPage,
        pageSize = pageSize
    )

    override suspend fun insertAllAccountLedgers(accountLedgers: List<AccountLedgerResponse>) = accountDao.insertAllAccountLedgers(accountLedgers)

    override suspend fun insertAccountLedger(accountLedger: AccountLedgerResponse) = accountDao.insertAccountLedger(accountLedger)

    override suspend fun deleteAccountLedger(accountLedger: AccountLedgerResponse) = accountDao.deleteAccountLedger(accountLedger)

    override suspend fun deleteAllAccountLedgers() = accountDao.deleteAllAccountLedgers()

    override suspend fun getTransferable(
        accountType: AccountType,
        accountCurrency: String
    ): Flow<Resource<AccountResponse>> = callbackFlow {
        val response = accountApi.getTransferable(
            currency = accountCurrency,
            type = accountType.value
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

    override suspend fun innerTransfer(
        innerTransferRequest: InnerTransferRequest
    ): Flow<Resource<InnerTransferResponse>> = callbackFlow {
        val response = accountApi.innerTransfer(
            request = innerTransferRequest
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
            getAllAccounts(forceRemote = true).collect {}
        }
        awaitClose { close() }
    }
}
