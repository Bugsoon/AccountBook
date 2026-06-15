package com.example.accountbook.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.data.entity.Category
import com.example.accountbook.ui.adapter.CategoryManageAdapter
import com.example.accountbook.ui.adapter.IconAdapter
import com.example.accountbook.ui.viewmodel.CategoryViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout

class ManageCategoriesActivity : AppCompatActivity() {
    private lateinit var viewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryManageAdapter
    private var currentType = "expense"

    private val allIcons = listOf(
        R.drawable.ic_cat_food,
        R.drawable.ic_cat_shopping,
        R.drawable.ic_cat_daily,
        R.drawable.ic_cat_transport,
        R.drawable.ic_cat_fruit,
        R.drawable.ic_cat_snacks,
        R.drawable.ic_cat_sports,
        R.drawable.ic_cat_entertainment,
        R.drawable.ic_cat_communication,
        R.drawable.ic_cat_clothing,
        R.drawable.ic_cat_social,
        R.drawable.ic_cat_medical,
        R.drawable.ic_cat_books,
        R.drawable.ic_cat_delivery,
        R.drawable.ic_cat_internet,
        R.drawable.ic_cat_game,
        R.drawable.ic_cat_study,
        R.drawable.ic_cat_salary,
        R.drawable.ic_cat_bonus,
        R.drawable.ic_cat_investment,
        R.drawable.ic_cat_parttime,
        R.drawable.ic_cat_other
    )

    private val iconResToName = mapOf(
        R.drawable.ic_cat_food to "ic_cat_food",
        R.drawable.ic_cat_shopping to "ic_cat_shopping",
        R.drawable.ic_cat_daily to "ic_cat_daily",
        R.drawable.ic_cat_transport to "ic_cat_transport",
        R.drawable.ic_cat_fruit to "ic_cat_fruit",
        R.drawable.ic_cat_snacks to "ic_cat_snacks",
        R.drawable.ic_cat_sports to "ic_cat_sports",
        R.drawable.ic_cat_entertainment to "ic_cat_entertainment",
        R.drawable.ic_cat_communication to "ic_cat_communication",
        R.drawable.ic_cat_clothing to "ic_cat_clothing",
        R.drawable.ic_cat_social to "ic_cat_social",
        R.drawable.ic_cat_medical to "ic_cat_medical",
        R.drawable.ic_cat_books to "ic_cat_books",
        R.drawable.ic_cat_delivery to "ic_cat_delivery",
        R.drawable.ic_cat_internet to "ic_cat_internet",
        R.drawable.ic_cat_game to "ic_cat_game",
        R.drawable.ic_cat_study to "ic_cat_study",
        R.drawable.ic_cat_salary to "ic_cat_salary",
        R.drawable.ic_cat_bonus to "ic_cat_bonus",
        R.drawable.ic_cat_investment to "ic_cat_investment",
        R.drawable.ic_cat_parttime to "ic_cat_parttime",
        R.drawable.ic_cat_other to "ic_cat_other"
    )

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
        val rvIcons: RecyclerView = dialogView.findViewById(R.id.rv_icons)

        val selectedIconHolder = intArrayOf(0)
        val iconAdapter = IconAdapter(allIcons) { iconRes ->
            selectedIconHolder[0] = iconRes
        }
        selectedIconHolder[0] = allIcons[0]
        rvIcons.layoutManager = GridLayoutManager(this, 6)
        rvIcons.adapter = iconAdapter

        AlertDialog.Builder(this)
            .setTitle("添加分类")
            .setView(dialogView)
            .setPositiveButton("添加") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val icon = iconResToName[selectedIconHolder[0]] ?: "ic_other"
                    val category = Category(
                        name = name,
                        icon = icon,
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
        val rvIcons: RecyclerView = dialogView.findViewById(R.id.rv_icons)
        etName.setText(category.name)

        val currentIconIndex = allIcons.indexOfFirst {
            iconResToName[it] == category.icon
        }.coerceAtLeast(0)

        val selectedIconHolder = intArrayOf(allIcons[currentIconIndex])
        val iconAdapter = IconAdapter(allIcons) { iconRes ->
            selectedIconHolder[0] = iconRes
        }
        rvIcons.layoutManager = GridLayoutManager(this, 6)
        rvIcons.adapter = iconAdapter
        iconAdapter.setInitialSelection(currentIconIndex)

        AlertDialog.Builder(this)
            .setTitle("编辑分类")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val icon = iconResToName[selectedIconHolder[0]] ?: "ic_other"
                    val updatedCategory = category.copy(name = name, icon = icon)
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