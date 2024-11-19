package com.mymoney.android.home.fragments.analysis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.RecordAnalysisItemBinding
import com.mymoney.android.roomDB.data.TransactionWithDetails

class RecordsAnalysisAdapter(private val recordsList: List<TransactionWithDetails>) :
    RecyclerView.Adapter<RecordsAnalysisAdapter.RecordsAnalysisViewHolder>() {

    inner class RecordsAnalysisViewHolder(private val binding: RecordAnalysisItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: TransactionWithDetails) {
            binding.titleTv.text = record.categoryName
            binding.subtitleTv.text = "-${record.amount}"
            binding.tvTrailing.text = "34.25%"
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