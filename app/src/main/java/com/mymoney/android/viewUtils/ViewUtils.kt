package com.mymoney.android.viewUtils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextPaint
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.piikeup.consumer.home.activity.TabItem
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Arrays
import java.util.Locale
import kotlin.math.roundToInt


object ViewUtils {

    fun parseDate(dateString: String?): LocalDate? {
        return try {
            val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val date = dateFormat.parse(dateString)
            date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        } catch (e: Exception) {
            null
        }
    }

    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun setUpStatusBar(activity: Activity, isDark: Boolean) {
        if (isDark) {
            removeLightStatusBar(activity)
        } else {
            addLightStatusBar(activity)
        }
    }

    fun removeLightStatusBar(activity: Activity) {
        val view = activity.window.decorView
        view.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun Context.getScreenWidth() = resources.displayMetrics.widthPixels
    fun Context.getScreenHeight() = resources.displayMetrics.heightPixels

    fun addLightStatusBar(activity: Activity) {
        val view = activity.window.decorView
        var flags = view.systemUiVisibility
        flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        view.systemUiVisibility = flags
    }

    fun setStatusBarColor(activity: Activity, color: Int) {
        try {
            activity.window?.statusBarColor = color
        } catch (e: Exception) {
            Log.e("TAG", "setting status bar color error")
        }
    }

    fun View.hideKeyboard(context: Context) {
        try {
            (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(
                this.rootView.windowToken,
                0
            )
        } catch (e: Exception) {
        }
    }

    fun View.showKeyboard(context: Context) {
        try {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
                this, InputMethodManager.SHOW_IMPLICIT
            )
        } catch (e: Exception) {
        }
    }


    fun dpToPx(dp: Float): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).roundToInt()
    }

    fun setUpTransparentStatusBar(activity: Activity) {
        try {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } catch (e: Exception) {

        }
    }

    fun setRoundedRectangleBackgroundDrawable(
        view: View, backgroundColor: Int, cornerRadius: Float
    ) {
        view.background = getRoundedRectangleBackgroundDrawable(view, backgroundColor, cornerRadius)
    }

    fun getRoundedRectangleBackgroundDrawable(
        view: View,
        backgroundColor: Int,
        cornerRadius: Float
    ): GradientDrawable {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.setColor(backgroundColor)
        view.background = shape
        if (cornerRadius > 0) {
            shape.cornerRadius = cornerRadius
        }
        return shape
    }

}

fun BottomNavigationView.setBottomNavWithViewPager(
    tabs: List<TabItem>?,
    viewPager: ViewPager?
) {
    menu.clear()
    tabs ?: return
    if (tabs.size <= 1) {
        visibility = View.GONE
    } else {
        View.VISIBLE
    }
    setOnNavigationItemSelectedListener { item ->
        val selectedPosition = item.itemId
        viewPager?.currentItem.let {
            if (it == selectedPosition) {
                return@setOnNavigationItemSelectedListener true
            }
        }
        viewPager?.setCurrentItem(selectedPosition, false)
        true
    }

    viewPager?.offscreenPageLimit = 3
    viewPager?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            selectedItemId = position
        }
    })

    tabs.forEachIndexed { index, tabItem ->
        menu.add(
            Menu.NONE,
            index,
            Menu.NONE,
            tabItem.title
        )?.icon = tabItem.icon!!
    }

    tabs.forEachIndexed { idx, it ->
        if (it.isSelected == true) {
            viewPager?.currentItem = idx
        }
    }

}
