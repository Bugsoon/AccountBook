package com.example.accountbook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.data.entity.AutoRecordTemplate
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial

class TemplateAdapter(
    private val onItemClick: (AutoRecordTemplate) -> Unit,
    private val onToggleActive: (AutoRecordTemplate, Boolean) -> Unit
) : ListAdapter<AutoRecordTemplate, TemplateAdapter.TemplateViewHolder>(TemplateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_template, parent, false)
        return TemplateViewHolder(view)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val template = getItem(position)
        holder.bind(template)
        holder.itemView.setOnClickListener { onItemClick(template) }
        holder.switchActive.setOnCheckedChangeListener { _, isChecked ->
            onToggleActive(template, isChecked)
        }
    }

    class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvFrequency: TextView = itemView.findViewById(R.id.tv_frequency)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val switchActive: SwitchMaterial = itemView.findViewById(R.id.switch_active)

        fun bind(template: AutoRecordTemplate) {
            tvName.text = template.name
            tvCategory.text = template.categoryName
            tvFrequency.text = getFrequencyText(template.frequency)
            
            val amountText = if (template.type == "expense") {
                "-¥${String.format("%.2f", template.amount)}"
            } else {
                "+¥${String.format("%.2f", template.amount)}"
            }
            tvAmount.text = amountText
            tvAmount.setTextColor(
                if (template.type == "expense") 
                    itemView.context.getColor(android.R.color.holo_red_dark)
                else 
                    itemView.context.getColor(android.R.color.holo_green_dark)
            )

            switchActive.isChecked = template.isActive
        }

        private fun getFrequencyText(frequency: String): String {
            return when (frequency) {
                "daily" -> "每天"
                "weekly" -> "每周"
                "monthly" -> "每月"
                "yearly" -> "每年"
                else -> frequency
            }
        }
    }

    class TemplateDiffCallback : DiffUtil.ItemCallback<AutoRecordTemplate>() {
        override fun areItemsTheSame(oldItem: AutoRecordTemplate, newItem: AutoRecordTemplate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AutoRecordTemplate, newItem: AutoRecordTemplate): Boolean {
            return oldItem == newItem
        }
    }
}