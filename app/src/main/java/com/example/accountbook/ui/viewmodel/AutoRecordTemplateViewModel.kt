package com.example.accountbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.accountbook.data.AppDatabase
import com.example.accountbook.data.entity.AutoRecordTemplate
import com.example.accountbook.data.repository.AutoRecordTemplateRepository
import kotlinx.coroutines.launch

class AutoRecordTemplateViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AutoRecordTemplateRepository
    val allTemplates: LiveData<List<AutoRecordTemplate>>

    init {
        val templateDao = AppDatabase.getDatabase(application).autoRecordTemplateDao()
        repository = AutoRecordTemplateRepository(templateDao)
        allTemplates = repository.allTemplates
    }

    fun getTemplatesToTrigger(currentDate: Long, callback: (List<AutoRecordTemplate>) -> Unit) {
        viewModelScope.launch {
            val templates = repository.getTemplatesToTrigger(currentDate)
            callback(templates)
        }
    }

    fun insert(template: AutoRecordTemplate) = viewModelScope.launch {
        repository.insert(template)
    }

    fun update(template: AutoRecordTemplate) = viewModelScope.launch {
        repository.update(template)
    }

    fun delete(template: AutoRecordTemplate) = viewModelScope.launch {
        repository.delete(template)
    }
}