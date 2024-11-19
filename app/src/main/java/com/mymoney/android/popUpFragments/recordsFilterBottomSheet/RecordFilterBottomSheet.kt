package com.mymoney.android.popUpFragments.recordsFilterBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.databinding.BottomSheetRecordFilterBinding
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModel
import com.mymoney.android.home.fragments.records.viewModel.RecordsViewModelProvider
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.roomDB.database.MyMoneyDatabase

class RecordFilterBottomSheet(): BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetRecordFilterBinding
    private var repository: TransactionRepository? = null
    private lateinit var transactionDao: TransactionDao
    private lateinit var viewModel: RecordsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetRecordFilterBinding.inflate(inflater, container, false)
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

    }



}