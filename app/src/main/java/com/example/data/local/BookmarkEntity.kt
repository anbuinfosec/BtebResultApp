package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_bookmarks")
data class BookmarkEntity(
    @PrimaryKey val roll: String,
    val curriculum: String,
    val instituteName: String,
    val regulation: String,
    val status: String,
    val savedAt: Long = System.currentTimeMillis()
)
