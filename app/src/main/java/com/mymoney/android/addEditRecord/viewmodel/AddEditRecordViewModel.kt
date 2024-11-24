package com.mymoney.android.addEditRecord.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.roomDB.data.Transaction
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.viewUtils.ViewUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEditRecordViewModel(private val repo: TransactionRepository) : ViewModel() {

    private val _transactionToEdit = MutableLiveData<Transaction?>()
    val transactionToEdit: LiveData<Transaction?> get() = _transactionToEdit

    private val _pickedDate = MutableLiveData<String>()
    val pickedDate: LiveData<String> get() = _pickedDate

    private val _pickedTime = MutableLiveData<String>()
    val pickedTime: LiveData<String> get() = _pickedTime

    private val _transactionType = MutableLiveData<TransactionType>()
    val transactionType: LiveData<TransactionType> get() = _transactionType

    private val _selectedType1 = MutableLiveData<Int>()
    val selectedType1: LiveData<Int> get() = _selectedType1

    private val _selectedType2 = MutableLiveData<Int>()
    val selectedType2: LiveData<Int> get() = _selectedType2

    fun setPickedDate(date: String) {
        _pickedDate.value = date
    }

    fun setPickedTime(time: String) {
        _pickedTime.value = time
    }

    fun setPickedType1Value(id: Int) {
        _selectedType1.value = id
    }

    fun setPickedType2Value(id: Int) {
        _selectedType2.value = id
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

    fun saveTransaction(transaction: Transaction, context: Context) {
        viewModelScope.launch {
            if (transaction.id == 0) {
                repo.insertTransaction(transaction)
                ViewUtils.showToast(context, "Transaction Saved!")
            } else {
                repo.updateTransaction(transaction)
                ViewUtils.showToast(context, "Transaction Updated!")
            }
        }
    }

    fun loadTransaction(transactionId: Int) {
        viewModelScope.launch {
            val transaction = repo.getTransactionById(transactionId)
            _transactionToEdit.postValue(transaction)
        }
    }

    fun fetchAccountName(accountId: Int, callback: (String) -> Unit) {
        viewModelScope.launch {
            val accountName = repo.getAccountNameById(accountId)
            callback(accountName)
        }
    }

    fun fetchCategoryName(categoryId: Int, callback: (String) -> Unit) {
        viewModelScope.launch {
            val categoryName = repo.getCategoryNameById(categoryId)
            callback(categoryName)
        }
    }

    fun deleteRecord(transactionId: Int) {
        viewModelScope.launch {
            repo.deleteTransaction(transactionId)
        }
    }
}

class AddEditRecordViewModelProvider(private val repo: TransactionRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddEditRecordViewModel(repo) as T
    }
}