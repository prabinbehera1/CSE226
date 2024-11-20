package com.berry.traveldiary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.model.User
import com.berry.traveldiary.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

//    val readAllData: LiveData<List<User>>
//    private val repository: UserRepository

//    init {
//        val userDao = MyDatabase.getDatabase(application).userDao()
//        repository = UserRepository(userDao)
//        readAllData = repository.readAllData
//    }
//
//    fun addUser(user: User) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.addUser(user)
//        }
//    }

   /* fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(user)
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllUsers()
        }
    }*/

}