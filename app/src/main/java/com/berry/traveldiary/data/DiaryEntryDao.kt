package com.berry.traveldiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.berry.traveldiary.model.DiaryEntries
import com.berry.traveldiary.model.User

@Dao
interface DiaryEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDiaryEntry(diaryEntries: DiaryEntries)

    @Query("SELECT * FROM diaryEntry_table ORDER BY entryId ASC")
    fun readAllData(): MutableList<DiaryEntries>

    @Query("Select * from diaryEntry_table where title like  :desc")
    fun getSearchResults(desc: String): MutableList<DiaryEntries>

    @Query("SELECT * FROM diaryEntry_table WHERE id = :id")
    fun getDiaryEntryForUser(id: Int): MutableList<DiaryEntries>


}