package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 *
 */

@Entity(tableName="site_category")
data class SiteCategory(
    @PrimaryKey(autoGenerate = true) var uid: Int,
    @ColumnInfo(name = "url") var url: String?, //首页
    @ColumnInfo(name = "name") var name: String?,//名称
    @ColumnInfo(name = "description") var description: String?,//描述
    @ColumnInfo(name = "icon") var icon: String?//icon
)