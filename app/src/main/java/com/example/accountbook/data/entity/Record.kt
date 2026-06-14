package com.example.accountbook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String,
    val categoryId: Long,
    val categoryName: String,
    val note: String = "",
    val date: Long,
    val createdAt: Long = System.currentTimeMillis()
)