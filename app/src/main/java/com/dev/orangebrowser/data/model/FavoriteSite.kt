package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * 最喜欢的网站
 */

@Entity(tableName="favorite_site")
data class FavoriteSite(
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "url") var url: String?, //首页
    @ColumnInfo(name = "nameRes") var name: String?,//名称
    @ColumnInfo(name = "icon") var icon: String, //icon
    @ColumnInfo(name = "rank") var rank: Int? //rank
)