package com.berry.traveldiary.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "diaryEntry_table",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DiaryEntries(
    @PrimaryKey(autoGenerate = true)
    val entryId: Int,
    val title: String,
    val date: String,
    val location: String,
    val description: String,
    val id:Int,
    val coverImg: String
)
