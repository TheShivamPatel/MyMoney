package com.piikeup.consumer.home.activity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.mymoney.android.home.activity.HomePageEvaluator
import com.piikeup.consumer.home.activity.TabItem

class HomePageAdapter(supportFragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val items: MutableList<TabItem> = mutableListOf()

    override fun getCount(): Int {
        return items.size
    }

    fun setItems(list: List<TabItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return HomePageEvaluator.getFragmentBasedOnPageType(items[position])
    }
}