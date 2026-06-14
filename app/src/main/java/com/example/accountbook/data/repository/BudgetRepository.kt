package com.example.accountbook.data.repository

import androidx.lifecycle.LiveData
import com.example.accountbook.data.dao.BudgetDao
import com.example.accountbook.data.entity.Budget

class BudgetRepository(private val budgetDao: BudgetDao) {
    fun getBudgetByMonth(month: String): LiveData<Budget?> {
        return budgetDao.getBudgetByMonth(month)
    }

    val allBudgets: LiveData<List<Budget>> = budgetDao.getAllBudgets()

    suspend fun insert(budget: Budget): Long {
        return budgetDao.insert(budget)
    }

    suspend fun update(budget: Budget) {
        budgetDao.update(budget)
    }

    suspend fun delete(budget: Budget) {
        budgetDao.delete(budget)
    }
}