package com.mymoney.android.popUpFragments.categoriesBottomSheet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.ImageTitleVerticalItemBinding
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.utils.CategoryIcon

class SelectCategoriesAdapter(
    private val categories: List<Category>,
    private val onItemClick: (category: Category) -> Unit
) : RecyclerView.Adapter<SelectCategoriesAdapter.SelectCategoriesViewHolder>() {

    class SelectCategoriesViewHolder(
        private val binding: ImageTitleVerticalItemBinding,
        private val onItemClick: (category: Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.titleTv.text = category.name
            val context = binding.root.context
            val categoryIcon = try {
                CategoryIcon.valueOf(category.icon)
            } catch (e: IllegalArgumentException) {
                CategoryIcon.SALE
            }
            binding.leadingImg.setImageDrawable(
                ContextCompat.getDrawable(context, categoryIcon.drawableId)
            )
            binding.root.setOnClickListener { onItemClick(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectCategoriesViewHolder {
        val binding = ImageTitleVerticalItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SelectCategoriesViewHolder(binding, onItemClick)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: SelectCategoriesViewHolder, position: Int) {
        holder.bind(categories[position])
    }
}
