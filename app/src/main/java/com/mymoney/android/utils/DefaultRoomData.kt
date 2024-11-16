package com.mymoney.android.utils

import com.mymoney.android.roomDB.data.Account
import com.mymoney.android.roomDB.data.Category
import com.mymoney.android.roomDB.data.CategoryType

object DefaultAccounts {
    fun getAccounts() : List<Account> {
        return listOf(
            Account(name = "Cash", balance = 0.0),
            Account(name = "Card", balance = 0.0),
            Account(name = "Savings", balance = 0.0),
        )
    }
}


object DefaultCategories {

    fun getIncomeCategories() : List<Category> {
        return listOf(
            Category(
                name = "Awards",
                type = CategoryType.INCOME.name,
                icon = CategoryIcon.AWARDS.name
            ),
            Category(
                name = "Coupons",
                type = CategoryType.INCOME.name,
                icon = CategoryIcon.AWARDS.name
            ),
            Category(
                name = "Salary",
                type = CategoryType.INCOME.name,
                icon = CategoryIcon.SALARY.name
            ),
            Category(
                name = "Grants",
                type = CategoryType.INCOME.name,
                icon = CategoryIcon.GRANTS.name
            ),
            Category(
                name = "Lottery",
                type = CategoryType.INCOME.name,
                icon = CategoryIcon.LOTTERY.name
            ),
            Category(
                name = "Refunds",
                type = CategoryType.INCOME.name,
                icon = CategoryIcon.REFUNDS.name
            ),
            Category(
                name = "Rental",
                type = CategoryType.INCOME.name,
                icon = CategoryIcon.RENTAL.name
            ),
            Category(
                name = "Sale",
                type = CategoryType.INCOME.name,
                icon = CategoryIcon.SALE.name
            ),
        )
    }

    fun getExpenseCategories() : List<Category> {
        return listOf(
            Category(
                name = "Baby",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.BABY.name
            ),
            Category(
                name = "Beauty",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.BEAUTY.name
            ),
            Category(
                name = "Bills",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.BILLS.name
            ),
            Category(
                name = "Car",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.CAR.name
            ),
            Category(
                name = "Clothing",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.CLOTHING.name
            ),
            Category(
                name = "Education",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.EDUCATION.name
            ),
            Category(
                name = "Electronics",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.ELECTRONICS.name
            ),
            Category(
                name = "Food",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.FOOD.name
            ),
            Category(
                name = "Health",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.HEALTH.name
            ),
            Category(
                name = "Insurance",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.INSURANCE.name
            ),
            Category(
                name = "Shopping",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.SHOPPING.name
            ),
            Category(
                name = "Tax",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.TAX.name
            ),
            Category(
                name = "Transportation",
                type = CategoryType.EXPENSE.name,
                icon = CategoryIcon.TRANSPORTATION.name
            ),
        )
    }
}








