package com.example.accountbook.ui.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.ui.activity.AddRecordActivity
import com.example.accountbook.ui.activity.AutoRecordActivity
import com.example.accountbook.ui.activity.ManageCategoriesActivity
import com.example.accountbook.ui.adapter.RecordAdapter
import com.example.accountbook.ui.viewmodel.RecordViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var viewModel: RecordViewModel
    private lateinit var recordAdapter: RecordAdapter
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RecordViewModel::class.java]

        setupHeader(view)
        setupQuickActions(view)
        setupRecyclerView(view)
        loadMonthData()
    }

    private fun setupHeader(view: View) {
        updateHeaderDisplay(view)

        val tvMonth: TextView = view.findViewById(R.id.tv_month)
        tvMonth.setOnClickListener {
            showMonthPicker(view)
        }

        val tvYear: TextView = view.findViewById(R.id.tv_year)
        tvYear.setOnClickListener {
            showMonthPicker(view)
        }
    }

    private fun showMonthPicker(view: View) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                currentYear = year
                currentMonth = month
                updateHeaderDisplay(view)
                loadMonthData()
            },
            currentYear,
            currentMonth,
            1
        ).show()
    }

    private fun updateHeaderDisplay(view: View) {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, currentMonth, 1)
        val yearFormat = SimpleDateFormat("yyyy年", Locale.CHINESE)
        val monthFormat = SimpleDateFormat("MM月", Locale.CHINESE)

        view.findViewById<TextView>(R.id.tv_year).text = yearFormat.format(calendar.time)
        view.findViewById<TextView>(R.id.tv_month).text = monthFormat.format(calendar.time)
    }

    private fun setupQuickActions(view: View) {
        view.findViewById<LinearLayout>(R.id.item_bill).setOnClickListener {
            // TODO: Navigate to bill list
        }
        view.findViewById<LinearLayout>(R.id.item_budget).setOnClickListener {
            (activity as? com.example.accountbook.MainActivity)?.switchToBudget()
        }
        view.findViewById<LinearLayout>(R.id.item_category).setOnClickListener {
            val intent = Intent(requireContext(), ManageCategoriesActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.item_auto).setOnClickListener {
            val intent = Intent(requireContext(), AutoRecordActivity::class.java)
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.item_more).setOnClickListener {
            // TODO: More options
        }
    }

    private fun setupRecyclerView(view: View) {
        val rvRecords: RecyclerView = view.findViewById(R.id.rv_records)
        recordAdapter = RecordAdapter(
            onItemClick = { record ->
                val intent = Intent(requireContext(), AddRecordActivity::class.java)
                intent.putExtra("record_id", record.id)
                startActivity(intent)
            },
            onItemLongClick = { record ->
                AlertDialog.Builder(requireContext())
                    .setTitle("删除记录")
                    .setMessage("确定要删除这条记录吗？")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.delete(record)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        )
        rvRecords.layoutManager = LinearLayoutManager(requireContext())
        rvRecords.adapter = recordAdapter
    }

    private fun loadMonthData() {
        val calendar = Calendar.getInstance()
        calendar.set(currentYear, currentMonth, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val endDate = calendar.timeInMillis

        viewModel.getRecordsByDateRange(startDate, endDate).observe(viewLifecycleOwner) { records ->
            recordAdapter.submitGroupedRecords(records)
            view?.findViewById<TextView>(R.id.tv_no_records)?.visibility =
                if (records.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.getTotalByTypeAndDateRange("expense", startDate, endDate)
            .observe(viewLifecycleOwner) { total ->
                view?.findViewById<TextView>(R.id.tv_total_expense)?.text =
                    String.format("%.2f", total ?: 0.0)
            }

        viewModel.getTotalByTypeAndDateRange("income", startDate, endDate)
            .observe(viewLifecycleOwner) { total ->
                view?.findViewById<TextView>(R.id.tv_total_income)?.text =
                    String.format("%.2f", total ?: 0.0)
            }
    }
}