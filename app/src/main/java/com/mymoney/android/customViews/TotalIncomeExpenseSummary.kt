package com.mymoney.android.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.mymoney.android.R
import com.mymoney.android.databinding.IncomeExpenseSummaryBinding

class TotalIncomeExpenseSummary @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding: IncomeExpenseSummaryBinding = IncomeExpenseSummaryBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        if (!isInEditMode) {
            initTotalIncomeExpenseSummaryData()
        }
    }

    private fun initTotalIncomeExpenseSummaryData() {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        binding.section1.subtitleTv.text = "-₹0"
        binding.section2.subtitleTv.text = "₹0"
        binding.section1.subtitleTv.setTextColor(ContextCompat.getColor(context, R.color.red_400))
        binding.section3.root.visibility = GONE
    }

    fun setExpenseData(title: String, value: Double) {
        binding.section1.titleTv.text = title
        binding.section1.subtitleTv.text = "-₹$value"
        binding.section1.subtitleTv.setTextColor(ContextCompat.getColor(context, R.color.red_400))
    }

    fun setIncomeData(title: String, value: Double) {
        binding.section2.titleTv.text = title
        binding.section2.subtitleTv.text = "₹$value"
    }

    fun setThirdSectionData(title: String, value: Double) {
        binding.section3.titleTv.text = title
        binding.section3.subtitleTv.text = "₹$value"
        binding.section3.root.visibility = VISIBLE
    }
}
