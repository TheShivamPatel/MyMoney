package com.mymoney.android.home.fragments.analysis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.RecordAnalysisItemBinding
import com.mymoney.android.roomDB.data.CategoryExpensePercentage
import com.mymoney.android.utils.CategoryIcon

class RecordsAnalysisAdapter(private val recordsList: List<CategoryExpensePercentage>) :
    RecyclerView.Adapter<RecordsAnalysisAdapter.RecordsAnalysisViewHolder>() {

    inner class RecordsAnalysisViewHolder(private val binding: RecordAnalysisItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: CategoryExpensePercentage) {
            binding.titleTv.text = record.categoryName
            binding.subtitleTv.text = "-₹${record.totalAmount}"
            binding.tvTrailing.text = "${record.percentage}%"
            binding.indicator.progress = record.percentage.toInt()
            binding.imageView2.setImageResource(CategoryIcon.valueOf(record.categoryIcon!!).drawableId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsAnalysisViewHolder {
        val binding =
            RecordAnalysisItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordsAnalysisViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    override fun onBindViewHolder(holder: RecordsAnalysisViewHolder, position: Int) {
        holder.bind(recordsList[position])
    }
}