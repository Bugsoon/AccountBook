package com.example.accountbook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val month: String,
    val createdAt: Long = System.currentTimeMillis()
)