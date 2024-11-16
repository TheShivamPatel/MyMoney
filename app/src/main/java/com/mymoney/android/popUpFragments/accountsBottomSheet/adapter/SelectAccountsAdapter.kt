package com.mymoney.android.popUpFragments.accountsBottomSheet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.TitleSubtitleHorizontalItemBinding
import com.mymoney.android.roomDB.data.Account

class SelectAccountsAdapter(
    private val accounts: List<Account>,
    private val onItemClick: (Account) -> Unit
) : RecyclerView.Adapter<SelectAccountsAdapter.SelectAccountsViewHolder>() {

    class SelectAccountsViewHolder(
        private val binding: TitleSubtitleHorizontalItemBinding,
        private val onItemClick: (Account) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(account: Account) {
            binding.titleTv.text = account.name
            binding.subtitleTv.text = "₹${account.balance}"
            binding.root.setOnClickListener {
                onItemClick(account)
            }
            binding.extraSpacer.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAccountsViewHolder {
        val binding = TitleSubtitleHorizontalItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectAccountsViewHolder(binding, onItemClick)
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onBindViewHolder(holder: SelectAccountsViewHolder, position: Int) {
        holder.bind(accounts[position])
    }
}
