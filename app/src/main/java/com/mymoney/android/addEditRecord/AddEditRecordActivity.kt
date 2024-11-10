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
import com.mymoney.android.addEditRecord.viewmodel.AddEditRecordViewModel
import com.mymoney.android.roomDB.data.TransactionType
import com.mymoney.android.databinding.ActivityAddEditRecordBinding
import com.mymoney.android.databinding.LeadingIconTitleSelectableViewBinding
import com.mymoney.android.databinding.LeadingIconWithTextBinding
import com.mymoney.android.databinding.LeadingIconWithTitleStrokeBgBinding
import com.mymoney.android.viewUtils.ViewUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditRecordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditRecordBinding
    private lateinit var viewModel: AddEditRecordViewModel
    private var selectedTransactionType: TransactionType? = null
    private var pickedDate: String? = null
    private var pickedTime: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AddEditRecordViewModel::class.java]
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
            ViewUtils.showToast(this, "Transaction Saved!")
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

        when(selectedTransactionType){

            TransactionType.TRANSFER -> {
                bindSelectTypeOptions(
                    leadingIconTitleSelectableViewBinding = binding.selectType1,
                    iconRes = R.drawable.icon_wallet,
                    titleRes = "From Account",
                    subTitleRes = "Choose Account",
                    context = this
                ) {}

                bindSelectTypeOptions(
                    leadingIconTitleSelectableViewBinding = binding.selectType2,
                    iconRes = R.drawable.icon_wallet,
                    titleRes = "To Account",
                    subTitleRes = "Choose Account",
                    context = this
                ) {}
            }
            else -> {
                bindSelectTypeOptions(
                    leadingIconTitleSelectableViewBinding = binding.selectType1,
                    iconRes = R.drawable.icon_wallet,
                    titleRes = "Account",
                    subTitleRes = "Choose Account",
                    context = this
                ) {}

                bindSelectTypeOptions(
                    leadingIconTitleSelectableViewBinding = binding.selectType2,
                    iconRes = R.drawable.icon_tag,
                    titleRes = "Category",
                    subTitleRes = "Choose Category",
                    context = this
                ) {}
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
            } else {
                ViewUtils.showToast(this, "No option selected")
                selectedTransactionType = null
            }
            setUpSelectType()
        }

        selectedTransactionType = TransactionType.EXPENSE

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

}
