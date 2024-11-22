package com.mymoney.android.popUpFragments.recordsFilterBottomSheet.adapter

import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterType
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.R
import com.mymoney.android.databinding.CheckboxItemBinding
import com.mymoney.android.databinding.ChipItemBinding
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterItem
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.SelectionType

class ChildFilterAdapter(
    private val filterType: FilterType,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val CHECKBOX_TYPE = 1
        const val CHIP_TYPE = 2
    }

    inner class ChildFilterViewHolder(private val binding: CheckboxItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(childFilter: FilterItem) {
            binding.titleTv.text = childFilter.name

            if (childFilter.isSelected) {
                binding.titleTv.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.green_500)
                )
                binding.checkImg.visibility = View.VISIBLE
            } else {
                binding.titleTv.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.primary_dark_color)
                )
                binding.checkImg.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                filterType.subFilters.forEach { it.isSelected = false }
                childFilter.isSelected = true
                itemClickListener.onCheckboxSelectionChanged(childFilter)
                notifyDataSetChanged()
            }

        }
    }

    inner class ChipViewHolder(private val binding: ChipItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(childFilter: FilterItem) {
            binding.tvFilterTitle.text = childFilter.name

            if (childFilter.isSelected) {
                binding.root.isSelected = true
                binding.tvFilterTitle.setTextColor(Color.WHITE)
                binding.root.setBackgroundResource(R.drawable.rounded_primary_button_bg)
                binding.trailingImage.visibility = View.VISIBLE
            } else {
                binding.root.isSelected = false
                binding.tvFilterTitle.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.green_500)
                )
                binding.root.setBackgroundResource(R.drawable.rounded_primary_stroke_bg)
                binding.trailingImage.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                childFilter.isSelected = !childFilter.isSelected
                itemClickListener.onCheckboxSelectionChanged(childFilter)
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (filterType.selectionType) {
            SelectionType.CHECKBOX_TYPE -> CHECKBOX_TYPE
            SelectionType.CHIP_TYPE -> CHIP_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CHECKBOX_TYPE -> {
                val binding = CheckboxItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChildFilterViewHolder(binding)
            }

            CHIP_TYPE -> {
                val binding =
                    ChipItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChipViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = filterType.subFilters.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val filterItem = filterType.subFilters[position]
        when (holder) {
            is ChildFilterViewHolder -> holder.bind(filterItem)
            is ChipViewHolder -> holder.bind(filterItem)
        }
    }

    interface ItemClickListener {
        fun onCheckboxSelectionChanged(filterItem: FilterItem)
    }
}
