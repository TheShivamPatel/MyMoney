package com.mymoney.android.roomDB.data

import androidx.room.Embedded
import androidx.room.Relation

data class TransferRecordDetails(
    @Embedded val record: Record,
    @Relation(
        parentColumn = "from_account_id",
        entityColumn = "id"
    )
    val fromAccount: Account?,
    @Relation(
        parentColumn = "to_account_id",
        entityColumn = "id"
    )
    val toAccount: Account?
)
