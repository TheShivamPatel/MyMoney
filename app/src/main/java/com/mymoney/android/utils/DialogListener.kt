package com.mymoney.android.utils

import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.data.Category

interface DialogListener {
    fun onNegativeButtonClick()
    fun onPositiveButtonClick(category: Category)
    fun onSecondaryButtonClick()
    fun onAccountPositiveButtonClick(account: Account)
}