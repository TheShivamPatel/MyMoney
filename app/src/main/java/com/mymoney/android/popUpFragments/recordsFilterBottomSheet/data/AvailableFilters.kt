package com.mymoney.android.popUpFragments.recordsFilterBottomSheet.data

import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterItem
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterType
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.SelectionType
import com.mymoney.android.roomDB.data.TransactionType

object AvailableFilters {

    private val availableFilters: MutableList<FilterType> = mutableListOf()

    init {
        availableFilters.add(
            FilterType(
                id = 1,
                title = "Category",
                isMultiSelectable = true,
                selectionType = SelectionType.CHIP_TYPE,
                subFilters = listOf(
                    FilterItem(id = 1, name = TransactionType.INCOME.name, isSelected = true),
                    FilterItem(id = 2, name = TransactionType.EXPENSE.name, isSelected = true),
                    FilterItem(id = 3, name = TransactionType.TRANSFER.name, isSelected = true),
                )
            )
        )

        availableFilters.add(
            FilterType(
                id = 2,
                title = "View mode",
                isMultiSelectable = false,
                selectionType = SelectionType.CHECKBOX_TYPE,
                subFilters = listOf(
                    FilterItem(id = 1, name = FilterTimePeriod.DAILY.name,),
                    FilterItem(id = 2, name =  FilterTimePeriod.WEEKLY.name,),
                    FilterItem(id = 3, name =  FilterTimePeriod.MONTHLY.name, isSelected = true),
                )
            )
        )
    }

    fun getAvailableFilters(): List<FilterType> = availableFilters
}


enum class FilterTimePeriod{
    DAILY, WEEKLY, MONTHLY
}