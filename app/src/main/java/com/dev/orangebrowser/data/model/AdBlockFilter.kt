package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * AdBlock规则
 */

@Entity(tableName="ad_block_filter")
data class AdBlockFilter(
    @PrimaryKey(autoGenerate = true)
    var uid: Int=0,
    @ColumnInfo(name = "rule")
    var rule: String? //规则
)