package com.mymoney.android.home.fragments.account.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.AccountsItemBinding
import com.mymoney.android.roomDB.data.Account

class AccountsAdapter(
    private val accountList: List<Account>,
    private val deleteAccount: (account: Account) -> Unit
) :
    RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(private val binding: AccountsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(account: Account) {
            binding.titleTv.text = account.name
            binding.trailingImg.setOnClickListener { deleteAccount(account) }
            binding.subtitleTv.text = "Balance: ₹${account.balance}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = AccountsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val category = accountList[position]
        holder.bind(category)
    }

    override fun getItemCount() = accountList.size
}