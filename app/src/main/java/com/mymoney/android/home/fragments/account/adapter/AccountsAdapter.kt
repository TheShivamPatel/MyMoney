package com.mymoney.android.home.fragments.account.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mymoney.android.databinding.AccountsItemBinding
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.utils.AccountIcon

class AccountsAdapter(
    private val accountList: List<Account>,
    private val context: Context,
    private val listener: OnItemClick
) :
    RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(private val binding: AccountsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(account: Account) {
            binding.titleTv.text = account.name
            val categoryIcon = try {
                AccountIcon.valueOf(account.icon)
            } catch (e: IllegalArgumentException) {
                AccountIcon.CASH
            }
            binding.imageView.setImageDrawable(
                ContextCompat.getDrawable(context, categoryIcon.drawableId)
            )
            binding.trailingImg.setOnClickListener { listener.onAccountClicked(account) }
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

    interface OnItemClick {
        fun onAccountClicked(account: Account)
    }

}