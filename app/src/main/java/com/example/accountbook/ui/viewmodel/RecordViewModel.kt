package com.example.accountbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.accountbook.data.AppDatabase
import com.example.accountbook.data.dao.CategoryTotal
import com.example.accountbook.data.entity.Record
import com.example.accountbook.data.repository.RecordRepository
import kotlinx.coroutines.launch

class RecordViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecordRepository
    val allRecords: LiveData<List<Record>>

    init {
        val recordDao = AppDatabase.getDatabase(application).recordDao()
        repository = RecordRepository(recordDao)
        allRecords = repository.allRecords
    }

    fun getRecordsByType(type: String): LiveData<List<Record>> {
        return repository.getRecordsByType(type)
    }

    fun getRecordsByDateRange(startDate: Long, endDate: Long): LiveData<List<Record>> {
        return repository.getRecordsByDateRange(startDate, endDate)
    }

    fun getRecordsByDateRangeAndType(startDate: Long, endDate: Long, type: String): LiveData<List<Record>> {
        return repository.getRecordsByDateRangeAndType(startDate, endDate, type)
    }

    fun searchRecords(keyword: String): LiveData<List<Record>> {
        return repository.searchRecords(keyword)
    }

    fun getTotalByTypeAndDateRange(type: String, startDate: Long, endDate: Long): LiveData<Double?> {
        return repository.getTotalByTypeAndDateRange(type, startDate, endDate)
    }

    fun getCategoryTotals(type: String, startDate: Long, endDate: Long): LiveData<List<CategoryTotal>> {
        return repository.getCategoryTotals(type, startDate, endDate)
    }

    fun insert(record: Record) = viewModelScope.launch {
        repository.insert(record)
    }

    fun update(record: Record) = viewModelScope.launch {
        repository.update(record)
    }

    fun delete(record: Record) = viewModelScope.launch {
        repository.delete(record)
    }
}