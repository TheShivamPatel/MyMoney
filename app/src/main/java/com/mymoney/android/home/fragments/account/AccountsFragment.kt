package com.mymoney.android.home.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mymoney.android.R
import com.mymoney.android.databinding.FragmentAccountsBinding
import com.mymoney.android.home.fragments.account.adapter.AccountsAdapter
import com.mymoney.android.home.fragments.account.repository.AccountsRepository
import com.mymoney.android.home.fragments.account.viewmodel.AccountsViewModel
import com.mymoney.android.home.fragments.account.viewmodel.AccountsViewModelProvider
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.popUpFragments.accountCreationUpdationDialog.AccountCreationUpdationDialog
import com.mymoney.android.roomDB.daos.AccountDao
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.database.MyMoneyDatabase
import com.mymoney.android.utils.DialogListener
import com.mymoney.android.viewUtils.ViewUtils

class AccountsFragment : Fragment(R.layout.fragment_accounts) {

    private lateinit var binding: FragmentAccountsBinding
    private lateinit var viewModel: AccountsViewModel
    private lateinit var repository: AccountsRepository
    private lateinit var financeRepository: FinanceRepository
    private lateinit var transactionDao: TransactionDao
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
    ): View {
        binding = FragmentAccountsBinding.inflate(inflater, container, false)
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
        setUpRecyclerview()
        setUpAccountSummary()
        setUpViews()
    }

    private fun setUpViews() {
        binding.addNewAccount.apply {
            root.setOnClickListener {
                createNewAccount()
            }
            title.text = getString(R.string.add_new_account_capital)
            leadingIcon.setImageDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.icon_plus)
            )
        }
    }

    private fun setUpAccountSummary() {
        viewModel.totalIncome.observe(viewLifecycleOwner, Observer { income ->
            binding.accountSummary.setIncomeData("INCOME SO FAR", income)
        })
        viewModel.totalExpense.observe(viewLifecycleOwner, Observer { expense ->
            binding.accountSummary.setExpenseData("EXPENSE SO FAR", expense)
        })
        viewModel.totalBalance.observe(viewLifecycleOwner, Observer { balance ->
            binding.allAccountsBalanceTv.text = "[ All Accounts ₹${balance} ]"
        })
    }

    private fun setUpRecyclerview() {

        binding.accountsRv.layoutManager = LinearLayoutManager(context)
        viewModel.allAccounts.observe(viewLifecycleOwner, Observer { accountList ->
            accountAdapter = AccountsAdapter(accountList, object : AccountsAdapter.OnItemClick {
                override fun onAccountClicked(account: Account) {
                    openAccountOperationDialog(account)
                }
            })
            binding.accountsRv.adapter = accountAdapter
        })
    }

    private fun createNewAccount() {
        AccountCreationUpdationDialog.showFragment(
            fragmentManager = requireActivity().supportFragmentManager,
            listener = object : DialogListener {
                override fun onNegativeButtonClick() {
                    ViewUtils.showToast(requireContext(), "Cancel")
                }

                override fun onPositiveButtonClick(newCategory: Category) {
                }

                override fun onSecondaryButtonClick() {
                    ViewUtils.showToast(requireContext(), "Delete")
                }

                override fun onAccountPositiveButtonClick(account: Account) {
                    saveNewAccount(account)
                }
            },
            dialogType = AccountCreationUpdationDialog.Companion.AccountDialogType.CREATE
        )
    }

    private fun openAccountOperationDialog(account: Account) {
        AccountCreationUpdationDialog.showFragment(
            fragmentManager = requireActivity().supportFragmentManager,
            listener = object : DialogListener {
                override fun onNegativeButtonClick() {
                }

                override fun onPositiveButtonClick(newCategory: Category) {
                }

                override fun onSecondaryButtonClick() {
                    deleteAccount(account)
                }

                override fun onAccountPositiveButtonClick(account: Account) {
                    updateAccount(account)
                }
            },
            dialogType = AccountCreationUpdationDialog.Companion.AccountDialogType.UPDATE,
            account = account
        )
    }

    private fun saveNewAccount(account: Account) {
        viewModel.saveAccount(account)
        ViewUtils.showToast(requireContext(), "Saved")
    }

    private fun updateAccount(account: Account) {
        viewModel.updateAccount(account)
        ViewUtils.showToast(requireContext(), "Account Updated!")
    }

    private fun deleteAccount(account: Account) {
        viewModel.removeAccount(account)
        ViewUtils.showToast(requireContext(), "Account Deleted")
    }
}