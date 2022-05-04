package com.shaho.tradingview.data.remote

import com.shaho.tradingview.data.model.response.RemoteResponse
import com.shaho.tradingview.data.model.response.UserResponse
import retrofit2.http.GET

interface UserApi {

    @GET("api/v1/sub/user")
    suspend fun getUsers(): RemoteResponse<List<UserResponse>>
}
