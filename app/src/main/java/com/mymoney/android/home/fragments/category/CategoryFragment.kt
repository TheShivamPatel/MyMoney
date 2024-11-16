package com.mymoney.android.home.fragments.category

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mymoney.android.R
import com.mymoney.android.databinding.FragmentCategoryBinding
import com.mymoney.android.home.fragments.category.adapter.CategoryAdapter
import com.mymoney.android.home.fragments.category.repository.CategoryRepository
import com.mymoney.android.home.fragments.category.viewmodel.CategoryViewModel
import com.mymoney.android.home.fragments.category.viewmodel.CategoryViewModelProvider
import com.mymoney.android.roomDB.daos.CategoryDao
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.database.MyMoneyDatabase

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var viewModel: CategoryViewModel
    private var repository: CategoryRepository? = null
    private lateinit var categoryDao: CategoryDao
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
        }
        repository = CategoryRepository(categoryDao)
        viewModel = ViewModelProvider(
            this,
            CategoryViewModelProvider(repository!!)
        )[CategoryViewModel::class.java]
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {

        binding.rvIncomeCategories.layoutManager = LinearLayoutManager(context)
        binding.rvExpenseCategories.layoutManager = LinearLayoutManager(context)

        viewModel.incomeCategory.observe(viewLifecycleOwner, Observer { categoryList ->
            incomeCategoryAdapter = context?.let {
                CategoryAdapter(it, categoryList) { category ->
                    deleteCategory(category)
                }
            }
            binding.rvIncomeCategories.adapter = incomeCategoryAdapter
        })

        viewModel.expenseCategory.observe(viewLifecycleOwner, Observer { categoryList ->
            expenseCategoryAdapter =
                context?.let {
                    CategoryAdapter(it, categoryList) { category ->
                        deleteCategory(category)
                    }
                }
            binding.rvExpenseCategories.adapter = expenseCategoryAdapter
        })
    }

    private fun deleteCategory(category: Category) {
        viewModel.deleteCategory(category)
    }
}