package com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model

data class FilterType(
    val id: Int,
    val title: String,
    val isMultiSelectable: Boolean,
    val selectionType: SelectionType,
    val subFilters: List<FilterItem>,
)

data class FilterItem(
    val id: Int,
    val name: String,
    var isSelected: Boolean = false,
)

enum class SelectionType {
    CHIP_TYPE,
    CHECKBOX_TYPE,
}