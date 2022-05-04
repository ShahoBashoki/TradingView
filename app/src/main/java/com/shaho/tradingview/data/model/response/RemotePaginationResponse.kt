package com.shaho.tradingview.data.model.response

data class RemotePaginationResponse<T>(
    val currentPage: Long? = null,
    val pageSize: Long? = null,
    val totalNum: Long? = null,
    val totalPage: Long? = null,
    val items: List<T>? = null
)
