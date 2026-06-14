package com.example.accountbook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.data.entity.Record
import com.example.accountbook.utils.CategoryIconHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class ListItem {
    data class DateHeader(val date: String, val dailyTotal: Double, val isExpense: Boolean) : ListItem()
    data class RecordItem(val record: Record) : ListItem()
}

class RecordAdapter(
    private val onItemClick: (Record) -> Unit,
    private val onItemLongClick: (Record) -> Unit
) : ListAdapter<ListItem, RecyclerView.ViewHolder>(ListItemDiffCallback()) {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_RECORD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ListItem.DateHeader -> TYPE_DATE_HEADER
            is ListItem.RecordItem -> TYPE_RECORD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_DATE_HEADER -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
                DateHeaderViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
                RecordViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ListItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item)
            is ListItem.RecordItem -> (holder as RecordViewHolder).bind(item.record, onItemClick, onItemLongClick)
        }
    }

    fun submitGroupedRecords(records: List<Record>) {
        val groupedItems = mutableListOf<ListItem>()
        val dateFormat = SimpleDateFormat("MM月dd日 EEEE", Locale.CHINESE)
        val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val grouped = records.groupBy { dayFormat.format(Date(it.date)) }

        grouped.forEach { (dateStr, dayRecords) ->
            val date = dayFormat.parse(dateStr)
            val dateHeader = dateFormat.format(date!!)
            val dailyTotal = dayRecords.filter { it.type == "expense" }.sumOf { it.amount }
            val isExpense = dailyTotal > 0

            groupedItems.add(ListItem.DateHeader(dateHeader, dailyTotal, isExpense))
            dayRecords.forEach { groupedItems.add(ListItem.RecordItem(it)) }
        }

        submitList(groupedItems)
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvDailyTotal: TextView = itemView.findViewById(R.id.tv_daily_total)

        fun bind(header: ListItem.DateHeader) {
            tvDate.text = header.date
            if (header.dailyTotal > 0) {
                tvDailyTotal.text = "支出：${String.format("%.1f", header.dailyTotal)}"
                tvDailyTotal.visibility = View.VISIBLE
            } else {
                tvDailyTotal.visibility = View.GONE
            }
        }
    }

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tv_category_name)
        private val tvNote: TextView = itemView.findViewById(R.id.tv_note)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)

        fun bind(record: Record, onItemClick: (Record) -> Unit, onItemLongClick: (Record) -> Unit) {
            ivIcon.setImageResource(CategoryIconHelper.getIconRes(record.categoryName))
            tvCategoryName.text = record.categoryName
            tvNote.text = record.note.ifEmpty { record.categoryName }

            val amountText = if (record.type == "expense") {
                "-${String.format("%.1f", record.amount)}"
            } else {
                "+${String.format("%.1f", record.amount)}"
            }
            tvAmount.text = amountText

            if (record.type == "expense") {
                tvAmount.setTextColor(0xFF333333.toInt())
            } else {
                tvAmount.setTextColor(0xFF4CAF50.toInt())
            }

            itemView.setOnClickListener { onItemClick(record) }
            itemView.setOnLongClickListener {
                onItemLongClick(record)
                true
            }
        }
    }

    class ListItemDiffCallback : DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return when {
                oldItem is ListItem.DateHeader && newItem is ListItem.DateHeader -> oldItem.date == newItem.date
                oldItem is ListItem.RecordItem && newItem is ListItem.RecordItem -> oldItem.record.id == newItem.record.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
            return oldItem == newItem
        }
    }
}