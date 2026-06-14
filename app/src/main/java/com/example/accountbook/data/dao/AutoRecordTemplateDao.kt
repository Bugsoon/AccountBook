package com.example.accountbook.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.accountbook.data.entity.AutoRecordTemplate

@Dao
interface AutoRecordTemplateDao {
    @Query("SELECT * FROM auto_record_templates ORDER BY createdAt DESC")
    fun getAllTemplates(): LiveData<List<AutoRecordTemplate>>

    @Query("SELECT * FROM auto_record_templates WHERE isActive = 1 AND nextTriggerDate <= :currentDate")
    suspend fun getTemplatesToTrigger(currentDate: Long): List<AutoRecordTemplate>

    @Query("SELECT * FROM auto_record_templates WHERE id = :id")
    suspend fun getTemplateById(id: Long): AutoRecordTemplate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: AutoRecordTemplate): Long

    @Update
    suspend fun update(template: AutoRecordTemplate)

    @Delete
    suspend fun delete(template: AutoRecordTemplate)
}