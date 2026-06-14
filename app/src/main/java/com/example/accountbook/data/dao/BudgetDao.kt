package com.example.accountbook.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.accountbook.data.entity.Budget

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE month = :month")
    fun getBudgetByMonth(month: String): LiveData<Budget?>

    @Query("SELECT * FROM budgets ORDER BY month DESC")
    fun getAllBudgets(): LiveData<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)
}