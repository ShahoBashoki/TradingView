package com.shaho.tradingview.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shaho.tradingview.data.model.response.AccountLedgerResponse
import com.shaho.tradingview.data.model.response.AccountResponse

@Dao
interface AccountDao {
    @Query(
        "SELECT * FROM account_table " +
            "where (:type IS NULL OR type LIKE :type)" +
            " AND (:currency IS NULL OR currency LIKE :currency)"
    )
    suspend fun getAccounts(
        type: String?,
        currency: String?
    ): List<AccountResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAccounts(accounts: List<AccountResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountResponse)

    @Delete
    suspend fun deleteAccount(account: AccountResponse)

    @Query("DELETE FROM account_table")
    suspend fun deleteAllAccounts()

    @Query(
        "SELECT * FROM account_ledger_table " +
            "where (:currency IS NULL OR currency LIKE :currency)" +
            " AND (:direction IS NULL OR direction LIKE :direction)" +
            " AND (:bizType IS NULL OR bizType LIKE :bizType)" +
            " AND ((:startAt IS NULL AND :endAt IS NULL ) OR createdAt BETWEEN :startAt AND :endAt)" +
            " LIMIT ((:currentPage - 1) * :pageSize) , :pageSize"
    )
    suspend fun getAccountLedgers(
        currency: String?,
        direction: String?,
        bizType: String?,
        startAt: Long?,
        endAt: Long?,
        currentPage: Int?,
        pageSize: Int?
    ): List<AccountLedgerResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAccountLedgers(accounts: List<AccountLedgerResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountLedger(account: AccountLedgerResponse)

    @Delete
    suspend fun deleteAccountLedger(account: AccountLedgerResponse)

    @Query("DELETE FROM account_ledger_table")
    suspend fun deleteAllAccountLedgers()
}
