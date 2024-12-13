package com.mymoney.android.home.fragments.records

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.mymoney.android.roomDB.data.Transaction
import com.mymoney.android.roomDB.database.MyMoneyDatabase
import com.mymoney.android.viewUtils.ViewUtils

class RecordsFragment : Fragment(R.layout.fragment_records) {

    private lateinit var binding: FragmentRecordsBinding
    private var recordsAdapter: RecordsAdapter? = null
    private lateinit var repository: TransactionRepository
    private lateinit var financeRepository: FinanceRepository
    private lateinit var transactionDao: TransactionDao
    private lateinit var viewModel: RecordsViewModel

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

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

        setUpViewMode()
        setUpOnClick()
        setUpAccountSummary()
        setUpRecyclerView()

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let {
                        val transaction = it.getSerializableExtra("new_transaction") as Transaction
                        val wantToDelete = it.getBooleanExtra("delete_transaction", false)
                        if (wantToDelete) {
                            viewModel.deleteRecord(transaction.id)
                        } else {
                            saveTransaction(transaction)
                        }
                    }
                }
            }
    }

    private fun saveTransaction(transaction: Transaction) {
        context?.let {
            ViewUtils.showToast(it, "Transaction Saved Successfully")
            viewModel.saveTransaction(transaction, it)
        }
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
        binding.fabAddNewRecord.setOnClickListener {
            val intent = Intent(context, AddEditRecordActivity::class.java)
            resultLauncher.launch(intent)
        }
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

        context?.let { transactionDao = MyMoneyDatabase.getDatabase(it).transactionDao() }
        financeRepository = FinanceRepository(transactionDao)
        repository = TransactionRepository(transactionDao)
        viewModel = ViewModelProvider(
            requireActivity(),
            RecordsViewModelProvider(repository, financeRepository)
        )[RecordsViewModel::class.java]

        viewModel.observeRecords()

        binding.nextImgBtn.setOnClickListener { viewModel.nextDateWeekMonth() }
        binding.backImgBtn.setOnClickListener { viewModel.previousDateWeekMonth() }

        viewModel.viewMode.observe(viewLifecycleOwner, Observer {
            binding.currentViewModelTv.text = it
        })
    }

    private fun setUpRecyclerView() {
        binding.recordsRv.layoutManager = LinearLayoutManager(context)
        viewModel.allRecords.observe(viewLifecycleOwner, Observer { records ->
            recordsAdapter = RecordsAdapter(records, object : RecordsAdapter.ItemClickListener {
                override fun onItemClick(id: Int) {
                    val intent = Intent(context, AddEditRecordActivity::class.java)
                    intent.putExtra(TRANSACTION_ID, id)
                    resultLauncher.launch(intent)
                }
            })
            binding.recordsRv.adapter = recordsAdapter
        })
    }
}