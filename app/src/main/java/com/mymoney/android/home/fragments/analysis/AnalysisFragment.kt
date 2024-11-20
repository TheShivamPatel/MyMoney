package com.mymoney.android.home.fragments.analysis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mymoney.android.R
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.databinding.FragmentAnalysisBinding
import com.mymoney.android.home.fragments.analysis.adapter.RecordsAnalysisAdapter
import com.mymoney.android.home.fragments.records.adapter.RecordsAdapter
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModel
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModelProvider
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.CategoryExpensePercentage
import com.mymoney.android.roomDB.database.MyMoneyDatabase

class AnalysisFragment : Fragment(R.layout.fragment_analysis) {

    private lateinit var binding: FragmentAnalysisBinding
    private var adapter: RecordsAnalysisAdapter? = null
    private var repository: TransactionRepository? = null
    private lateinit var transactionDao: TransactionDao
    private lateinit var viewModel: RecordsViewModel

    companion object {
        fun newInstance(): AnalysisFragment {
            return AnalysisFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            transactionDao = MyMoneyDatabase.getDatabase(it).transactionDao()
        }
        repository = TransactionRepository(transactionDao)
        viewModel = ViewModelProvider(
            this,
            RecordsViewModelProvider(repository!!)
        )[RecordsViewModel::class.java]
        setUpRecyclerView()
        setUpAccountSummary()
    }

    private fun setUpAccountSummary() {
        binding.accountSummary.setExpenseData("EXPENSE", 5000.0)
        binding.accountSummary.setIncomeData("INCOME",15000.0)
        binding.accountSummary.setThirdSectionData("BALANCE", 10000.0)
    }


    private fun setUpRecyclerView() {
        binding.recordsOverviewRv.layoutManager = LinearLayoutManager(context)
        viewModel.allTotalExpensesByCategory.observe(viewLifecycleOwner, Observer { categoryExpenses ->
            val totalExpense = categoryExpenses.sumByDouble { it.totalAmount }
            val categoryPercentageList = categoryExpenses.map {
                val percentage = (it.totalAmount / totalExpense) * 100
                val formattedPercentage = String.format("%.2f", percentage).toDouble()
                CategoryExpensePercentage(
                    categoryName = it.categoryName,
                    totalAmount = it.totalAmount,
                    percentage = formattedPercentage,
                    categoryIcon = it.categoryIcon
                )
            }.sortedByDescending { it.percentage }

            adapter = context?.let { RecordsAnalysisAdapter(categoryPercentageList, context!!) }
            binding.recordsOverviewRv.adapter = adapter
        })
    }
}