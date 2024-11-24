package com.mymoney.android.addEditRecord

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.mymoney.android.R
import com.mymoney.android.addEditRecord.repository.TransactionRepository
import com.mymoney.android.addEditRecord.viewmodel.AddEditRecordViewModel
import com.mymoney.android.addEditRecord.viewmodel.AddEditRecordViewModelProvider
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.databinding.ActivityAddEditRecordBinding
import com.mymoney.android.databinding.ChipItemBinding
import com.mymoney.android.databinding.LeadingIconTitleSelectableViewBinding
import com.mymoney.android.databinding.LeadingIconWithTextBinding
import com.mymoney.android.databinding.LeadingIconWithTitleStrokeBgBinding
import com.mymoney.android.popUpFragments.accountsBottomSheet.AccountsBottomSheet
import com.mymoney.android.popUpFragments.categoriesBottomSheet.CategoriesBottomSheet
import com.mymoney.android.roomDB.daos.CategoryDao
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.Transaction
import com.mymoney.android.roomDB.database.MyMoneyDatabase
import com.mymoney.android.viewUtils.ViewUtils
import com.mymoney.android.viewUtils.ViewUtils.setStatusBarColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditRecordBinding
    private lateinit var viewModel: AddEditRecordViewModel
    private lateinit var repository: TransactionRepository
    private var selectedTransactionType: TransactionType = TransactionType.EXPENSE
    private lateinit var transactionDao: TransactionDao
    private var transactionId: Int? = null

    companion object {
        const val TRANSACTION_ID = "transaction_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactionDao = MyMoneyDatabase.getDatabase(this).transactionDao()
        repository = TransactionRepository(transactionDao)
        viewModel = ViewModelProvider(
            this,
            AddEditRecordViewModelProvider(repository)
        )[AddEditRecordViewModel::class.java]

        transactionId = intent.getIntExtra(TRANSACTION_ID, 0)

        if (transactionId != 0) {
            viewModel.loadTransaction(transactionId!!)
            binding.deleteImgBtn.visibility = View.VISIBLE
        }

        setUpOnClick()
        setUpStatusBar()
        setUpObservers()
        setUpTopActions()
        setUpChipGroup()
        setUpDateAndTime()
        setUpSelectType()
    }

    private fun setUpOnClick() {
        binding.deleteImgBtn.setOnClickListener {
            transactionId?.let { it1 ->
                viewModel.deleteRecord(
                    it1
                )
            }
            finish()
        }

    }

    private fun setUpStatusBar() {
        ViewUtils.addLightStatusBar(this@AddEditRecordActivity)
        setStatusBarColor(
            this@AddEditRecordActivity,
            ContextCompat.getColor(this@AddEditRecordActivity, R.color.green_500)
        )
    }

    private fun setUpObservers() {

        viewModel.transactionToEdit.observe(this, Observer { transaction ->
            transaction?.let {
                binding.edtAmount.setText(it.amount.toString())
                binding.edtNote.setText(it.note)
                viewModel.setPickedDate(it.date.toString())
                viewModel.setPickedTime(it.time.toString())
                viewModel.setTransactionType(TransactionType.valueOf(it.type))
                setUpChipGroup()
                viewModel.setPickedType1Value(it.from_account_id!!)
                fetchFromAccountNameAndUpdateUI(it.from_account_id)
                if (selectedTransactionType.name == TransactionType.TRANSFER.name) {
                    viewModel.setPickedType2Value(it.to_account_id!!)
                    fetchToAccountNameAndUpdateUI(it.to_account_id)
                } else {
                    viewModel.setPickedType2Value(it.category_id!!)
                    fetchCategoryNameAndUpdateUI(it.category_id)
                }
                binding.edtNote.setText(it.note)
            }
        })

        viewModel.pickedDate.observe(this, Observer { date ->
            binding.pickDateLayout.title.text = date
        })

        viewModel.pickedTime.observe(this, Observer { time ->
            binding.pickTimeLayout.title.text = time
        })

        viewModel.transactionType.observe(this, Observer { type ->
            selectedTransactionType = type
            setUpSelectType()
        })
    }

    private fun fetchFromAccountNameAndUpdateUI(accountId: Int) {
        viewModel.fetchAccountName(accountId) { accountName ->
            binding.selectType1.tvSubtitle.text = accountName
        }
    }

    private fun fetchToAccountNameAndUpdateUI(accountId: Int) {
        viewModel.fetchAccountName(accountId) { accountName ->
            binding.selectType2.tvSubtitle.text = accountName
        }
    }

    private fun fetchCategoryNameAndUpdateUI(categoryId: Int) {
        viewModel.fetchCategoryName(categoryId) { categoryName ->
            binding.selectType2.tvSubtitle.text = categoryName
        }
    }


    private fun setUpTopActions() {

        bindActionButtons(
            leadingIconWithTextBinding = binding.actionClose,
            textRes = "CANCEL",
            context = this,
            iconRes = R.drawable.ic_close,
        ) {
            finish()
        }

        bindActionButtons(
            leadingIconWithTextBinding = binding.actionSave,
            textRes = "SAVE",
            context = this,
            iconRes = R.drawable.icon_check,
        ) {
            onSaveTransaction()
        }

    }

    private fun setUpDateAndTime() {

        bindDateAndTimeSelector(
            dateTimeSelector = binding.pickDateLayout,
            textRes = viewModel.pickedDate.value.toString(),
            iconRes = R.drawable.icon_calendar,
            context = this
        ) {
            datePicker()
        }

        bindDateAndTimeSelector(
            dateTimeSelector = binding.pickTimeLayout,
            textRes = viewModel.pickedTime.value.toString(),
            iconRes = R.drawable.icon_clock,
            context = this
        ) {
            timePicker()
        }
    }

    private fun setUpSelectType() {

        when (selectedTransactionType) {

            TransactionType.TRANSFER -> {
                bindSelectTypeOptions(
                    leadingIconTitleSelectableViewBinding = binding.selectType1,
                    iconRes = R.drawable.icon_wallet,
                    titleRes = "From Account",
                    subTitleRes = "Choose Account",
                    context = this
                ) {
                    AccountsBottomSheet() {
                        viewModel.setPickedType1Value(it.id)
                        binding.selectType1.tvSubtitle.text = it.name
                    }.show(supportFragmentManager, "AccountsBottomSheet")
                }

                bindSelectTypeOptions(
                    leadingIconTitleSelectableViewBinding = binding.selectType2,
                    iconRes = R.drawable.icon_wallet,
                    titleRes = "To Account",
                    subTitleRes = "Choose Account",
                    context = this
                ) {
                    AccountsBottomSheet() {
                        binding.selectType2.tvSubtitle.text = it.name
                        viewModel.setPickedType2Value(it.id)
                    }.show(supportFragmentManager, "AccountsBottomSheet")
                }
            }

            else -> {
                bindSelectTypeOptions(
                    leadingIconTitleSelectableViewBinding = binding.selectType1,
                    iconRes = R.drawable.icon_wallet,
                    titleRes = "Account",
                    subTitleRes = "Choose Account",
                    context = this
                ) {
                    AccountsBottomSheet() {
                        binding.selectType1.tvSubtitle.text = it.name
                        viewModel.setPickedType1Value(it.id)
                    }.show(supportFragmentManager, "AccountsBottomSheet")
                }

                bindSelectTypeOptions(
                    leadingIconTitleSelectableViewBinding = binding.selectType2,
                    iconRes = R.drawable.icon_tag,
                    titleRes = "Category",
                    subTitleRes = "Choose Category",
                    context = this
                ) {
                    if (selectedTransactionType == TransactionType.EXPENSE) {
                        CategoriesBottomSheet(CategoriesBottomSheet.Companion.DialogType.TYPE_EXTEND_EXPENSE) {
                            binding.selectType2.tvSubtitle.text = it.name
                            viewModel.setPickedType2Value(it.id)
                        }.show(supportFragmentManager, "CategoriesBottomSheet")
                    } else {
                        CategoriesBottomSheet(CategoriesBottomSheet.Companion.DialogType.TYPE_SELECT_INCOME) {
                            binding.selectType2.tvSubtitle.text = it.name
                            viewModel.setPickedType2Value(it.id)
                        }.show(supportFragmentManager, "CategoriesBottomSheet")
                    }
                }
            }
        }
    }

    private fun bindActionButtons(
        leadingIconWithTextBinding: LeadingIconWithTextBinding,
        textRes: String,
        iconRes: Int,
        context: Context,
        onActionClick: () -> Unit
    ) {
        leadingIconWithTextBinding.apply {
            positiveImageBtn.setImageDrawable(ContextCompat.getDrawable(context, iconRes))
            tvAction.text = textRes
            root.setOnClickListener { onActionClick() }
        }
    }

    private fun bindDateAndTimeSelector(
        dateTimeSelector: LeadingIconWithTitleStrokeBgBinding,
        textRes: String,
        iconRes: Int,
        context: Context,
        onActionClick: () -> Unit
    ) {
        dateTimeSelector.apply {
            leadingIcon.setImageDrawable(ContextCompat.getDrawable(context, iconRes))
            title.text = textRes
            root.setOnClickListener { onActionClick() }
        }
    }

    private fun bindSelectTypeOptions(
        leadingIconTitleSelectableViewBinding: LeadingIconTitleSelectableViewBinding,
        titleRes: String,
        subTitleRes: String,
        iconRes: Int,
        context: Context,
        onActionClick: () -> Unit
    ) {
        leadingIconTitleSelectableViewBinding.apply {
            leadingImg.setImageDrawable(ContextCompat.getDrawable(context, iconRes))
            tvTitle.text = titleRes
            tvSubtitle.text = subTitleRes
            root.setOnClickListener { onActionClick() }
        }
    }

    private fun setUpChipGroup() {
        bindChipItem(binding.incomeChip, TransactionType.INCOME)
        bindChipItem(binding.expenseChip, TransactionType.EXPENSE)
        bindChipItem(binding.transferChip, TransactionType.TRANSFER)
    }

    private fun bindChipItem(
        chipItemBinding: ChipItemBinding,
        transactionType: TransactionType
    ) {
        chipItemBinding.tvFilterTitle.text = transactionType.name

        val isSelected = transactionType == selectedTransactionType
        handleChipStatus(chipItemBinding, isSelected)
        if (isSelected) {
            viewModel.setTransactionType(selectedTransactionType)
        }

        chipItemBinding.root.setOnClickListener {
            if (chipItemBinding.root.isSelected && isOnlyChipSelected()) {
                return@setOnClickListener
            }
            updateChipSelection(transactionType)
        }
    }

    private fun isOnlyChipSelected(): Boolean {
        return listOf(
            binding.incomeChip.root.isSelected,
            binding.expenseChip.root.isSelected,
            binding.transferChip.root.isSelected
        ).count { it } == 1
    }

    private fun updateChipSelection(selectedType: TransactionType) {
        handleChipStatus(binding.incomeChip, selectedType == TransactionType.INCOME)
        handleChipStatus(binding.expenseChip, selectedType == TransactionType.EXPENSE)
        handleChipStatus(binding.transferChip, selectedType == TransactionType.TRANSFER)

        viewModel.setTransactionType(selectedType)
    }

    private fun handleChipStatus(
        chipItemBinding: ChipItemBinding,
        isSelected: Boolean
    ) {
        chipItemBinding.root.isSelected = isSelected
        chipItemBinding.tvFilterTitle.setTextColor(
            if (isSelected) Color.WHITE else ContextCompat.getColor(
                binding.root.context,
                R.color.green_500
            )
        )
        chipItemBinding.root.setBackgroundResource(if (isSelected) R.drawable.rounded_primary_button_bg else R.drawable.rounded_primary_stroke_bg)
    }

    private fun datePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.show(supportFragmentManager, "DatePicker")

        datePicker.addOnPositiveButtonClickListener {
            val dateFormatter = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val date = dateFormatter.format(Date(it))
            viewModel.setPickedDate(date)
        }
    }

    private fun timePicker() {
        val timePicker = MaterialTimePicker.Builder().setTitleText("Select a time").build()
        timePicker.show(supportFragmentManager, "TIME_PICKER")

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            val formattedTime = timeFormatter.format(calendar.time)
            viewModel.setPickedTime(formattedTime)
        }
    }

    private fun createTransaction(): Transaction? {
        val transactionType = viewModel.transactionType.value
        val type1 = viewModel.selectedType1.value
        val type2 = viewModel.selectedType2.value
        val amount = binding.edtAmount.text.toString().toDoubleOrNull()

        if (amount == null) {
            ViewUtils.showToast(this, "Please enter a valid amount")
            return null
        }

        val transactionIds =
            getTransactionIds(transactionType ?: TransactionType.INCOME, type1, type2)

        if (transactionType == TransactionType.TRANSFER && transactionIds.from_account_id == transactionIds.to_account_id) {
            ViewUtils.showToast(
                this,
                "From account and To account cannot be the same for a transfer."
            )
            return null
        }

        return Transaction(
            id = transactionId ?: 0,
            date = viewModel.pickedDate.value,
            time = viewModel.pickedTime.value,
            amount = amount,
            type = transactionType.toString(),
            category_id = transactionIds.category_id,
            from_account_id = transactionIds.from_account_id,
            to_account_id = transactionIds.to_account_id,
            note = binding.edtNote.text.toString()
        )
    }


    private fun onSaveTransaction() {
        val transaction = createTransaction()
        if (transaction != null) {
            viewModel.saveTransaction(transaction, this)
        }
    }

    private fun getTransactionIds(
        transactionType: TransactionType,
        type1: Int?,
        type2: Int?
    ): TransactionIds {
        return when (transactionType) {
            TransactionType.INCOME, TransactionType.EXPENSE -> {
                TransactionIds(
                    from_account_id = type1,
                    category_id = type2,
                    to_account_id = null
                )
            }

            TransactionType.TRANSFER -> {
                TransactionIds(
                    from_account_id = type1,
                    category_id = null,
                    to_account_id = type2
                )
            }
        }
    }

    data class TransactionIds(
        val from_account_id: Int?,
        val category_id: Int?,
        val to_account_id: Int?
    )
}
