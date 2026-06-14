package com.example.accountbook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.data.entity.Category
import com.example.accountbook.utils.CategoryIconHelper

class CategoryGridAdapter(
    private val onCategoryClick: (Category) -> Unit,
    private val onAddClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var categories = listOf<Category>()
    private var selectedPosition = -1
    private val TYPE_ITEM = 0
    private val TYPE_ADD = 1

    fun submitList(newList: List<Category>) {
        categories = newList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == categories.size) TYPE_ADD else TYPE_ITEM
    }

    override fun getItemCount(): Int = categories.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ADD -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_add, parent, false)
                AddViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_grid, parent, false)
                CategoryViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> {
                val category = categories[position]
                holder.bind(category, position == selectedPosition)
                holder.itemView.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = holder.adapterPosition
                    if (previousPosition != -1) {
                        notifyItemChanged(previousPosition)
                    }
                    notifyItemChanged(selectedPosition)
                    onCategoryClick(category)
                }
            }
            is AddViewHolder -> {
                holder.itemView.setOnClickListener { onAddClick() }
            }
        }
    }

    fun getSelectedCategory(): Category? {
        return if (selectedPosition != -1 && selectedPosition < categories.size) {
            categories[selectedPosition]
        } else null
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
        private val tvName: TextView = itemView.findViewById(R.id.tv_name)

        fun bind(category: Category, isSelected: Boolean) {
            ivIcon.setImageResource(CategoryIconHelper.getIconRes(category.icon.ifEmpty { category.name }))
            tvName.text = category.name
            ivIcon.setBackgroundResource(
                if (isSelected) R.drawable.bg_category_selected
                else R.drawable.bg_category_circle
            )
        }
    }

    class AddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}