package com.mymoney.android.home.fragments.records.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.data.AvailableFilters
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.data.FilterTimePeriod
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterType
import com.mymoney.android.roomDB.data.CategoryExpenseSummary
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.roomDB.data.TransactionWithDetails
import com.mymoney.android.viewUtils.ViewUtils.parseDate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.util.Date
import java.util.Locale

class RecordsViewModel(private val repo: TransactionRepository, private val financeRepository: FinanceRepository) : ViewModel() {

    val allTotalExpensesByCategory : LiveData<List<CategoryExpenseSummary>> = repo.getTotalByCategory(TransactionType.EXPENSE.name)

    val totalIncome: LiveData<Double?> = financeRepository.getTotalIncome(TransactionType.INCOME.name)

    val totalExpense: LiveData<Double?> = financeRepository.getTotalExpense(TransactionType.EXPENSE.name)

    private val filterList = AvailableFilters.getAvailableFilters()

    val allRecords = MutableLiveData<List<TransactionWithDetails>>()
    private val _unfilteredRecords = mutableListOf<TransactionWithDetails>()
    val unfilteredRecords: List<TransactionWithDetails> get() = _unfilteredRecords

    private var selectedCategories = emptyList<String>()
    var viewMode = MutableLiveData<String>("Dec, 2024")

    private var currentDate = LocalDate.now()
    private var currentMonth = LocalDate.now().monthValue
    private var currentWeekOfYear = LocalDate.now().get(ChronoField.ALIGNED_WEEK_OF_YEAR)
    private var currentYear = LocalDate.now().year


    init {
        observeRecords()
    }

    private fun observeRecords() {
        repo.getAllTransactionsWithDetails().observeForever { response ->
            response?.let {
                val dateTimeFormatter = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())

                val sortedList = it.sortedByDescending { transaction ->
                    val dateTimeString = "${transaction.date} ${transaction.time}"
                    try {
                        dateTimeFormatter.parse(dateTimeString)?.time
                    } catch (e: Exception) {
                        null
                    }
                }

                _unfilteredRecords.clear()
                _unfilteredRecords.addAll(sortedList)
                allRecords.postValue(sortedList)
                applyFilters()
            }
        }
    }


    fun applyFilters() {
        selectedCategories = filterList.getSubFilterNamesByType(1)
        val selectedViewMode = filterList.getSelectedViewMode()

        var filteredList = unfilteredRecords

        if (selectedCategories.isNotEmpty()) {
            filteredList = filteredList.filter { it.type in selectedCategories }
        }

        filteredList = when (selectedViewMode) {
            FilterTimePeriod.DAILY.name -> filterByDay(filteredList)
            FilterTimePeriod.WEEKLY.name -> filterByWeek(filteredList)
            FilterTimePeriod.MONTHLY.name -> filterByMonth(filteredList)
            else -> filterByMonth(filteredList)
        }

        allRecords.postValue(filteredList)
    }

    private fun List<FilterType>.getSelectedViewMode(): String {
        return this.firstOrNull { it.id == 2 }
            ?.subFilters
            ?.firstOrNull { it.isSelected }
            ?.name ?: FilterTimePeriod.MONTHLY.name
    }

    private fun filterByDay(records: List<TransactionWithDetails>): List<TransactionWithDetails> {
        val formattedDate = getFormattedDate(currentDate)
        return records.filter { it.date?.startsWith(formattedDate) == true }
    }

    private fun filterByWeek(records: List<TransactionWithDetails>): List<TransactionWithDetails> {
        val startOfWeek = currentDate.with(DayOfWeek.MONDAY)
        val endOfWeek = currentDate.with(DayOfWeek.SUNDAY)
        return records.filter {
            val recordDate = parseDate(it.date)
            recordDate in startOfWeek..endOfWeek
        }
    }

    private fun filterByMonth(records: List<TransactionWithDetails>): List<TransactionWithDetails> {
        return records.filter { getMonth(it.date) == currentMonth && getYear(it.date) == currentYear }
    }

    private fun getMonth(date: String?): Int {
        return try {
            val parsedDate = parseDate(date)
            parsedDate?.monthValue ?: -1
        } catch (e: Exception) {
            -1
        }
    }

    private fun getYear(date: String?): Int {
        return try {
            val parsedDate = parseDate(date)
            parsedDate?.year ?: -1
        } catch (e: Exception) {
            -1
        }
    }

    private fun List<FilterType>.getSubFilterNamesByType(type: Int): List<String> {
        return this.firstOrNull { it.id == type }
            ?.subFilters
            ?.filter { it.isSelected }
            ?.map { it.name }
            ?: listOf()
    }


    fun previousDateWeekMonth() {
        val selectedViewMode = filterList.getSelectedViewMode()

        when (selectedViewMode) {
            FilterTimePeriod.DAILY.name -> {
                currentDate = currentDate.minusDays(1)
                viewMode.postValue(getFormattedDate(currentDate))
            }
            FilterTimePeriod.WEEKLY.name -> {
                currentDate = currentDate.minusWeeks(1)
                val startOfWeek = currentDate.with(DayOfWeek.MONDAY)
                val endOfWeek = startOfWeek.plusDays(6)
                val weekRange = formatWeekRange(startOfWeek, endOfWeek)
                viewMode.postValue(weekRange)
            }
            FilterTimePeriod.MONTHLY.name -> {
                currentMonth -= 1
                if (currentMonth < 1) {
                    currentMonth = 12
                    currentYear -= 1
                }
                val monthName = LocalDate.of(currentYear, currentMonth, 1).month.name.capitalize()
                viewMode.postValue("$monthName, $currentYear")
            }
        }

        applyFilters()
    }


    fun nextDateWeekMonth() {
        val selectedViewMode = filterList.getSelectedViewMode()

        when (selectedViewMode) {
            FilterTimePeriod.DAILY.name -> {
                currentDate = currentDate.plusDays(1)
                viewMode.postValue(getFormattedDate(currentDate))
            }
            FilterTimePeriod.WEEKLY.name -> {
                currentDate = currentDate.plusWeeks(1)
                val startOfWeek = currentDate.with(DayOfWeek.MONDAY)
                val endOfWeek = startOfWeek.plusDays(6)
                val weekRange = formatWeekRange(startOfWeek, endOfWeek)
                viewMode.postValue(weekRange)
            }
            FilterTimePeriod.MONTHLY.name -> {
                currentMonth += 1
                if (currentMonth > 12) {
                    currentMonth = 1
                    currentYear += 1
                }
                val monthName = LocalDate.of(currentYear, currentMonth, 1).month.name.capitalize()
                viewMode.postValue("$monthName, $currentYear")
            }
        }

        applyFilters()
    }

    private fun formatWeekRange(startOfWeek: LocalDate, endOfWeek: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.getDefault())
        val startFormatted = startOfWeek.format(formatter)
        val endFormatted = endOfWeek.format(formatter)
        return "$startFormatted - $endFormatted"
    }

    private fun getFormattedDate(date: LocalDate): String {
        val formatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
        return formatter.format(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))
    }

}

class RecordsViewModelProvider(private val repo: TransactionRepository, private val financeRepository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordsViewModel(repo, financeRepository) as T
    }
}