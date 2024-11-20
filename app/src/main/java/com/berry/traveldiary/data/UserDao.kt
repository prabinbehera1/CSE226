package com.berry.traveldiary.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.berry.traveldiary.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("SELECT EXISTS(SELECT * FROM user_table WHERE userName = :userName or email = :email)")
    suspend fun isRecordExists(userName: String?, email: String): Boolean

    @Query("SELECT EXISTS(SELECT * FROM user_table WHERE userName = :userName and password = :password)")
    suspend fun isUserExists(userName: String?, password: String): Boolean

    @Query("SELECT * FROM user_table WHERE username = :username")
    fun getUser(username: String) : User

    @Query("UPDATE user_table SET password = :newPassword WHERE id = :id")
    suspend fun updatePassword(id: Int, newPassword: String)

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

}