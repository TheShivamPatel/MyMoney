package com.mymoney.android.addEditRecord.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mymoney.android.roomDB.data.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEditRecordViewModel() : ViewModel() {

    private val _pickedDate = MutableLiveData<String>()
    val pickedDate : LiveData<String>  get() = _pickedDate

    private val _pickedTime = MutableLiveData<String>()
    val pickedTime: LiveData<String> get() = _pickedTime

    private val _transactionType = MutableLiveData<TransactionType>()
    val transactionType: LiveData<TransactionType> get() = _transactionType

    fun setPickedDate(date: String) {
        _pickedDate.value = date
    }

    fun setPickedTime(time: String) {
        _pickedTime.value = time
    }

    fun setTransactionType(type: TransactionType) {
        _transactionType.value = type
    }

    private fun getCurrentDateFormatted(): String {
        val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormatter.format(currentDate)
    }

    private fun getCurrentTimeFormatted(): String {
        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentDate = Date()
        return timeFormatter.format(currentDate)
    }

    init {
        _pickedDate.value = getCurrentDateFormatted()
        _pickedTime.value = getCurrentTimeFormatted()
    }

}