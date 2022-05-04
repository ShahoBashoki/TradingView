package com.shaho.tradingview.repository.account

import com.shaho.tradingview.data.model.request.CreateAccountRequest
import com.shaho.tradingview.data.model.request.InnerTransferRequest
import com.shaho.tradingview.data.model.response.AccountLedgerResponse
import com.shaho.tradingview.data.model.response.AccountResponse
import com.shaho.tradingview.data.model.response.CreateAccountResponse
import com.shaho.tradingview.data.model.response.InnerTransferResponse
import com.shaho.tradingview.data.model.response.RemotePaginationResponse
import com.shaho.tradingview.util.Resource
import com.shaho.tradingview.util.enum.AccountType
import kotlinx.coroutines.flow.Flow

interface IAccountRepository {
    suspend fun createAccount(
        createAccountRequest: CreateAccountRequest
    ): Flow<Resource<CreateAccountResponse>>

    suspend fun getAllAccounts(
        forceRemote: Boolean = false,
        accountType: AccountType? = null,
        accountCurrency: String? = null
    ): Flow<Resource<List<AccountResponse>>>

    suspend fun getAllAccountsFromRemote(
        accountType: AccountType? = null,
        accountCurrency: String? = null
    ): Flow<Resource<List<AccountResponse>>>

    suspend fun getAllAccountsFromLocal(
        accountType: AccountType? = null,
        accountCurrency: String? = null
    ): List<AccountResponse>

    suspend fun getAccount(
        accountId: String
    ): Flow<Resource<AccountResponse>>

    suspend fun insertAllAccounts(
        accounts: List<AccountResponse>
    )

    suspend fun insertAccount(
        account: AccountResponse
    )

    suspend fun deleteAccount(
        account: AccountResponse
    )

    suspend fun deleteAllAccounts()

    suspend fun getAllAccountLedgers(
        forceRemote: Boolean = false,
        currency: String? = null,
        direction: String? = null,
        bizType: String? = null,
        startAt: Long? = null,
        endAt: Long? = null,
        currentPage: Int = 1,
        pageSize: Int = 50
    ): Flow<Resource<List<AccountLedgerResponse>>>

    suspend fun getAllAccountLedgersFromRemote(
        currency: String? = null,
        direction: String? = null,
        bizType: String? = null,
        startAt: Long? = null,
        endAt: Long? = null,
        currentPage: Int = 1,
        pageSize: Int = 50
    ): Flow<Resource<RemotePaginationResponse<AccountLedgerResponse>>>

    suspend fun getAllAccountLedgersFromLocal(
        currency: String? = null,
        direction: String? = null,
        bizType: String? = null,
        startAt: Long? = null,
        endAt: Long? = null,
        currentPage: Int = 1,
        pageSize: Int = 50
    ): List<AccountLedgerResponse>

    suspend fun insertAllAccountLedgers(
        accountLedgers: List<AccountLedgerResponse>
    )

    suspend fun insertAccountLedger(
        accountLedger: AccountLedgerResponse
    )

    suspend fun deleteAccountLedger(
        accountLedger: AccountLedgerResponse
    )

    suspend fun deleteAllAccountLedgers()

    suspend fun getTransferable(
        accountType: AccountType,
        accountCurrency: String
    ): Flow<Resource<AccountResponse>>

    suspend fun innerTransfer(
        innerTransferRequest: InnerTransferRequest
    ): Flow<Resource<InnerTransferResponse>>
}
