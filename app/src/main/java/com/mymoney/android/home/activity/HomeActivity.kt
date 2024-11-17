package com.mymoney.android.home.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.mymoney.android.R
import com.mymoney.android.databinding.ActivityHomeScreenBinding
import com.mymoney.android.roomDB.database.MyMoneyDatabase
import com.mymoney.android.viewUtils.ViewUtils
import com.mymoney.android.viewUtils.setBottomNavWithViewPager
import com.piikeup.consumer.home.activity.TabItem
import com.piikeup.consumer.home.activity.adapter.HomePageAdapter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding
    private var database: MyMoneyDatabase? = null
    private val adapter by lazy {
        HomePageAdapter(supportFragmentManager)
    }
    private var currentTabSelectedPosition = 0
    private val pageSelectedListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            currentTabSelectedPosition = position
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backPressed()
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRoomDB()
        setUpStatusBar()
        setUpTabs()
        setUpBottomNavigationMenu()
    }

    private fun setUpRoomDB() {
        database = MyMoneyDatabase.getDatabase(this)
    }

    private fun backPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 1) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
            }
        })
    }

    private fun setUpTabs() {
        binding.viewPager.adapter = adapter
    }

    private fun setUpBottomNavigationMenu() {
        val tabs: List<TabItem> = listOf(
            TabItem(
                "Records",
                ContextCompat.getDrawable(applicationContext, R.drawable.icon_transaction),
                true
            ),
            TabItem(
                "Analysis",
                ContextCompat.getDrawable(applicationContext, R.drawable.icon_pie_chart)
            ),
            TabItem(
                "Accounts",
                ContextCompat.getDrawable(applicationContext, R.drawable.icon_wallet)
            ),
            TabItem(
                "Category",
                ContextCompat.getDrawable(applicationContext, R.drawable.icon_tag)
            )
        )
        adapter.setItems(tabs)
        binding.bnv.setBottomNavWithViewPager(tabs, binding.viewPager)
        binding.viewPager.addOnPageChangeListener(pageSelectedListener)
    }

    private fun setUpStatusBar() {
        ViewUtils.setUpStatusBar(this, true)
        ViewUtils.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }
}