package com.shaho.tradingview.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "account_table")
data class AccountResponse(
    @PrimaryKey
    val id: String,
    val currency: String? = null,
    val type: String? = null,
    val balance: BigDecimal? = null,
    val available: BigDecimal? = null,
    val holds: BigDecimal? = null,
    val transferable: String? = null
)
