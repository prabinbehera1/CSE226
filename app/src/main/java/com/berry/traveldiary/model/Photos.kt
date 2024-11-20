package com.berry.traveldiary.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos_table",
    foreignKeys = [ForeignKey(
        entity = DiaryEntries::class,
        parentColumns = ["entryId"],
        childColumns = ["entryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Photos(
    @PrimaryKey(autoGenerate = true)
    val photoId: Int,
    val entryId: Int,
    var imagePath: String,
    val caption: String,
)
