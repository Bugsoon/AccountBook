package com.example.accountbook.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.accountbook.R
import com.example.accountbook.data.entity.Budget
import com.example.accountbook.ui.viewmodel.BudgetViewModel
import com.example.accountbook.ui.viewmodel.RecordViewModel
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BudgetFragment : Fragment() {
    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var recordViewModel: RecordViewModel

    private lateinit var tvBudgetAmount: TextView
    private lateinit var tvBudgetUsed: TextView
    private lateinit var tvBudgetRemaining: TextView
    private lateinit var progressBudget: ProgressBar

    private var currentMonthBudget: Budget? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        budgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]
        recordViewModel = ViewModelProvider(this)[RecordViewModel::class.java]

        tvBudgetAmount = view.findViewById(R.id.tv_budget_amount)
        tvBudgetUsed = view.findViewById(R.id.tv_budget_used)
        tvBudgetRemaining = view.findViewById(R.id.tv_budget_remaining)
        progressBudget = view.findViewById(R.id.progress_budget)

        val btnSetBudget: MaterialButton = view.findViewById(R.id.btn_set_budget)
        btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        observeBudget()
        observeExpense()
    }

    private fun observeBudget() {
        val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val currentMonth = monthFormat.format(Calendar.getInstance().time)

        budgetViewModel.getBudgetByMonth(currentMonth).observe(viewLifecycleOwner) { budget ->
            currentMonthBudget = budget
            if (budget != null) {
                tvBudgetAmount.text = "¥${String.format("%.2f", budget.amount)}"
                updateBudgetProgress()
            } else {
                tvBudgetAmount.text = "¥0.00"
                tvBudgetUsed.text = "¥0.00"
                tvBudgetRemaining.text = "¥0.00"
                progressBudget.progress = 0
            }
        }
    }

    private fun observeExpense() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis

        recordViewModel.getTotalByTypeAndDateRange("expense", startDate, endDate)
            .observe(viewLifecycleOwner) { total ->
                val expense = total ?: 0.0
                tvBudgetUsed.text = "¥${String.format("%.2f", expense)}"
                updateBudgetProgress()
            }
    }

    private fun updateBudgetProgress() {
        val budgetAmount = currentMonthBudget?.amount ?: 0.0
        if (budgetAmount > 0) {
            val usedText = tvBudgetUsed.text.toString().replace("¥", "")
            val usedAmount = usedText.toDoubleOrNull() ?: 0.0
            val remaining = budgetAmount - usedAmount
            val progress = ((usedAmount / budgetAmount) * 100).toInt().coerceIn(0, 100)

            tvBudgetRemaining.text = "¥${String.format("%.2f", remaining)}"
            progressBudget.progress = progress
        } else {
            tvBudgetRemaining.text = "¥0.00"
            progressBudget.progress = 0
        }
    }

    private fun showSetBudgetDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_budget, null)
        val etBudget: EditText = dialogView.findViewById(R.id.et_budget)

        currentMonthBudget?.let {
            etBudget.setText(it.amount.toString())
        }

        AlertDialog.Builder(requireContext())
            .setTitle("设置月预算")
            .setView(dialogView)
            .setPositiveButton("保存") { _, _ ->
                val amountStr = etBudget.text.toString().trim()
                val amount = amountStr.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    val monthFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
                    val currentMonth = monthFormat.format(Calendar.getInstance().time)

                    val budget = Budget(
                        id = currentMonthBudget?.id ?: 0,
                        amount = amount,
                        month = currentMonth
                    )
                    budgetViewModel.insert(budget)
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}