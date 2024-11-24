package com.mymoney.android.popUpFragments.categoryCreationUpdationDialog.dialogListener

import com.mymoney.android.roomDB.data.Category

interface DialogListener {
    fun onNegativeButtonClick()
    fun onPositiveButtonClick(category: Category)
    fun onSecondaryButtonClick()
}