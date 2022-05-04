package com.shaho.tradingview.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_ledger_table")
data class AccountLedgerResponse(
    @PrimaryKey
    val id: String,
    val currency: String? = null,
    val amount: String? = null,
    val fee: String? = null,
    val balance: String? = null,
    val accountType: String? = null,
    val bizType: String? = null,
    val direction: String? = null,
    val createdAt: Long? = null,
    val context: String? = null
)
