package com.shaho.tradingview.repository.user

import com.shaho.tradingview.data.local.dao.UserDao
import com.shaho.tradingview.data.model.response.UserResponse
import com.shaho.tradingview.data.remote.UserApi
import com.shaho.tradingview.util.Resource
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userApi: UserApi,
    private val userDao: UserDao
) : IUserRepository {
    override suspend fun getAllUsersFromRemote(): Flow<Resource<List<UserResponse>>> =
        callbackFlow {
            val response = userApi.getUsers()
            response.msg?.let { itMessage ->
                trySend(
                    Resource.Failure(
                        message = "code: ${response.code}, message: $itMessage"
                    )
                )
            } ?: kotlin.run {
                trySend(
                    Resource.Success(
                        data = response.data
                    )
                )
            }
            awaitClose { close() }
        }

    override suspend fun getAllUsersFromLocal(): List<UserResponse> = userDao.getUsers()

    override suspend fun getAllUsers(forceRemote: Boolean): Flow<Resource<List<UserResponse>>> =
        callbackFlow {
            if (forceRemote) {
                getAllUsersFromRemote().catch {
                    trySend(Resource.Failure(message = ""))
                }.collect {
                    when (it) {
                        is Resource.Failure -> trySend(Resource.Failure(message = ""))
                        Resource.Loading -> trySend(Resource.Loading)
                        is Resource.Success -> {
                            it.data?.let { listUser ->
                                insertAllUsers(listUser)
                            } ?: run {
                                trySend(Resource.Failure(message = ""))
                            }
                        }
                    }

                    trySend(Resource.Success(getAllUsersFromLocal()))
                }
            } else {
                trySend(Resource.Success(getAllUsersFromLocal()))
            }
            awaitClose { cancel() }
        }

    override suspend fun insertAllUsers(users: List<UserResponse>) = userDao.insertAllUsers(users)

    override suspend fun insertUser(user: UserResponse) = userDao.insert(user)

    override suspend fun deleteUser(user: UserResponse) = userDao.delete(user)

    override suspend fun deleteAllUsers() = userDao.deleteAllUsers()
}
