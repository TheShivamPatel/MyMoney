package com.mymoney.android.home.fragments.category

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
import com.mymoney.android.databinding.FragmentCategoryBinding
import com.mymoney.android.home.fragments.category.adapter.CategoryAdapter
import com.mymoney.android.home.fragments.category.repository.CategoryRepository
import com.mymoney.android.home.fragments.category.viewmodel.CategoryViewModel
import com.mymoney.android.home.fragments.category.viewmodel.CategoryViewModelProvider
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.popUpFragments.categoryCreationUpdationDialog.CategoryCreationUpdationDialog
import com.mymoney.android.popUpFragments.categoryCreationUpdationDialog.dialogListener.DialogListener
import com.mymoney.android.roomDB.daos.CategoryDao
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.database.MyMoneyDatabase
import com.mymoney.android.viewUtils.ViewUtils

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var viewModel: CategoryViewModel
    private lateinit var financeRepository: FinanceRepository
    private lateinit var repository: CategoryRepository
    private lateinit var categoryDao: CategoryDao
    private lateinit var transactionDao: TransactionDao
    private var incomeCategoryAdapter: CategoryAdapter? = null
    private var expenseCategoryAdapter: CategoryAdapter? = null

    companion object {
        fun newInstance(): CategoryFragment {
            return CategoryFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            categoryDao = MyMoneyDatabase.getDatabase(it).categoryDao()
            transactionDao = MyMoneyDatabase.getDatabase(it).transactionDao()
        }
        repository = CategoryRepository(categoryDao)
        financeRepository = FinanceRepository(transactionDao)
        viewModel = ViewModelProvider(
            this,
            CategoryViewModelProvider(repository, financeRepository)
        )[CategoryViewModel::class.java]
        setUpRecyclerView()
        setUpAccountSummary()
        setUpViews()
    }

    private fun setUpViews() {
        binding.addNewCategory.apply {
            root.setOnClickListener {
                createNewCategory()
            }
            title.text = getString(R.string.add_new_category_capital)
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

    private fun setUpRecyclerView() {

        binding.rvIncomeCategories.layoutManager = LinearLayoutManager(context)
        binding.rvExpenseCategories.layoutManager = LinearLayoutManager(context)

        viewModel.incomeCategory.observe(viewLifecycleOwner, Observer { categoryList ->
            incomeCategoryAdapter = context?.let {
                CategoryAdapter(it, categoryList, object : CategoryAdapter.OnItemClick{
                    override fun onCategoryClicked(category: Category) {
                        openCategoryOperationDialog(category)
                    }
                })
            }
            binding.rvIncomeCategories.adapter = incomeCategoryAdapter
        })

        viewModel.expenseCategory.observe(viewLifecycleOwner, Observer { categoryList ->
            expenseCategoryAdapter =
                context?.let {
                    CategoryAdapter(it, categoryList, object : CategoryAdapter.OnItemClick{
                        override fun onCategoryClicked(category: Category) {
                            openCategoryOperationDialog(category)
                        }
                    })
                }
            binding.rvExpenseCategories.adapter = expenseCategoryAdapter
        })
    }

    private fun createNewCategory() {
        CategoryCreationUpdationDialog.showFragment(
            fragmentManager = requireActivity().supportFragmentManager,
            listener = object : DialogListener{
                override fun onNegativeButtonClick() {
                    ViewUtils.showToast(requireContext(), "Cancel")
                }

                override fun onPositiveButtonClick(newCategory: Category) {
                    saveNewCategory(newCategory)
                }

                override fun onSecondaryButtonClick() {
                    ViewUtils.showToast(requireContext(), "Delete")
                }
            },
            dialogType = CategoryCreationUpdationDialog.Companion.CategoryDialogType.CREATE
        )
    }

    private fun openCategoryOperationDialog(category: Category) {
        CategoryCreationUpdationDialog.showFragment(
            fragmentManager = requireActivity().supportFragmentManager,
            listener = object : DialogListener{
                override fun onNegativeButtonClick() {
                }

                override fun onPositiveButtonClick(newCategory: Category) {
                    updateCategory(newCategory)
                }

                override fun onSecondaryButtonClick() {
                    deleteCategory(category)
                }
            },
            dialogType = CategoryCreationUpdationDialog.Companion.CategoryDialogType.UPDATE,
            category = category
        )
    }

    private fun deleteCategory(category: Category) {
        viewModel.deleteCategory(category)
        ViewUtils.showToast(requireContext(), "Category Deleted")
    }

    private fun saveNewCategory(category: Category){
        viewModel.saveCategory(category)
        ViewUtils.showToast(requireContext(), "Saved")
    }

    private fun updateCategory(category: Category) {
        viewModel.updateCategory(category)
        ViewUtils.showToast(requireContext(), "Category Updated!")
    }

}