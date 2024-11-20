package com.berry.traveldiary.repository

import androidx.lifecycle.LiveData
import com.berry.traveldiary.data.DiaryEntryDao
import com.berry.traveldiary.data.UserDao
import com.berry.traveldiary.model.DiaryEntries
import com.berry.traveldiary.model.User

class UserRepository(private val diaryEntriesDao: DiaryEntryDao) {

//    val readAllData: LiveData<List<DiaryEntries>> = diaryEntriesDao.readAllData()

//    suspend fun addUser(user: User){
//        userDao.addUser(user)
//    }

 /*   suspend fun updateUser(user: User){
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User){
        userDao.deleteUser(user)
    }

    suspend fun deleteAllUsers(){
        userDao.deleteAllUsers()
    }*/


}