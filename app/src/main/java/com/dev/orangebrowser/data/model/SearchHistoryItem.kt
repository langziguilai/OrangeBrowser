package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/***
 * 最喜欢的网站
 */

@Entity(tableName="search_history_item",indices = [Index(value=["search_item"],unique = true)])
data class SearchHistoryItem(
    @PrimaryKey(autoGenerate = true) var uid: Int,
    @ColumnInfo(name = "search_item") var searchItem: String //首页
)