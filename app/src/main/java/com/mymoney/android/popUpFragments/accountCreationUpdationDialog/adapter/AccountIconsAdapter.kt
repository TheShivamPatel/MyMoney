package com.mymoney.android.popUpFragments.accountCreationUpdationDialog.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.R
import com.mymoney.android.databinding.SelectableIconItemBinding
import com.mymoney.android.utils.AccountIcon

class AccountIconsAdapter(
    private val icons: List<AccountIcon>,
    private val onItemClick: (AccountIcon) -> Unit
) : RecyclerView.Adapter<AccountIconsAdapter.AccountIconsViewHolder>() {

    private var selectedPosition: Int = -1

    inner class AccountIconsViewHolder(private val binding: SelectableIconItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(accountIcon: AccountIcon, isSelected: Boolean){

            binding.iconImg.setImageResource(accountIcon.drawableId)
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
                onItemClick(accountIcon)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountIconsViewHolder {
        val view = SelectableIconItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountIconsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountIconsViewHolder, position: Int) {
        holder.bind(icons[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = icons.size

    fun setSelectedIcon(iconName: String) {
        selectedPosition = icons.indexOfFirst { it.name == iconName }
        notifyItemChanged(selectedPosition)
    }

}