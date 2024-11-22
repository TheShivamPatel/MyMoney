package com.mymoney.android.popUpFragments.recordsFilterBottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mymoney.android.databinding.BottomSheetRecordFilterBinding
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.adapter.ChildFilterAdapter
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.adapter.ParentFilterAdapter
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.data.AvailableFilters
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterItem
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterType

class RecordFilterBottomSheet( private val positiveCallBack: ()-> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetRecordFilterBinding
    private lateinit var parentFilterAdapter: ParentFilterAdapter
    private lateinit var childFilterAdapter: ChildFilterAdapter
    private var selectedFilterType: FilterType? = null
    private var parentFilterList = AvailableFilters.getAvailableFilters()

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

        setUpOnClick()
        setUpRecyclerView()
    }

    private fun setUpOnClick() {
        binding.btnPositive.setOnClickListener {
            positiveCallBack()
            dismiss()
        }
    }

    private fun setUpRecyclerView() {
        parentFilterAdapter = ParentFilterAdapter(parentFilterList) { selectedFilter ->
            loadChildFilters(selectedFilter)
        }
        binding.parentFilterRv.layoutManager = LinearLayoutManager(context)
        binding.parentFilterRv.adapter = parentFilterAdapter

        if (parentFilterList.isNotEmpty()) {
            loadChildFilters(parentFilterList[0])
        }
    }

    private fun loadChildFilters(selectedFilter: FilterType) {
        selectedFilterType = selectedFilter

        childFilterAdapter = ChildFilterAdapter(selectedFilter, object : ChildFilterAdapter.ItemClickListener {
            override fun onCheckboxSelectionChanged(filterItem: FilterItem) {
                val filterIndex = selectedFilterType?.subFilters?.indexOf(filterItem)
                if (filterIndex != null && filterIndex >= 0) {
                    selectedFilterType?.subFilters?.get(filterIndex)?.isSelected = filterItem.isSelected
                }
            }
        })
        binding.childFilterRv.layoutManager = LinearLayoutManager(context)
        binding.childFilterRv.adapter = childFilterAdapter
    }

}
