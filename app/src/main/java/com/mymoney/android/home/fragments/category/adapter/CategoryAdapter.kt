package com.mymoney.android.home.fragments.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.LeadingIconTitleTrailingIconItemBinding
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.utils.CategoryIcon

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val listener: OnItemClick
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: LeadingIconTitleTrailingIconItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.tvTitle.text = category.name
            binding.leadingImg.setImageResource(CategoryIcon.valueOf(category.icon).drawableId)
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