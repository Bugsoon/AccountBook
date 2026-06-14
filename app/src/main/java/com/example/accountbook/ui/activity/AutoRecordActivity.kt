package com.example.accountbook.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.data.entity.AutoRecordTemplate
import com.example.accountbook.data.entity.Category
import com.example.accountbook.ui.adapter.TemplateAdapter
import com.example.accountbook.ui.viewmodel.AutoRecordTemplateViewModel
import com.example.accountbook.ui.viewmodel.CategoryViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AutoRecordActivity : AppCompatActivity() {
    private lateinit var templateViewModel: AutoRecordTemplateViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var templateAdapter: TemplateAdapter

    private var selectedType = "expense"
    private var categories: List<Category> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_record)

        templateViewModel = ViewModelProvider(this)[AutoRecordTemplateViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        setupToolbar()
        setupRecyclerView()
        setupAddButton()
        observeData()
    }

    private fun setupToolbar() {
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        val rvTemplates: RecyclerView = findViewById(R.id.rv_templates)
        templateAdapter = TemplateAdapter(
            onItemClick = { template ->
                // TODO: Show edit dialog
            },
            onToggleActive = { template, isActive ->
                val updatedTemplate = template.copy(isActive = isActive)
                templateViewModel.update(updatedTemplate)
            }
        )
        rvTemplates.layoutManager = LinearLayoutManager(this)
        rvTemplates.adapter = templateAdapter
    }

    private fun setupAddButton() {
        val btnAdd: MaterialButton = findViewById(R.id.btn_add_template)
        btnAdd.setOnClickListener {
            showAddTemplateDialog()
        }
    }

    private fun observeData() {
        templateViewModel.allTemplates.observe(this) { templates ->
            templateAdapter.submitList(templates)
        }

        categoryViewModel.allCategories.observe(this) { categories ->
            this.categories = categories
        }
    }

    private fun showAddTemplateDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_template, null)
        val etName: EditText = dialogView.findViewById(R.id.et_name)
        val etAmount: EditText = dialogView.findViewById(R.id.et_amount)
        val etNote: EditText = dialogView.findViewById(R.id.et_note)
        val toggleType: MaterialButtonToggleGroup = dialogView.findViewById(R.id.toggle_type)
        val spinnerCategory: Spinner = dialogView.findViewById(R.id.spinner_category)
        val spinnerFrequency: Spinner = dialogView.findViewById(R.id.spinner_frequency)

        // Setup type toggle
        toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedType = when (checkedId) {
                    R.id.btn_expense -> "expense"
                    R.id.btn_income -> "income"
                    else -> "expense"
                }
                updateCategorySpinner(spinnerCategory)
            }
        }

        // Setup category spinner
        updateCategorySpinner(spinnerCategory)

        // Setup frequency spinner
        val frequencies = arrayOf("每天", "每周", "每月", "每年")
        val frequencyValues = arrayOf("daily", "weekly", "monthly", "yearly")
        val frequencyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencies)
        frequencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFrequency.adapter = frequencyAdapter

        AlertDialog.Builder(this)
            .setTitle("添加自动记账模板")
            .setView(dialogView)
            .setPositiveButton("添加") { _, _ ->
                val name = etName.text.toString().trim()
                val amountStr = etAmount.text.toString().trim()
                val note = etNote.text.toString().trim()

                if (name.isEmpty() || amountStr.isEmpty()) {
                    Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val amount = amountStr.toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    Toast.makeText(this, "请输入有效金额", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedCategory = if (spinnerCategory.selectedItem is Category) {
                    spinnerCategory.selectedItem as Category
                } else {
                    null
                }

                if (selectedCategory == null) {
                    Toast.makeText(this, "请选择分类", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val frequency = frequencyValues[spinnerFrequency.selectedItemPosition]
                val nextTriggerDate = calculateNextTriggerDate(frequency)

                val template = AutoRecordTemplate(
                    name = name,
                    amount = amount,
                    type = selectedType,
                    categoryId = selectedCategory.id,
                    categoryName = selectedCategory.name,
                    note = note,
                    frequency = frequency,
                    nextTriggerDate = nextTriggerDate
                )

                templateViewModel.insert(template)
                Toast.makeText(this, "模板已添加", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun updateCategorySpinner(spinner: Spinner) {
        val filteredCategories = categories.filter { it.type == selectedType }
        val categoryNames = filteredCategories.map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun calculateNextTriggerDate(frequency: String): Long {
        val calendar = Calendar.getInstance()
        when (frequency) {
            "daily" -> calendar.add(Calendar.DAY_OF_YEAR, 1)
            "weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
            "monthly" -> calendar.add(Calendar.MONTH, 1)
            "yearly" -> calendar.add(Calendar.YEAR, 1)
        }
        return calendar.timeInMillis
    }
}