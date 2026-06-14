package com.example.accountbook.data.repository

import androidx.lifecycle.LiveData
import com.example.accountbook.data.dao.AutoRecordTemplateDao
import com.example.accountbook.data.entity.AutoRecordTemplate

class AutoRecordTemplateRepository(private val templateDao: AutoRecordTemplateDao) {
    val allTemplates: LiveData<List<AutoRecordTemplate>> = templateDao.getAllTemplates()

    suspend fun getTemplatesToTrigger(currentDate: Long): List<AutoRecordTemplate> {
        return templateDao.getTemplatesToTrigger(currentDate)
    }

    suspend fun getTemplateById(id: Long): AutoRecordTemplate? {
        return templateDao.getTemplateById(id)
    }

    suspend fun insert(template: AutoRecordTemplate): Long {
        return templateDao.insert(template)
    }

    suspend fun update(template: AutoRecordTemplate) {
        templateDao.update(template)
    }

    suspend fun delete(template: AutoRecordTemplate) {
        templateDao.delete(template)
    }
}