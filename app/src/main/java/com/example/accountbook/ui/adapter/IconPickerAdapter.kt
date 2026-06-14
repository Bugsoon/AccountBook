package com.example.accountbook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R

class IconPickerAdapter(
    private val icons: List<Int>,
    private val onIconClick: (Int) -> Unit
) : RecyclerView.Adapter<IconPickerAdapter.IconViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon_picker, parent, false)
        return IconViewHolder(view)
    }

    override fun getItemCount(): Int = icons.size

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconRes = icons[position]
        holder.bind(iconRes, position == selectedPosition)
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            if (previousPosition != -1) {
                notifyItemChanged(previousPosition)
            }
            notifyItemChanged(selectedPosition)
            onIconClick(iconRes)
        }
    }

    class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)

        fun bind(iconRes: Int, isSelected: Boolean) {
            ivIcon.setImageResource(iconRes)
            ivIcon.setBackgroundResource(
                if (isSelected) R.drawable.bg_category_selected
                else R.drawable.bg_category_circle
            )
        }
    }
}