package com.mymoney.android.home.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mymoney.android.R
import com.mymoney.android.databinding.FragmentAccountsBinding
import com.mymoney.android.home.fragments.account.adapter.AccountsAdapter
import com.mymoney.android.home.fragments.account.repository.AccountsRepository
import com.mymoney.android.home.fragments.account.viewmodel.AccountsViewModel
import com.mymoney.android.home.fragments.account.viewmodel.AccountsViewModelProvider
import com.mymoney.android.roomDB.daos.AccountDao
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.database.MyMoneyDatabase

class AccountsFragment : Fragment(R.layout.fragment_accounts) {

    private lateinit var binding: FragmentAccountsBinding
    private lateinit var viewModel: AccountsViewModel
    private var repository: AccountsRepository? = null
    private lateinit var accountsDao: AccountDao
    private var accountAdapter: AccountsAdapter? = null

    companion object {
        fun newInstance(): AccountsFragment {
            return AccountsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            accountsDao = MyMoneyDatabase.getDatabase(it).accountDao()
        }
        repository = AccountsRepository(accountsDao)
        viewModel = ViewModelProvider(this, AccountsViewModelProvider(repository!!))[AccountsViewModel::class.java]
        setUpRecyclerview()
    }

    private fun setUpRecyclerview() {

        binding.accountsRv.layoutManager = LinearLayoutManager(context)

        viewModel.allAccounts.observe(viewLifecycleOwner, Observer { accountList ->
            accountAdapter = AccountsAdapter(accountList) { account ->
                deleteAccount(account)
            }
            binding.accountsRv.adapter = accountAdapter
        })
    }

    private fun deleteAccount(account: Account) {
        viewModel.removeAccount(account)
    }

}