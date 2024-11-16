package com.mymoney.android.roomDB.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecordWithDetails(
    @Embedded val record: Record,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: Category?,
    @Relation(
        parentColumn = "account_id",
        entityColumn = "id"
    )
    val account: Account?
)
