package com.example.accountbook.ui.view

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.accountbook.R
import java.util.Calendar

class WheelDatePickerDialog(
    private val onDateSelected: (year: Int, month: Int, day: Int) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 32, 48, 32)
            gravity = android.view.Gravity.CENTER
        }

        val yearPicker = NumberPicker(requireContext()).apply {
            minValue = 2020
            maxValue = 2030
            value = currentYear
            wrapSelectorWheel = false
        }

        val monthPicker = NumberPicker(requireContext()).apply {
            minValue = 1
            maxValue = 12
            value = currentMonth + 1
            wrapSelectorWheel = true
            displayedValues = Array(12) { String.format("%02d", it + 1) + "月" }
        }

        val dayPicker = NumberPicker(requireContext()).apply {
            minValue = 1
            maxValue = 31
            value = currentDay
            wrapSelectorWheel = true
            displayedValues = Array(31) { String.format("%02d", it + 1) + "日" }
        }

        monthPicker.setOnValueChangedListener { _, _, newVal ->
            val maxDay = getMaxDay(yearPicker.value, newVal - 1)
            dayPicker.maxValue = maxDay
            if (dayPicker.value > maxDay) {
                dayPicker.value = maxDay
            }
        }

        yearPicker.setOnValueChangedListener { _, _, newVal ->
            val maxDay = getMaxDay(newVal, monthPicker.value - 1)
            dayPicker.maxValue = maxDay
            if (dayPicker.value > maxDay) {
                dayPicker.value = maxDay
            }
        }

        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER
        }

        row.addView(yearPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(monthPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(dayPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        layout.addView(row)

        return android.app.AlertDialog.Builder(requireContext())
            .setTitle("选择日期")
            .setView(layout)
            .setPositiveButton("确定") { _, _ ->
                onDateSelected(yearPicker.value, monthPicker.value - 1, dayPicker.value)
            }
            .setNegativeButton("取消", null)
            .create()
    }

    private fun getMaxDay(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}