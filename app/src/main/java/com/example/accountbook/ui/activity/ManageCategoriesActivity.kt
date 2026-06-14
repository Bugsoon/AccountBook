package com.example.accountbook.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.data.entity.Category
import com.example.accountbook.ui.adapter.CategoryManageAdapter
import com.example.accountbook.ui.viewmodel.CategoryViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout

class ManageCategoriesActivity : AppCompatActivity() {
    private lateinit var viewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryManageAdapter
    private var currentType = "expense"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_categories)

        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        setupToolbar()
        setupTabs()
        setupRecyclerView()
        setupAddButton()
        observeData()
    }

    private fun setupToolbar() {
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupTabs() {
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        tabLayout.addTab(tabLayout.newTab().setText("支出"))
        tabLayout.addTab(tabLayout.newTab().setText("收入"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentType = when (tab?.position) {
                    0 -> "expense"
                    1 -> "income"
                    else -> "expense"
                }
                observeData()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        val rvCategories: RecyclerView = findViewById(R.id.rv_categories)
        categoryAdapter = CategoryManageAdapter(
            onEditClick = { category ->
                showEditCategoryDialog(category)
            },
            onDeleteClick = { category ->
                showDeleteCategoryDialog(category)
            }
        )
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = categoryAdapter
    }

    private fun setupAddButton() {
        val btnAdd: MaterialButton = findViewById(R.id.btn_add_category)
        btnAdd.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun observeData() {
        viewModel.getCategoriesByType(currentType).observe(this) { categories ->
            categoryAdapter.submitList(categories)
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val etName: EditText = dialogView.findViewById(R.id.et_category_name)

        AlertDialog.Builder(this)
            .setTitle("添加分类")
            .setView(dialogView)
            .setPositiveButton("添加") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val category = Category(
                        name = name,
                        icon = "ic_other",
                        type = currentType,
                        isDefault = false
                    )
                    viewModel.insert(category)
                    Toast.makeText(this, "分类已添加", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "请输入分类名称", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val etName: EditText = dialogView.findViewById(R.id.et_category_name)
        etName.setText(category.name)

        AlertDialog.Builder(this)
            .setTitle("编辑分类")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val updatedCategory = category.copy(name = name)
                    viewModel.update(updatedCategory)
                    Toast.makeText(this, "分类已更新", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "请输入分类名称", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showDeleteCategoryDialog(category: Category) {
        if (category.isDefault) {
            Toast.makeText(this, "默认分类不能删除", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("删除分类")
            .setMessage("确定要删除分类 \"${category.name}\" 吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.delete(category)
                Toast.makeText(this, "分类已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}