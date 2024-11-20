package com.berry.traveldiary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.berry.traveldiary.model.Photos

@Dao
interface PhotosDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhotoData(photos: Photos)

    @Query("SELECT * FROM photos_table ORDER BY photoId ASC")
    fun readAllData(): MutableList<Photos>

    @Delete
    suspend fun delete(photos: Photos)

    @Query("DELETE FROM photos_table WHERE photoId = :photoId")
    suspend fun deleteByPhotoId(photoId: Int)

    @Query("SELECT * FROM photos_table WHERE entryId = :entryId ORDER BY photoId ASC")
    fun getPhotosById(entryId: Int): MutableList<Photos>


}