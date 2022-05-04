package com.shaho.tradingview.data.remote

import com.shaho.tradingview.data.model.request.LimitOrderCreateRequest
import com.shaho.tradingview.data.model.request.MarketOrderCreateRequest
import com.shaho.tradingview.data.model.response.CancelOrder
import com.shaho.tradingview.data.model.response.CancelOrderByClientOid
import com.shaho.tradingview.data.model.response.OrderCreateResponse
import com.shaho.tradingview.data.model.response.OrderResponse
import com.shaho.tradingview.data.model.response.RemotePaginationResponse
import com.shaho.tradingview.data.model.response.RemoteResponse
import com.shaho.tradingview.util.enum.StatusOrderType
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OrderApi {

    @POST("api/v1/orders")
    suspend fun createLimitOrder(
        @Body request: LimitOrderCreateRequest
    ): RemoteResponse<OrderCreateResponse>

    @POST("api/v1/orders")
    suspend fun createMarketOrder(
        @Body request: MarketOrderCreateRequest
    ): RemoteResponse<OrderCreateResponse>

    @DELETE("api/v1/orders/{orderId}")
    suspend fun cancelAnOrder(
        @Path("orderId") orderId: String
    ): RemoteResponse<CancelOrder>

    @DELETE("api/v1/order/client-order/{clientOid}")
    suspend fun cancelSingleOrderByClientOid(
        @Path("clientOid") clientOid: String
    ): RemoteResponse<CancelOrderByClientOid>

    @DELETE("api/v1/orders")
    suspend fun cancelAllOrders(
        @Query("symbol") symbol: String? = null,
        @Query("tradeType") tradeType: String? = null,
    ): RemoteResponse<CancelOrder>

    @GET("api/v1/orders")
    suspend fun listOrders(
        @Query("currentPage") currentPage: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("status") status: String = StatusOrderType.ACTIVE.value,
        @Query("symbol") symbol: String? = null,
        @Query("side") side: String? = null,
        @Query("type") type: String? = null,
        @Query("tradeType") tradeType: String,
        @Query("startAt") startAt: Long? = null,
        @Query("endAt") endAt: Long? = null,
    ): RemoteResponse<RemotePaginationResponse<OrderResponse>>
}
