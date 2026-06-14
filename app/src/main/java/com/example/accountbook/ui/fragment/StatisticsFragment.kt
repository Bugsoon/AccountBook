package com.example.accountbook.ui.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.accountbook.R
import com.example.accountbook.ui.view.LineChartView
import com.example.accountbook.ui.view.PieChartView
import com.example.accountbook.ui.viewmodel.RecordViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatisticsFragment : Fragment() {
    private lateinit var viewModel: RecordViewModel
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RecordViewModel::class.java]

        setupMonthSelector(view)
        loadMonthData()
    }

    private fun setupMonthSelector(view: View) {
        val tvMonth: TextView = view.findViewById(R.id.tv_current_month)
        updateMonthDisplay(tvMonth)

        tvMonth.setOnClickListener {
            android.app.DatePickerDialog(
                requireContext(),
                { _, year, month, _ ->
                    currentYear = year
                    currentMonth = month
                    updateMonthDisplay(tvMonth)
                    loadMonthData()
                },
                currentYear,
                currentMonth,
                1
            ).show()
        }
    }

    private fun updateMonthDisplay(tvMonth: TextView) {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, currentMonth, 1)
        val monthFormat = SimpleDateFormat("yyyy年MM月", Locale.CHINESE)
        tvMonth.text = monthFormat.format(calendar.time)
    }

    private fun loadMonthData() {
        val view = view ?: return

        val calendar = Calendar.getInstance()
        calendar.set(currentYear, currentMonth, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis

        val tvExpense: TextView = view.findViewById(R.id.tv_month_expense)
        val tvIncome: TextView = view.findViewById(R.id.tv_month_income)
        val tvAvg: TextView = view.findViewById(R.id.tv_month_avg)
        val lineChart: LineChartView = view.findViewById(R.id.line_chart)
        val pieChart: PieChartView = view.findViewById(R.id.pie_chart)
        val llLegend: LinearLayout = view.findViewById(R.id.ll_legend)
        val llRankList: LinearLayout = view.findViewById(R.id.ll_rank_list)

        viewModel.getTotalByTypeAndDateRange("expense", startDate, endDate)
            .observe(viewLifecycleOwner) { total ->
                val expense = total ?: 0.0
                tvExpense.text = String.format("%.2f", expense)
                val avg = if (daysInMonth > 0) expense / daysInMonth else 0.0
                tvAvg.text = String.format("%.2f", avg)
            }

        viewModel.getTotalByTypeAndDateRange("income", startDate, endDate)
            .observe(viewLifecycleOwner) { total ->
                tvIncome.text = String.format("%.2f", total ?: 0.0)
            }

        loadLineChartData(view, startDate, daysInMonth)
        loadPieChartData(view, startDate, endDate, llLegend)
        loadRankData(view, startDate, endDate, llRankList)
    }

    private fun loadLineChartData(view: View, startDate: Long, daysInMonth: Int) {
        val lineChart: LineChartView = view.findViewById(R.id.line_chart)
        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val points = mutableListOf<Float>()
        val labels = mutableListOf<String>()

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startDate

        for (day in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val dayStart = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val dayEnd = calendar.timeInMillis

            points.add(0f)
            labels.add(day.toString())
        }

        viewModel.getRecordsByDateRange(startDate, calendar.timeInMillis)
            .observe(viewLifecycleOwner) { records ->
                val dailyTotals = FloatArray(daysInMonth) { 0f }

                records.filter { it.type == "expense" }.forEach { record ->
                    val recordCalendar = Calendar.getInstance()
                    recordCalendar.timeInMillis = record.date
                    val dayOfMonth = recordCalendar.get(Calendar.DAY_OF_MONTH) - 1
                    if (dayOfMonth in 0 until daysInMonth) {
                        dailyTotals[dayOfMonth] += record.amount.toFloat()
                    }
                }

                lineChart.setData(dailyTotals.toList(), labels)
            }
    }

    private fun loadPieChartData(view: View, startDate: Long, endDate: Long, llLegend: LinearLayout) {
        val pieChart: PieChartView = view.findViewById(R.id.pie_chart)

        val colors = intArrayOf(
            Color.parseColor("#FFA000"),
            Color.parseColor("#FF7043"),
            Color.parseColor("#42A5F5"),
            Color.parseColor("#66BB6A"),
            Color.parseColor("#AB47BC"),
            Color.parseColor("#EF5350"),
            Color.parseColor("#26C6DA"),
            Color.parseColor("#FFCA28")
        )

        viewModel.getCategoryTotals("expense", startDate, endDate)
            .observe(viewLifecycleOwner) { totals ->
                llLegend.removeAllViews()

                if (totals.isNullOrEmpty()) {
                    pieChart.setData(emptyList())
                    return@observe
                }

                val totalAmount = totals.sumOf { it.total }
                val pieData = totals.map { Pair(it.categoryName, it.total.toFloat()) }
                pieChart.setData(pieData)

                totals.forEachIndexed { index, item ->
                    val legendItem = LayoutInflater.from(context)
                        .inflate(R.layout.item_legend, llLegend, false)

                    val colorView = legendItem.findViewById<View>(R.id.view_color)
                    val tvName = legendItem.findViewById<TextView>(R.id.tv_legend_name)
                    val tvPercent = legendItem.findViewById<TextView>(R.id.tv_legend_percent)

                    colorView.setBackgroundColor(colors[index % colors.size])
                    tvName.text = item.categoryName
                    tvPercent.text = String.format("%.1f%%", (item.total / totalAmount * 100))

                    llLegend.addView(legendItem)
                }
            }
    }

    private fun loadRankData(view: View, startDate: Long, endDate: Long, llRankList: LinearLayout) {
        val emojis = mapOf(
            "餐饮" to "\uD83C\uDF7D",
            "交通" to "\uD83D\uDE8C",
            "购物" to "\uD83D\uDED2",
            "娱乐" to "\uD83C\uDFAE",
            "居住" to "\uD83C\uDFE0",
            "医疗" to "\uD83C\uDFE5",
            "教育" to "\uD83C\uDF93",
            "通讯" to "\uD83D\uDCF1",
            "日用" to "\uD83D\uDCC1",
            "零食" to "\uD83C\uDF6A",
            "学习" to "\uD83C\uDF93"
        )

        viewModel.getCategoryTotals("expense", startDate, endDate)
            .observe(viewLifecycleOwner) { totals ->
                llRankList.removeAllViews()

                if (totals.isNullOrEmpty()) return@observe

                val totalAmount = totals.sumOf { it.total }
                val maxAmount = totals.maxOfOrNull { it.total } ?: 1.0

                totals.forEach { item ->
                    val rankItem = LayoutInflater.from(context)
                        .inflate(R.layout.item_rank, llRankList, false)

                    val tvEmoji = rankItem.findViewById<TextView>(R.id.tv_rank_emoji)
                    val tvName = rankItem.findViewById<TextView>(R.id.tv_rank_name)
                    val tvPercent = rankItem.findViewById<TextView>(R.id.tv_rank_percent)
                    val tvAmount = rankItem.findViewById<TextView>(R.id.tv_rank_amount)
                    val progress = rankItem.findViewById<ProgressBar>(R.id.progress_rank)

                    tvEmoji.text = emojis.getOrElse(item.categoryName) { "\uD83D\uDCB1" }
                    tvName.text = item.categoryName
                    tvPercent.text = String.format("%.1f%%", (item.total / totalAmount * 100))
                    tvAmount.text = String.format("%.0f", item.total)
                    progress.progress = ((item.total / maxAmount) * 100).toInt()

                    llRankList.addView(rankItem)
                }
            }
    }
}