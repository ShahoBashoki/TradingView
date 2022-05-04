package com.shaho.tradingview.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_table")
data class CurrencyResponse(
    @PrimaryKey
    val currency: String,
    val name: String? = null,
    val fullName: String? = null,
    val precision: Long? = null,
    val confirms: Long? = null,
    val contractAddress: String? = null,
    val withdrawalMinSize: String? = null,
    val withdrawalMinFee: String? = null,
    val isWithdrawEnabled: Boolean? = null,
    val isDepositEnabled: Boolean? = null,
    val isMarginEnabled: Boolean? = null,
    val isDebitEnabled: Boolean? = null
)
