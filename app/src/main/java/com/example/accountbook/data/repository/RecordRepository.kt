package com.example.accountbook.data.repository

import androidx.lifecycle.LiveData
import com.example.accountbook.data.dao.CategoryTotal
import com.example.accountbook.data.dao.RecordDao
import com.example.accountbook.data.entity.Record

class RecordRepository(private val recordDao: RecordDao) {
    val allRecords: LiveData<List<Record>> = recordDao.getAllRecords()

    fun getRecordsByType(type: String): LiveData<List<Record>> {
        return recordDao.getRecordsByType(type)
    }

    fun getRecordsByDateRange(startDate: Long, endDate: Long): LiveData<List<Record>> {
        return recordDao.getRecordsByDateRange(startDate, endDate)
    }

    fun getRecordsByDateRangeAndType(startDate: Long, endDate: Long, type: String): LiveData<List<Record>> {
        return recordDao.getRecordsByDateRangeAndType(startDate, endDate, type)
    }

    fun searchRecords(keyword: String): LiveData<List<Record>> {
        return recordDao.searchRecords(keyword)
    }

    fun getTotalByTypeAndDateRange(type: String, startDate: Long, endDate: Long): LiveData<Double?> {
        return recordDao.getTotalByTypeAndDateRange(type, startDate, endDate)
    }

    fun getCategoryTotals(type: String, startDate: Long, endDate: Long): LiveData<List<CategoryTotal>> {
        return recordDao.getCategoryTotals(type, startDate, endDate)
    }

    suspend fun insert(record: Record): Long {
        return recordDao.insert(record)
    }

    suspend fun update(record: Record) {
        recordDao.update(record)
    }

    suspend fun delete(record: Record) {
        recordDao.delete(record)
    }
}