package com.berry.traveldiary.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.model.DiaryEntries
import com.berry.traveldiary.repository.UserRepository

class HomeViewModel(application: Application) : ViewModel() {

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is home Fragment"
//    }
//    val text: LiveData<String> = _text

//    val readAllData: LiveData<List<DiaryEntries>>
//    private val repository: UserRepository


//    init {
//        val diaryEntriesDao = MyDatabase.getDatabase(application).diaryEntryDao()
//        repository = UserRepository(diaryEntriesDao)
//        readAllData = repository.readAllData
//    }

}