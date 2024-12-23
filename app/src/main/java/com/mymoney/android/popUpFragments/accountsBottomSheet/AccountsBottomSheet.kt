package com.mymoney.android.popUpFragments.accountsBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mymoney.android.databinding.BottomSheetAccountsBinding
import com.mymoney.android.home.fragments.account.repository.AccountsRepository
import com.mymoney.android.home.fragments.account.viewmodel.AccountsViewModel
import com.mymoney.android.home.fragments.account.viewmodel.AccountsViewModelProvider
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.popUpFragments.accountsBottomSheet.adapter.SelectAccountsAdapter
import com.mymoney.android.roomDB.daos.AccountDao
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.database.MyMoneyDatabase

class AccountsBottomSheet(private val onSelectAccount: (account: Account) -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAccountsBinding
    private var selectAccountsAdapter: SelectAccountsAdapter? = null
    private lateinit var viewModel: AccountsViewModel
    private lateinit var repository: AccountsRepository
    private lateinit var financeRepository: FinanceRepository
    private lateinit var accountsDao: AccountDao
    private lateinit var transactionDao: TransactionDao

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            accountsDao = MyMoneyDatabase.getDatabase(it).accountDao()
            transactionDao = MyMoneyDatabase.getDatabase(it).transactionDao()
        }
        repository = AccountsRepository(accountsDao)
        financeRepository = FinanceRepository(transactionDao)
        viewModel = ViewModelProvider(
            this,
            AccountsViewModelProvider(repository, financeRepository)
        )[AccountsViewModel::class.java]

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.accountsRv.layoutManager = LinearLayoutManager(context)

        viewModel.allAccounts.observe(viewLifecycleOwner, Observer { accountList ->
            selectAccountsAdapter = SelectAccountsAdapter(accountList) { account ->
                onSelectAccount(account)
                dismiss()
            }
            binding.accountsRv.adapter = selectAccountsAdapter
        })
    }

}