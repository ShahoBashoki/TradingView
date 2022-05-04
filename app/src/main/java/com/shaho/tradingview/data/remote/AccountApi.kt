package com.shaho.tradingview.data.remote

import com.shaho.tradingview.data.model.request.CreateAccountRequest
import com.shaho.tradingview.data.model.request.InnerTransferRequest
import com.shaho.tradingview.data.model.response.AccountLedgerResponse
import com.shaho.tradingview.data.model.response.AccountResponse
import com.shaho.tradingview.data.model.response.CreateAccountResponse
import com.shaho.tradingview.data.model.response.InnerTransferResponse
import com.shaho.tradingview.data.model.response.RemotePaginationResponse
import com.shaho.tradingview.data.model.response.RemoteResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountApi {

    @POST("api/v1/accounts")
    suspend fun createAccount(
        @Body request: CreateAccountRequest
    ): RemoteResponse<CreateAccountResponse>

    @GET("api/v1/accounts")
    suspend fun getAccounts(
        @Query("type") type: String? = null,
        @Query("currency") currency: String? = null
    ): RemoteResponse<List<AccountResponse>>

    @GET("api/v1/accounts/{accountId}")
    suspend fun getAccount(
        @Path("accountId") accountId: String
    ): RemoteResponse<AccountResponse>

    @GET("api/v1/accounts/ledgers")
    suspend fun getAccountLedgers(
        @Query("currency") currency: String? = null,
        @Query("direction") direction: String? = null,
        @Query("bizType") bizType: String? = null,
        @Query("startAt") startAt: Long? = null,
        @Query("endAt") endAt: Long? = null,
        @Query("currentPage") currentPage: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): RemoteResponse<RemotePaginationResponse<AccountLedgerResponse>>

    @GET("api/v1/accounts/transferable")
    suspend fun getTransferable(
        @Query("currency") currency: String,
        @Query("type") type: String
    ): RemoteResponse<AccountResponse>

    @POST("api/v2/accounts/inner-transfer")
    suspend fun innerTransfer(
        @Body request: InnerTransferRequest
    ): RemoteResponse<InnerTransferResponse>
}
