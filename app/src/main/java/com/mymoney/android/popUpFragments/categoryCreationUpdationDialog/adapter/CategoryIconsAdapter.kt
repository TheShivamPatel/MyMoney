package com.mymoney.android.popUpFragments.categoryCreationUpdationDialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.R
import com.mymoney.android.databinding.SelectableIconItemBinding
import com.mymoney.android.utils.CategoryIcon

class CategoryIconsAdapter(
    private val icons: List<CategoryIcon>,
    private val onItemClick: (CategoryIcon) -> Unit
) : RecyclerView.Adapter<CategoryIconsAdapter.CategoryIconsViewHolder>() {

    private var selectedPosition: Int = -1

    inner class CategoryIconsViewHolder(private val binding: SelectableIconItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categoryIcon: CategoryIcon, isSelected: Boolean){

            binding.iconImg.setImageResource(categoryIcon.drawableId)
            binding.root.background = if (isSelected) {
                ContextCompat.getDrawable(binding.root.context, R.drawable.rounded_primary_stroke_bg)
            } else {
                null
            }

            binding.root.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onItemClick(categoryIcon)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryIconsViewHolder {
        val view = SelectableIconItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryIconsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryIconsViewHolder, position: Int) {
       holder.bind(icons[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = icons.size

    fun setSelectedIcon(iconName: String) {
        selectedPosition = icons.indexOfFirst { it.name == iconName }
        notifyItemChanged(selectedPosition)
    }

}
