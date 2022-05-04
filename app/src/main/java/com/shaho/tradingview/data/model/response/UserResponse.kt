package com.shaho.tradingview.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "user_table")
data class UserResponse(
    @PrimaryKey
    val userId: String,
    val subName: String? = null,
    val type: BigDecimal? = null,
    val remarks: String? = null
)
