package com.mymoney.android.popUpFragments.categoriesBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mymoney.android.databinding.BottomSheetCategoriesBinding
import com.mymoney.android.home.fragments.category.repository.CategoryRepository
import com.mymoney.android.home.fragments.category.viewmodel.CategoryViewModel
import com.mymoney.android.home.fragments.category.viewmodel.CategoryViewModelProvider
import com.mymoney.android.popUpFragments.categoriesBottomSheet.adapter.SelectCategoriesAdapter
import com.mymoney.android.roomDB.daos.CategoryDao
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.database.MyMoneyDatabase

class CategoriesBottomSheet(private val onSelectCategory: (category: Category) -> Unit) :
    BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCategoriesBinding
    private lateinit var viewModel: CategoryViewModel
    private var repository: CategoryRepository? = null
    private lateinit var categoryDao: CategoryDao
    private var selectCategoryAdapter: SelectCategoriesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCategoriesBinding.inflate(inflater, container, false)
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

        binding.categoriesRv.layoutManager = GridLayoutManager(context, 3)

        viewModel.expenseCategory.observe(viewLifecycleOwner, Observer { categoryList ->
            selectCategoryAdapter =
                context?.let {
                    SelectCategoriesAdapter(categoryList) { category ->
                        onSelectCategory(category)
                    }
                }
            binding.categoriesRv.adapter = selectCategoryAdapter
        })
    }

}