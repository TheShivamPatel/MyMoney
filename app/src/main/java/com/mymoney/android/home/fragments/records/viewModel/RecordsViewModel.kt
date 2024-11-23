package com.mymoney.android.home.fragments.records.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.home.repository.FinanceRepository
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.data.AvailableFilters
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.data.FilterTimePeriod
import com.mymoney.android.popUpFragments.recordsFilterBottomSheet.model.FilterType
import com.mymoney.android.roomDB.data.CategoryExpensePercentage
import com.mymoney.android.roomDB.data.CategoryExpenseSummary
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.roomDB.data.TransactionWithDetails
import com.mymoney.android.viewUtils.ViewUtils.formatWeekRange
import com.mymoney.android.viewUtils.ViewUtils.getFormattedDate
import com.mymoney.android.viewUtils.ViewUtils.getMonthAndYear
import com.mymoney.android.viewUtils.ViewUtils.getMonthFromDateString
import com.mymoney.android.viewUtils.ViewUtils.getYearFromDateString
import com.mymoney.android.viewUtils.ViewUtils.parseDate
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Locale

class RecordsViewModel(
    private val repo: TransactionRepository,
    private val financeRepository: FinanceRepository
) : ViewModel() {

    val totalIncome: LiveData<Double?> = financeRepository.getTotalIncome(TransactionType.INCOME.name)

    val totalExpense: LiveData<Double?> = financeRepository.getTotalExpense(TransactionType.EXPENSE.name)

    private val filterList = AvailableFilters.getAvailableFilters()


    val allRecords = MutableLiveData<List<TransactionWithDetails>>()
    private val _unfilteredRecords = mutableListOf<TransactionWithDetails>()
    val unfilteredRecords: List<TransactionWithDetails> get() = _unfilteredRecords


    private val _viewMode = MutableLiveData<String>()
    val viewMode : LiveData<String> get() = _viewMode

    private var selectedCategories = emptyList<String>()

    private var currentDate = LocalDate.now()
    private var currentMonth = currentDate.monthValue
    private var currentYear = currentDate.year
    private var lastSelectedViewMode: String = FilterTimePeriod.DAILY.name

    init {
        observeRecords()
    }

    private fun observeRecords() {
        repo.getAllTransactionsWithDetails().observeForever { response ->
            response?.let {
                val dateTimeFormatter =
                    SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())

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
                setFilterTimePeriod()
            }
        }
    }


    private fun applyFilters() {
        selectedCategories = filterList.getSubFilterNamesByType()
        val selectedViewMode = filterList.getSelectedViewMode()

        var filteredList = unfilteredRecords

        if (selectedCategories.isNotEmpty()) {
            filteredList = filteredList.filter { it.type in selectedCategories }
        }

        filteredList = when (selectedViewMode) {
            FilterTimePeriod.DAILY.name -> filterByDay(filteredList)
            FilterTimePeriod.WEEKLY.name -> filterByWeek(filteredList)
            FilterTimePeriod.MONTHLY.name -> filterByMonth(filteredList)
            FilterTimePeriod.YEAR.name -> filterByYear(filteredList)
            else -> filterByMonth(filteredList)
        }

        allRecords.postValue(filteredList)
    }

    private fun List<FilterType>.getSubFilterNamesByType(): List<String> {
        return this.firstOrNull { it.id == 1 }
            ?.subFilters
            ?.filter { it.isSelected }
            ?.map { it.name }
            ?: listOf()
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
        return records.filter {
            getMonthFromDateString(it.date) == currentMonth && getYearFromDateString(
                it.date
            ) == currentYear
        }
    }

    private fun filterByYear(records: List<TransactionWithDetails>): List<TransactionWithDetails> {
        return records.filter { getYearFromDateString(it.date) == currentYear }
    }


    private fun updateCurrentDateForViewMode(selectedViewMode: String) {
        when (lastSelectedViewMode) {
            FilterTimePeriod.DAILY.name -> {
                currentMonth = currentDate.monthValue
                currentYear = currentDate.year
            }
            FilterTimePeriod.WEEKLY.name -> {
                currentDate = currentDate.with(DayOfWeek.MONDAY)
                currentMonth = currentDate.monthValue
                currentYear = currentDate.year
            }
            FilterTimePeriod.MONTHLY.name -> {
                currentDate = LocalDate.of(currentYear, currentMonth, 1)
            }
            FilterTimePeriod.YEAR.name -> {
                currentDate = LocalDate.of(currentYear, 1, 1)
                currentMonth = 1
            }
        }
        lastSelectedViewMode = selectedViewMode
    }

    fun setFilterTimePeriod() {
        val selectedViewMode = filterList.getSelectedViewMode()

        updateCurrentDateForViewMode(selectedViewMode)

        val mode = when (selectedViewMode) {
            FilterTimePeriod.DAILY.name -> getFormattedDate(currentDate)
            FilterTimePeriod.WEEKLY.name -> formatWeekRange(
                currentDate.with(DayOfWeek.MONDAY),
                currentDate.with(DayOfWeek.SUNDAY)
            )
            FilterTimePeriod.MONTHLY.name -> getMonthAndYear(currentYear, currentMonth)
            FilterTimePeriod.YEAR.name -> currentYear.toString()
            else -> ""
        }
        _viewMode.postValue(mode)
        applyFilters()
        getExpenseAnalysisList()
    }

    fun previousDateWeekMonth() {
        val selectedViewMode = filterList.getSelectedViewMode()
        val mode = when (selectedViewMode) {
            FilterTimePeriod.DAILY.name -> {
                currentDate = currentDate.minusDays(1)
                getFormattedDate(currentDate)
            }
            FilterTimePeriod.WEEKLY.name -> {
                currentDate = currentDate.minusWeeks(1)
                formatWeekRange(
                    currentDate.with(DayOfWeek.MONDAY),
                    currentDate.with(DayOfWeek.SUNDAY)
                )
            }
            FilterTimePeriod.MONTHLY.name -> {
                adjustMonth(-1)
                getMonthAndYear(currentYear, currentMonth)
            }
            FilterTimePeriod.YEAR.name -> {
                currentYear -= 1
                currentYear.toString()
            }
            else -> ""
        }
        _viewMode.postValue(mode)
        applyFilters()
        getExpenseAnalysisList()
    }

    fun nextDateWeekMonth() {
        val selectedViewMode = filterList.getSelectedViewMode()
        val mode = when (selectedViewMode) {
            FilterTimePeriod.DAILY.name -> {
                currentDate = currentDate.plusDays(1)
                getFormattedDate(currentDate)
            }
            FilterTimePeriod.WEEKLY.name -> {
                currentDate = currentDate.plusWeeks(1)
                formatWeekRange(
                    currentDate.with(DayOfWeek.MONDAY),
                    currentDate.with(DayOfWeek.SUNDAY)
                )
            }
            FilterTimePeriod.MONTHLY.name -> {
                adjustMonth(1)
                getMonthAndYear(currentYear, currentMonth)
            }
            FilterTimePeriod.YEAR.name -> {
                currentYear += 1
                currentYear.toString()
            }
            else -> ""
        }
        _viewMode.postValue(mode)
        applyFilters()
        getExpenseAnalysisList()
    }

    private fun adjustMonth(step: Int) {
        currentMonth += step
        if (currentMonth > 12) {
            currentMonth = 1
            currentYear += 1
        } else if (currentMonth < 1) {
            currentMonth = 12
            currentYear -= 1
        }
    }


    val filteredAllTotalExpensesByCategory = MutableLiveData<List<CategoryExpensePercentage>>()

    private fun getExpenseAnalysisList() {
        Log.d("zzz", "getExpenseAnalysisList")

        val selectedViewMode = filterList.getSelectedViewMode()

        var filteredList = unfilteredRecords

        filteredList = filteredList.filter { it.type in TransactionType.EXPENSE.name }

        filteredList = when (selectedViewMode) {
            FilterTimePeriod.DAILY.name -> filterByDay(filteredList)
            FilterTimePeriod.WEEKLY.name -> filterByWeek(filteredList)
            FilterTimePeriod.MONTHLY.name -> filterByMonth(filteredList)
            FilterTimePeriod.YEAR.name -> filterByYear(filteredList)
            else -> filterByMonth(filteredList)
        }

        val categoryTotals = filteredList.groupBy { it.categoryName }.mapValues { entry ->
            entry.value.sumOf { it.amount }
        }

        val totalExpense = categoryTotals.values.sum()

        val categoryPercentageList = categoryTotals.map { (categoryName, totalAmount) ->
            val percentage = if (totalExpense > 0) (totalAmount / totalExpense) * 100 else 0.0
            val formattedPercentage = String.format("%.2f", percentage).toDouble()
            CategoryExpensePercentage(
                categoryName = categoryName ?: "Unknown",
                totalAmount = totalAmount,
                percentage = formattedPercentage,
                categoryIcon = filteredList.firstOrNull { it.categoryName == categoryName }?.categoryIcon
            )
        }.sortedByDescending { it.percentage }

        filteredAllTotalExpensesByCategory.postValue(categoryPercentageList)
    }

}

class RecordsViewModelProvider(
    private val repo: TransactionRepository,
    private val financeRepository: FinanceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordsViewModel(repo, financeRepository) as T
    }
}