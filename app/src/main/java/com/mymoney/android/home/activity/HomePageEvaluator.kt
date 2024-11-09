package com.mymoney.android.home.activity

import androidx.fragment.app.Fragment
import com.mymoney.android.home.fragments.analysis.AnalysisFragment
import com.mymoney.android.home.fragments.category.CategoryFragment
import com.mymoney.android.home.fragments.records.RecordsFragment
import com.piikeup.consumer.home.activity.TabItem

object HomePageEvaluator {
    private const val PAGE_TYPE_RECORDS = "Records"
    private const val PAGE_TYPE_BUDGETS = "Budgets"
    private const val PAGE_TYPE_ANALYSIS = "Analysis"
    private const val PAGE_TYPE_CATEGORY = "Category"

    fun getFragmentBasedOnPageType(tab: TabItem): Fragment {
        return when (tab.title) {
            PAGE_TYPE_RECORDS -> {
                RecordsFragment.newInstance()
            }

            PAGE_TYPE_BUDGETS -> {
                CategoryFragment.newInstance()
            }

            PAGE_TYPE_ANALYSIS -> {
                AnalysisFragment.newInstance()
            }

            PAGE_TYPE_CATEGORY -> {
                CategoryFragment.newInstance()
            }

            else -> {
                RecordsFragment.newInstance()
            }
        }
    }
}