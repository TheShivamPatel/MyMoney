package com.mymoney.android.home.fragments.analysis

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.mymoney.android.R
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.databinding.FragmentAnalysisBinding
import com.mymoney.android.home.fragments.analysis.adapter.RecordsAnalysisAdapter
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModel
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModelProvider
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.RecordFilterBottomSheet
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.CategoryExpensePercentage
import com.mymoney.android.roomDB.database.MyMoneyDatabase

class AnalysisFragment : Fragment(R.layout.fragment_analysis) {

    private lateinit var binding: FragmentAnalysisBinding
    private var adapter: RecordsAnalysisAdapter? = null
    private lateinit var repository: TransactionRepository
    private lateinit var financeRepository: FinanceRepository
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
        financeRepository = FinanceRepository(transactionDao)
        repository = TransactionRepository(transactionDao)
        viewModel = ViewModelProvider(requireActivity(), RecordsViewModelProvider(repository, financeRepository))[RecordsViewModel::class.java]
        setUpRecyclerView()
        setUpAccountSummary()
        setUpViewMode()
        setUpOnClick()
    }

    private fun setUpOnClick() {
        binding.filterImg.setOnClickListener {
            activity?.supportFragmentManager?.let { it1 ->
                RecordFilterBottomSheet(positiveCallBack = {
                    viewModel.setFilterTimePeriod()
                }).show(
                    it1,
                    "RecordFilterBottomSheet"
                )
            }
        }
    }

    private fun setUpAccountSummary() {

        viewModel.totalIncome.observe(viewLifecycleOwner, Observer { income ->
            binding.accountSummary.setIncomeData("INCOME", income ?: 0.0)
        })
        viewModel.totalExpense.observe(viewLifecycleOwner, Observer { expense ->
            binding.accountSummary.setExpenseData("EXPENSE", expense ?: 0.0)
        })
        binding.accountSummary.setThirdSectionData("BALANCE", 10000.0)
    }

    private fun setUpViewMode() {

        binding.nextImgBtn.setOnClickListener { viewModel.nextDateWeekMonth() }
        binding.backImgBtn.setOnClickListener { viewModel.previousDateWeekMonth() }

        viewModel.viewMode.observe(viewLifecycleOwner, Observer {
            binding.currentViewModelTv.text = it
        })
    }

    private fun setUpRecyclerView() {
        binding.recordsOverviewRv.layoutManager = LinearLayoutManager(context)

        viewModel.filteredAllTotalExpensesByCategory.observe(viewLifecycleOwner, Observer {categoryPercentageList->
            adapter = context?.let { RecordsAnalysisAdapter(categoryPercentageList) }
            binding.recordsOverviewRv.adapter = adapter

            setUpPieChart(categoryPercentageList)
        })
    }

    private fun setUpPieChart(categoryPercentageList: List<CategoryExpensePercentage>) {

        val pieEntries = categoryPercentageList.map { category ->
            PieEntry(category.percentage.toFloat(), category.categoryName)
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.TRANSPARENT
        dataSet.valueTextSize = 0f

        var data = PieData(dataSet)

        binding.analysisPieChart.apply{
            this.data = data
            description.isEnabled = false
            animateY(1000)
            holeRadius = 40f
            setDrawEntryLabels(false)
            isRotationEnabled = false

            legend.isEnabled = true
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.setDrawInside(false)
            legend.textSize = 12f
            invalidate()
        }
    }
}