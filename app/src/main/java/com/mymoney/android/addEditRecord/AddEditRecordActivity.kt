package com.mymoney.android.addEditRecord

import android.content.Context
import android.os.Bundle
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
import com.mymoney.android.databinding.LeadingIconTitleSelectableViewBinding
import com.mymoney.android.databinding.LeadingIconWithTextBinding
import com.mymoney.android.databinding.LeadingIconWithTitleStrokeBgBinding
import com.mymoney.android.popUpFragments.accountsBottomSheet.AccountsBottomSheet
import com.mymoney.android.popUpFragments.categoriesBottomSheet.CategoriesBottomSheet
import com.mymoney.android.roomDB.daos.TransactionDao
import com.mymoney.android.roomDB.data.Transaction
import com.mymoney.android.roomDB.database.MyMoneyDatabase
import com.mymoney.android.viewUtils.ViewUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditRecordBinding
    private lateinit var viewModel: AddEditRecordViewModel
    private lateinit var repository: TransactionRepository
    private var selectedTransactionType: TransactionType? = null
    private lateinit var transactionDao: TransactionDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transactionDao = MyMoneyDatabase.getDatabase(this).transactionDao()
        repository = TransactionRepository(transactionDao)
        viewModel = ViewModelProvider(
            this,
            AddEditRecordViewModelProvider(repository!!)
        )[AddEditRecordViewModel::class.java]

        setUpObservers()
        setUpTopActions()
        setUpChipGroup()
        setUpDateAndTime()
        setUpSelectType()
    }

    private fun setUpObservers() {
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
                        ViewUtils.showToast(this, "$it Selected")
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
                        ViewUtils.showToast(this, "$it Selected")
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
                        ViewUtils.showToast(this, "$it Selected")
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
                    CategoriesBottomSheet() {
                        ViewUtils.showToast(this, "$it Selected")
                        binding.selectType2.tvSubtitle.text = it.name
                        viewModel.setPickedType2Value(it.id)
                    }.show(supportFragmentManager, "CategoriesBottomSheet")
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

        binding.chipsGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedChipId = checkedIds[0]
                selectedTransactionType = when (selectedChipId) {
                    binding.option1.id -> {
                        ViewUtils.showToast(this, "Income selected")
                        TransactionType.INCOME
                    }

                    binding.option2.id -> {
                        ViewUtils.showToast(this, "Expense selected")
                        TransactionType.EXPENSE
                    }

                    binding.option3.id -> {
                        ViewUtils.showToast(this, "Transfer selected")
                        TransactionType.TRANSFER
                    }

                    else -> null
                }
                selectedTransactionType?.let {
                    viewModel.setTransactionType(it)
                }
            } else {
                ViewUtils.showToast(this, "No option selected")
                selectedTransactionType = null
                viewModel.setTransactionType(TransactionType.EXPENSE)
            }
            setUpSelectType()
        }

        viewModel.setTransactionType(TransactionType.EXPENSE)

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
            date = viewModel.pickedDate.value,
            time = viewModel.pickedTime.value,
            amount = amount,
            type = transactionType.toString(),
            category_id = transactionIds.category_id,
            account_id = transactionIds.account_id,
            from_account_id = transactionIds.from_account_id,
            to_account_id = transactionIds.to_account_id,
            note = binding.edtNote.text.toString()
        )
    }

    private fun onSaveTransaction() {
        val transaction = createTransaction()
        if (transaction != null) {
            viewModel.saveTransaction(transaction)
            ViewUtils.showToast(this, "Transaction Saved!")
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
                    category_id = type1,
                    account_id = type2,
                    from_account_id = null,
                    to_account_id = null
                )
            }

            TransactionType.TRANSFER -> {
                TransactionIds(
                    category_id = null,
                    account_id = null,
                    from_account_id = type1,
                    to_account_id = type2
                )
            }
        }
    }

    data class TransactionIds(
        val category_id: Int?,
        val account_id: Int?,
        val from_account_id: Int?,
        val to_account_id: Int?
    )
}
