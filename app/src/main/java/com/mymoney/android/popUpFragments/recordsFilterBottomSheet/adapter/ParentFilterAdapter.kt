package com.mymoney.android.popUpFragments.recordsFilterBottomSheet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.VerticalTabItemBinding
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterType

class ParentFilterAdapter(
    private val parentFilterList: List<FilterType>,
    private val onFilterClick: (FilterType) -> Unit
) : RecyclerView.Adapter<ParentFilterAdapter.ParentFilterViewHolder>() {

    private var selectedPosition: Int = 0

    inner class ParentFilterViewHolder(private val binding: VerticalTabItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tab: FilterType, position: Int) {
            binding.title.text = tab.title

            binding.indicator.visibility = if (selectedPosition == position) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

            binding.root.setOnClickListener {
                selectedPosition = position
                onFilterClick(tab)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentFilterViewHolder {
        val binding =
            VerticalTabItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ParentFilterViewHolder(binding)
    }

    override fun getItemCount(): Int = parentFilterList.size

    override fun onBindViewHolder(holder: ParentFilterViewHolder, position: Int) {
        holder.bind(parentFilterList[position], position)
    }
}
