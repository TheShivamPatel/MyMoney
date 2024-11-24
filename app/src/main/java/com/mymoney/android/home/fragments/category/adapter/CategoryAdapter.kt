package com.mymoney.android.home.fragments.category.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.LeadingIconTitleTrailingIconItemBinding
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.utils.CategoryIcon

class CategoryAdapter(
    private val context: Context,
    private val categoryList: List<Category>,
    private val listener: OnItemClick
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: LeadingIconTitleTrailingIconItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.tvTitle.text = category.name
            val categoryIcon = try {
                CategoryIcon.valueOf(category.icon)
            } catch (e: IllegalArgumentException) {
                CategoryIcon.SALE
            }
            binding.leadingImg.setImageDrawable(
                ContextCompat.getDrawable(context, categoryIcon.drawableId)
            )
            binding.trailingImg.setOnClickListener { listener.onCategoryClicked(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = LeadingIconTitleTrailingIconItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.bind(category)
    }

    override fun getItemCount() = categoryList.size

    interface OnItemClick {
        fun onCategoryClicked(category: Category)
    }

}