package com.mymoney.android.utils

import com.mymoney.android.R

//fun getDrawableIdForIcon(symbol: String): Int {
//    return when (symbol.uppercase()) {
//        "SALARY" -> R.drawable.vector_salary
//        "AWARDS" -> R.drawable.vector_award
//        "REFUNDS" -> R.drawable.vector_refunds
//        else -> R.drawable.vector_Sale
//    }
//}

enum class CategoryIcon(val drawableId: Int) {
    AWARDS(R.drawable.icon_tag),
    COUPONS(R.drawable.icon_tag),
    GRANTS(R.drawable.icon_wallet),
    LOTTERY(R.drawable.icon_clock),
    REFUNDS(R.drawable.icon_calendar),
    RENTAL(R.drawable.icon_calendar),
    SALARY(R.drawable.icon_calendar),
    SALE(R.drawable.icon_calendar),
    BABY(R.drawable.icon_tag),
    BEAUTY(R.drawable.icon_calendar),
    BILLS(R.drawable.icon_calendar),
    CAR(R.drawable.icon_calendar),
    CLOTHING(R.drawable.icon_calendar),
    EDUCATION(R.drawable.icon_calendar),
    ELECTRONICS(R.drawable.icon_calendar),
    ENTERTAINMENT(R.drawable.icon_calendar),
    FOOD(R.drawable.icon_calendar),
    HEALTH(R.drawable.icon_calendar),
    HOME(R.drawable.icon_calendar),
    INSURANCE(R.drawable.icon_calendar),
    SHOPPING(R.drawable.icon_calendar),
    TAX(R.drawable.icon_calendar),
    TRANSPORTATION(R.drawable.icon_calendar)
}
