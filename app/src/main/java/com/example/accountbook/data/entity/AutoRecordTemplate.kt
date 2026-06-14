package com.example.accountbook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auto_record_templates")
data class AutoRecordTemplate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val type: String,
    val categoryId: Long,
    val categoryName: String,
    val note: String = "",
    val frequency: String,
    val nextTriggerDate: Long,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)