package com.mymoney.android.home.fragments.records

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mymoney.android.R
import com.mymoney.android.addEditRecord.AddEditRecordActivity
import com.mymoney.android.addEditRecord.AddEditRecordActivity.Companion.TRANSACTION_ID
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.databinding.FragmentRecordsBinding
import com.mymoney.android.home.fragments.records.adapter.RecordsAdapter
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModel
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModelProvider
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.RecordFilterBottomSheet
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.database.MyMoneyDatabase
import com.mymoney.android.viewUtils.ViewUtils

class RecordsFragment : Fragment(R.layout.fragment_records) {

    private lateinit var binding: FragmentRecordsBinding
    private var recordsAdapter: RecordsAdapter? = null
    private lateinit var repository: TransactionRepository
    private lateinit var financeRepository: FinanceRepository
    private lateinit var transactionDao: TransactionDao
    private lateinit var viewModel: RecordsViewModel

    companion object {
        fun newInstance(): RecordsFragment {
            return RecordsFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddNewRecord.setOnClickListener {
            startActivity(Intent(context, AddEditRecordActivity::class.java))
        }

        context?.let {
            transactionDao = MyMoneyDatabase.getDatabase(it).transactionDao()
        }
        financeRepository = FinanceRepository(transactionDao)
        repository = TransactionRepository(transactionDao)
        viewModel = ViewModelProvider(
            requireActivity(),
            RecordsViewModelProvider(repository, financeRepository)
        )[RecordsViewModel::class.java]

        viewModel.observeRecords()

        setUpViewMode()
        setUpOnClick()
        setUpAccountSummary()
        setUpRecyclerView()
    }

    private fun setUpAccountSummary() {
        viewModel.totalIncome.observe(viewLifecycleOwner, Observer { income ->
            binding.accountSummary.setIncomeData("INCOME", income)
        })
        viewModel.totalExpense.observe(viewLifecycleOwner, Observer { expense ->
            binding.accountSummary.setExpenseData("EXPENSE", expense)
        })
        binding.accountSummary.setThirdSectionData("BALANCE", 10000.0)
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

    private fun setUpViewMode() {

        binding.nextImgBtn.setOnClickListener { viewModel.nextDateWeekMonth() }
        binding.backImgBtn.setOnClickListener { viewModel.previousDateWeekMonth() }

        viewModel.viewMode.observe(viewLifecycleOwner, Observer {
            binding.currentViewModelTv.text = it
        })
    }

    private fun setUpRecyclerView() {
        binding.recordsRv.layoutManager = LinearLayoutManager(context)
        viewModel.allRecords.observe(viewLifecycleOwner, Observer { records ->
            context?.let { ViewUtils.showToast(it, records.size.toString()) }
            recordsAdapter = context?.let {
                RecordsAdapter(records, it, object : RecordsAdapter.ItemClickListener {
                    override fun onItemClick(id: Int) {
                        val intent = Intent(context, AddEditRecordActivity::class.java)
                        intent.putExtra(TRANSACTION_ID, id)
                        startActivity(intent)
                    }
                })
            }
            binding.recordsRv.adapter = recordsAdapter
        })
    }
}