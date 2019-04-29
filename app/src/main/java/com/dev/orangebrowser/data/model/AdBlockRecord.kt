package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * 最喜欢的网站
 */

@Entity(tableName="ad_block_record")
data class AdBlockRecord(
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "rule") var rule: String?, //规则
    @ColumnInfo(name = "count") var count: Int?//次数
)