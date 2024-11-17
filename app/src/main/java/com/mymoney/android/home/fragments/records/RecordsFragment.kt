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
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.databinding.FragmentRecordsBinding
import com.mymoney.android.home.fragments.records.adapter.RecordsAdapter
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModel
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModelProvider
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.database.MyMoneyDatabase

class RecordsFragment : Fragment(R.layout.fragment_records) {

    private lateinit var binding: FragmentRecordsBinding
    private var recordsAdapter: RecordsAdapter? = null
    private var repository: TransactionRepository? = null
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
    ): View? {
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
        repository = TransactionRepository(transactionDao)
        viewModel = ViewModelProvider(
            this,
            RecordsViewModelProvider(repository!!)
        )[RecordsViewModel::class.java]
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.recordsRv.layoutManager = LinearLayoutManager(context)
        viewModel.allRecords.observe(viewLifecycleOwner, Observer { records ->
            recordsAdapter = RecordsAdapter(records)
            binding.recordsRv.adapter = recordsAdapter
        })
    }
}