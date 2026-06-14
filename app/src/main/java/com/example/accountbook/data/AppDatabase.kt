package com.example.accountbook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.accountbook.data.dao.AutoRecordTemplateDao
import com.example.accountbook.data.dao.BudgetDao
import com.example.accountbook.data.dao.CategoryDao
import com.example.accountbook.data.dao.RecordDao
import com.example.accountbook.data.entity.AutoRecordTemplate
import com.example.accountbook.data.entity.Budget
import com.example.accountbook.data.entity.Category
import com.example.accountbook.data.entity.Record
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Record::class, Category::class, Budget::class, AutoRecordTemplate::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun autoRecordTemplateDao(): AutoRecordTemplateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "account_book_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDefaultCategories(database.categoryDao())
                }
            }
        }

        private suspend fun populateDefaultCategories(categoryDao: CategoryDao) {
            val defaultCategories = listOf(
                Category(name = "餐饮", icon = "ic_cat_food", type = "expense", isDefault = true, sortOrder = 1),
                Category(name = "交通", icon = "ic_cat_transport", type = "expense", isDefault = true, sortOrder = 2),
                Category(name = "购物", icon = "ic_cat_shopping", type = "expense", isDefault = true, sortOrder = 3),
                Category(name = "娱乐", icon = "ic_cat_entertainment", type = "expense", isDefault = true, sortOrder = 4),
                Category(name = "居住", icon = "ic_cat_daily", type = "expense", isDefault = true, sortOrder = 5),
                Category(name = "医疗", icon = "ic_cat_medical", type = "expense", isDefault = true, sortOrder = 6),
                Category(name = "日用", icon = "ic_cat_daily", type = "expense", isDefault = true, sortOrder = 7),
                Category(name = "通讯", icon = "ic_cat_communication", type = "expense", isDefault = true, sortOrder = 8),
                Category(name = "其他支出", icon = "ic_cat_other", type = "expense", isDefault = true, sortOrder = 9),
                Category(name = "工资", icon = "ic_cat_salary", type = "income", isDefault = true, sortOrder = 10),
                Category(name = "奖金", icon = "ic_cat_bonus", type = "income", isDefault = true, sortOrder = 11),
                Category(name = "投资", icon = "ic_cat_investment", type = "income", isDefault = true, sortOrder = 12),
                Category(name = "兼职", icon = "ic_cat_parttime", type = "income", isDefault = true, sortOrder = 13),
                Category(name = "其他收入", icon = "ic_cat_other", type = "income", isDefault = true, sortOrder = 14),
            )
            defaultCategories.forEach { categoryDao.insert(it) }
        }
    }
}