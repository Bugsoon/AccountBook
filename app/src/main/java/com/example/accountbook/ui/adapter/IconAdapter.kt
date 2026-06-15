package com.example.accountbook.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R

class IconAdapter(
    private val icons: List<Int>,
    private val onIconSelected: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedIndex = 0

    class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_icon_selectable, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        holder.ivIcon.setImageResource(icons[position])

        val isSelected = position == selectedIndex
        holder.itemView.setBackgroundResource(
            if (isSelected) R.drawable.icon_selection_bg_selected
            else R.drawable.icon_selection_bg
        )

        holder.itemView.setOnClickListener {
            val oldIndex = selectedIndex
            selectedIndex = position
            notifyItemChanged(oldIndex)
            notifyItemChanged(position)
            onIconSelected(icons[position])
        }
    }

    override fun getItemCount(): Int = icons.size

    fun getSelectedIconRes(): Int = icons[selectedIndex]

    fun setInitialSelection(index: Int) {
        selectedIndex = index
        notifyDataSetChanged()
    }
}
