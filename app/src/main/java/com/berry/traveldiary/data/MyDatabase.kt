package com.berry.traveldiary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.berry.traveldiary.model.DiaryEntries
import com.berry.traveldiary.model.Photos
import com.berry.traveldiary.model.User

@Database(entities = [User::class, DiaryEntries::class, Photos::class], version = 1, exportSchema = false)
abstract class MyDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun photosDao(): PhotosDao

    companion object {
        @Volatile
        private var INSTANCE: MyDatabase? = null

        fun getDatabase(context: Context): MyDatabase {

            INSTANCE?.let {
                return it
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyDatabase::class.java,
                    "my_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}