package com.example.accountbook.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.accountbook.data.entity.Record

@Dao
interface RecordDao {
    @Query("SELECT * FROM records ORDER BY date DESC, createdAt DESC")
    fun getAllRecords(): LiveData<List<Record>>

    @Query("SELECT * FROM records WHERE type = :type ORDER BY date DESC, createdAt DESC")
    fun getRecordsByType(type: String): LiveData<List<Record>>

    @Query("SELECT * FROM records WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getRecordsByDateRange(startDate: Long, endDate: Long): LiveData<List<Record>>

    @Query("SELECT * FROM records WHERE date BETWEEN :startDate AND :endDate AND type = :type ORDER BY date DESC")
    fun getRecordsByDateRangeAndType(startDate: Long, endDate: Long, type: String): LiveData<List<Record>>

    @Query("SELECT * FROM records WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getRecordsByCategory(categoryId: Long): LiveData<List<Record>>

    @Query("SELECT * FROM records WHERE note LIKE '%' || :keyword || '%' ORDER BY date DESC")
    fun searchRecords(keyword: String): LiveData<List<Record>>

    @Query("SELECT SUM(amount) FROM records WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    fun getTotalByTypeAndDateRange(type: String, startDate: Long, endDate: Long): LiveData<Double?>

    @Query("SELECT categoryName, SUM(amount) as total FROM records WHERE type = :type AND date BETWEEN :startDate AND :endDate GROUP BY categoryName")
    fun getCategoryTotals(type: String, startDate: Long, endDate: Long): LiveData<List<CategoryTotal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record): Long

    @Update
    suspend fun update(record: Record)

    @Delete
    suspend fun delete(record: Record)
}

data class CategoryTotal(
    val categoryName: String,
    val total: Double
)