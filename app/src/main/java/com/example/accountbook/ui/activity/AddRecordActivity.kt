package com.example.accountbook.ui.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.data.entity.Category
import com.example.accountbook.data.entity.Record
import com.example.accountbook.ui.adapter.CategoryGridAdapter
import com.example.accountbook.ui.adapter.IconPickerAdapter
import com.example.accountbook.ui.view.WheelDatePickerDialog
import com.example.accountbook.ui.viewmodel.CategoryViewModel
import com.example.accountbook.ui.viewmodel.RecordViewModel
import com.example.accountbook.utils.CategoryIconHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddRecordActivity : AppCompatActivity() {
    private lateinit var recordViewModel: RecordViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryGridAdapter

    private var selectedType = "expense"
    private var selectedCategory: Category? = null
    private var selectedDate = Calendar.getInstance()
    private var editingRecordId: Long = -1
    private var amountStr = "0"
    private var expression = ""
    private var lastResult = 0.0
    private var hasDecimal = false

    private lateinit var tvAmountDisplay: TextView
    private lateinit var tvNote: TextView
    private lateinit var tvTabExpense: TextView
    private lateinit var tvTabIncome: TextView
    private lateinit var tvSelectedCategory: TextView
    private lateinit var ivSelectedIcon: ImageView
    private lateinit var llKeyboardArea: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)

        recordViewModel = ViewModelProvider(this)[RecordViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        tvAmountDisplay = findViewById(R.id.tv_amount_display)
        tvNote = findViewById(R.id.tv_note)
        tvTabExpense = findViewById(R.id.tab_expense)
        tvTabIncome = findViewById(R.id.tab_income)
        tvSelectedCategory = findViewById(R.id.tv_selected_category)
        ivSelectedIcon = findViewById(R.id.iv_selected_icon)
        llKeyboardArea = findViewById(R.id.ll_keyboard_area)

        setupTabs()
        setupCategoryGrid()
        setupKeyboard()
        setupNoteInput()
        setupCancel()
        updateDateDisplay()
    }

    private fun setupTabs() {
        tvTabExpense.setOnClickListener {
            selectedType = "expense"
            updateTabStyles()
            loadCategories()
        }

        tvTabIncome.setOnClickListener {
            selectedType = "income"
            updateTabStyles()
            loadCategories()
        }

        tvTabExpense.performClick()
    }

    private fun updateTabStyles() {
        if (selectedType == "expense") {
            tvTabExpense.setTextColor(Color.WHITE)
            tvTabExpense.typeface = Typeface.DEFAULT_BOLD
            tvTabExpense.setBackgroundResource(R.drawable.bg_tab_selected)
            tvTabIncome.setTextColor(0xCCFFFFFF.toInt())
            tvTabIncome.typeface = Typeface.DEFAULT
            tvTabIncome.setBackgroundResource(0)
        } else {
            tvTabIncome.setTextColor(Color.WHITE)
            tvTabIncome.typeface = Typeface.DEFAULT_BOLD
            tvTabIncome.setBackgroundResource(R.drawable.bg_tab_selected)
            tvTabExpense.setTextColor(0xCCFFFFFF.toInt())
            tvTabExpense.typeface = Typeface.DEFAULT
            tvTabExpense.setBackgroundResource(0)
        }
    }

    private fun setupCategoryGrid() {
        val rvCategories = findViewById<RecyclerView>(R.id.rv_categories)
        categoryAdapter = CategoryGridAdapter(
            onCategoryClick = { category ->
                onCategorySelected(category)
            },
            onAddClick = {
                showAddCategoryDialog()
            }
        )
        rvCategories.layoutManager = GridLayoutManager(this, 4)
        rvCategories.adapter = categoryAdapter
        loadCategories()
    }

    private fun onCategorySelected(category: Category) {
        selectedCategory = category
        ivSelectedIcon.setImageResource(CategoryIconHelper.getIconRes(category.name))
        tvSelectedCategory.text = category.name

        amountStr = "0"
        expression = ""
        lastResult = 0.0
        hasDecimal = false
        updateAmountDisplay()

        llKeyboardArea.visibility = View.VISIBLE
    }

    private fun loadCategories() {
        categoryViewModel.getCategoriesByType(selectedType).observe(this) { categories ->
            categoryAdapter.submitList(categories)
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_custom_category, null)
        val etName = dialogView.findViewById<EditText>(R.id.et_category_name)
        val ivSelectedIcon = dialogView.findViewById<ImageView>(R.id.iv_selected_icon)
        val rvIconPicker = dialogView.findViewById<RecyclerView>(R.id.rv_icon_picker)

        val iconList = listOf(
            R.drawable.ic_cat_food, R.drawable.ic_cat_shopping, R.drawable.ic_cat_daily,
            R.drawable.ic_cat_transport, R.drawable.ic_cat_fruit, R.drawable.ic_cat_snacks,
            R.drawable.ic_cat_sports, R.drawable.ic_cat_entertainment, R.drawable.ic_cat_communication,
            R.drawable.ic_cat_clothing, R.drawable.ic_cat_social, R.drawable.ic_cat_medical,
            R.drawable.ic_cat_books, R.drawable.ic_cat_delivery, R.drawable.ic_cat_internet,
            R.drawable.ic_cat_game, R.drawable.ic_cat_study, R.drawable.ic_cat_salary,
            R.drawable.ic_cat_bonus, R.drawable.ic_cat_investment, R.drawable.ic_cat_parttime,
            R.drawable.ic_cat_other
        )

        var selectedIconRes = iconList[0]
        ivSelectedIcon.setImageResource(selectedIconRes)

        rvIconPicker.layoutManager = GridLayoutManager(this, 7)
        rvIconPicker.adapter = IconPickerAdapter(iconList) { iconRes ->
            selectedIconRes = iconRes
            ivSelectedIcon.setImageResource(iconRes)
        }

        AlertDialog.Builder(this)
            .setTitle("添加自定义分类")
            .setView(dialogView)
            .setPositiveButton("添加") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val iconResName = resources.getResourceEntryName(selectedIconRes)
                    val category = Category(
                        name = name,
                        icon = iconResName,
                        type = selectedType,
                        isDefault = false
                    )
                    categoryViewModel.insert(category)
                    Toast.makeText(this, "分类已添加", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "请输入分类名称", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun setupKeyboard() {
        val buttons = mapOf(
            R.id.btn_0 to "0", R.id.btn_1 to "1", R.id.btn_2 to "2",
            R.id.btn_3 to "3", R.id.btn_4 to "4", R.id.btn_5 to "5",
            R.id.btn_6 to "6", R.id.btn_7 to "7", R.id.btn_8 to "8",
            R.id.btn_9 to "9", R.id.btn_dot to "."
        )

        buttons.forEach { (id, value) ->
            findViewById<TextView>(id).setOnClickListener { onNumberInput(value) }
        }

        findViewById<TextView>(R.id.btn_plus).setOnClickListener { onOperator("+") }
        findViewById<TextView>(R.id.btn_minus).setOnClickListener { onOperator("-") }
        findViewById<TextView>(R.id.btn_delete).setOnClickListener { onDelete() }
        findViewById<TextView>(R.id.btn_confirm).setOnClickListener { onConfirm() }

        findViewById<TextView>(R.id.btn_today).setOnClickListener {
            WheelDatePickerDialog { year, month, day ->
                selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)
                updateDateDisplay()
            }.show(supportFragmentManager, "date_picker")
        }
    }

    private fun updateDateDisplay() {
        val dateFormat = SimpleDateFormat("MM月dd日", Locale.CHINESE)
        findViewById<TextView>(R.id.btn_today).text = "\uD83D\uDCC5 ${dateFormat.format(selectedDate.time)}"
    }

    private fun onNumberInput(number: String) {
        if (amountStr == "0" && number != ".") {
            amountStr = number
        } else if (number == ".") {
            if (!hasDecimal) {
                amountStr += "."
                hasDecimal = true
            }
        } else {
            amountStr += number
        }
        updateAmountDisplay()
    }

    private fun onOperator(operator: String) {
        val currentAmount = amountStr.toDoubleOrNull() ?: 0.0
        if (expression.isEmpty()) {
            expression = "$amountStr $operator "
            lastResult = currentAmount
        } else {
            lastResult = calculateExpression(expression + amountStr)
            expression = "$lastResult $operator "
        }
        amountStr = "0"
        hasDecimal = false
        updateAmountDisplay()
    }

    private fun onDelete() {
        if (amountStr.length > 1) {
            amountStr = amountStr.dropLast(1)
            if (!amountStr.contains(".")) hasDecimal = false
        } else {
            amountStr = "0"
            hasDecimal = false
        }
        updateAmountDisplay()
    }

    private fun calculateExpression(expr: String): Double {
        return try {
            val parts = expr.trim().split(" ")
            if (parts.size < 3) return parts[0].toDoubleOrNull() ?: 0.0
            var result = parts[0].toDouble()
            var i = 1
            while (i < parts.size - 1) {
                val op = parts[i]
                val num = parts[i + 1].toDouble()
                when (op) {
                    "+" -> result += num
                    "-" -> result -= num
                }
                i += 2
            }
            result
        } catch (e: Exception) { 0.0 }
    }

    private fun updateAmountDisplay() {
        val display = if (expression.isNotEmpty()) {
            val result = calculateExpression(expression + amountStr)
            String.format("%.2f", result)
        } else {
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            String.format("%.2f", amount)
        }
        tvAmountDisplay.text = display
    }

    private fun setupNoteInput() {
        tvNote.setOnClickListener {
            val input = EditText(this).apply {
                hint = "输入备注"
                setPadding(48, 32, 48, 32)
            }
            AlertDialog.Builder(this)
                .setTitle("添加备注")
                .setView(input)
                .setPositiveButton("确定") { _, _ ->
                    val note = input.text.toString().trim()
                    tvNote.text = if (note.isNotEmpty()) "备注：$note" else "备注：点击填写备注"
                    tvNote.setTextColor(if (note.isNotEmpty()) Color.parseColor("#333333") else Color.parseColor("#CCCCCC"))
                }
                .setNegativeButton("取消", null)
                .show()
        }
    }

    private fun setupCancel() {
        findViewById<TextView>(R.id.btn_cancel).setOnClickListener {
            if (llKeyboardArea.visibility == View.VISIBLE) {
                llKeyboardArea.visibility = View.GONE
                selectedCategory = null
            } else {
                finish()
            }
        }
    }

    private fun onConfirm() {
        val finalAmount = if (expression.isNotEmpty()) {
            calculateExpression(expression + amountStr)
        } else {
            amountStr.toDoubleOrNull() ?: 0.0
        }

        if (finalAmount <= 0) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedCategory == null) {
            Toast.makeText(this, "请选择分类", Toast.LENGTH_SHORT).show()
            return
        }

        val noteText = tvNote.text.toString()
        val note = if (noteText.startsWith("备注：")) noteText.removePrefix("备注：") else ""

        val record = Record(
            id = if (editingRecordId != -1L) editingRecordId else 0,
            amount = finalAmount,
            type = selectedType,
            categoryId = selectedCategory!!.id,
            categoryName = selectedCategory!!.name,
            note = note,
            date = selectedDate.timeInMillis
        )

        if (editingRecordId != -1L) {
            recordViewModel.update(record)
            Toast.makeText(this, "记录已更新", Toast.LENGTH_SHORT).show()
        } else {
            recordViewModel.insert(record)
            Toast.makeText(this, "记录已保存", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}