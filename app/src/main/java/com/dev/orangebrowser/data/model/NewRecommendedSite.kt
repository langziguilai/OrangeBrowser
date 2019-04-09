package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * 新推荐网站
 */

@Entity(tableName="new_recommended_site")
data class NewRecommendedSite(
    @PrimaryKey var uid: Int,
    @ColumnInfo(name = "url") var url: String?, //首页
    @ColumnInfo(name = "nameRes") var name: String?,//名称
    @ColumnInfo(name = "icon") var icon: String?, //icon
    @ColumnInfo(name = "rank") var rank: Int?, //rank
    @ColumnInfo(name = "category") var category: String? //分类
)