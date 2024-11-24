package com.mymoney.android.home.fragments.records.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.R
import com.mymoney.android.databinding.RecordsItemBinding
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.roomDB.data.TransactionWithDetails
import com.mymoney.android.utils.CategoryIcon

class RecordsAdapter(
    private val recordsList: List<TransactionWithDetails>,
    private val context: Context,
    private val listener: ItemClickListener
) :
    RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder>() {

    inner class RecordsViewHolder(private val binding: RecordsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: TransactionWithDetails) {
            if (record.type != TransactionType.TRANSFER.name) {
                binding.titleTv.text = record.categoryName
            }
            binding.subtitleTv.text = record.type

            when (record.type) {
                TransactionType.EXPENSE.name -> {
                    binding.tvTrailing.text = "-₹${record.amount}"
                    binding.tvTrailing.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.red_400)
                    )
                }

                TransactionType.INCOME.name -> {
                    binding.tvTrailing.text = "₹${record.amount}"
                    binding.tvTrailing.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.green_500)
                    )
                }

                else -> {
                    binding.tvTrailing.text = "₹${record.amount}"
                    binding.tvTrailing.setTextColor(
                        ContextCompat.getColor(binding.root.context, R.color.purple_400)
                    )
                }
            }

            if (adapterPosition == 0 || record.date != recordsList[adapterPosition - 1].date) {
                binding.textView.visibility = View.VISIBLE
                binding.textView.text = record.date
                binding.view.visibility = View.VISIBLE
            } else {
                binding.textView.visibility = View.GONE
                binding.view.visibility = View.GONE
            }

            if (adapterPosition == recordsList.size - 1 || record.date != recordsList[adapterPosition + 1].date) {
                binding.view2.visibility = View.GONE
            } else {
                binding.view2.visibility = View.VISIBLE
            }

            if (record.type != TransactionType.TRANSFER.name) {
                val categoryIcon = try {
                    CategoryIcon.valueOf(record.categoryIcon!!)
                } catch (e: IllegalArgumentException) {
                    CategoryIcon.SALE
                }
                binding.imageView2.setImageDrawable(
                    ContextCompat.getDrawable(context, categoryIcon.drawableId)
                )
            }

            binding.root.setOnClickListener { listener.onItemClick(record.transactionId) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        val binding = RecordsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return recordsList.size
    }

    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        holder.bind(recordsList[position])
    }

    interface ItemClickListener {
        fun onItemClick(id: Int)
    }
}