package com.shaho.tradingview.repository.user

import com.shaho.tradingview.data.model.response.UserResponse
import com.shaho.tradingview.util.Resource
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun getAllUsersFromRemote(): Flow<Resource<List<UserResponse>>>
    suspend fun getAllUsersFromLocal(): List<UserResponse>
    suspend fun getAllUsers(forceRemote: Boolean = false): Flow<Resource<List<UserResponse>>>
    suspend fun insertAllUsers(users: List<UserResponse>)
    suspend fun insertUser(user: UserResponse)
    suspend fun deleteUser(user: UserResponse)
    suspend fun deleteAllUsers()
}
