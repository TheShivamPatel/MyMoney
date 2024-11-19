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
    }

    private fun setUpRecyclerView() {
        binding.recordsOverviewRv.layoutManager = LinearLayoutManager(context)
        viewModel.allRecordsWithDetails.observe(viewLifecycleOwner, Observer { records ->
            adapter = context?.let { RecordsAnalysisAdapter(records) }
            binding.recordsOverviewRv.adapter = adapter
        })
    }
}