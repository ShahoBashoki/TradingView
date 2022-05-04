package com.shaho.tradingview.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shaho.tradingview.data.model.response.UserResponse

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
    suspend fun getUsers(): List<UserResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUsers(user: List<UserResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserResponse)

    @Delete
    suspend fun delete(user: UserResponse)

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()
}
