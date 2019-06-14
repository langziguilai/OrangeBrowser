package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * 常用网站
 */

@Entity(tableName="common_site")
data class CommonSite(
    @PrimaryKey(autoGenerate = true) var uid: Int,
    @ColumnInfo(name = "url") var url: String?, //首页
    @ColumnInfo(name = "name") var name: String?,//名称
    @ColumnInfo(name = "description") var description: String?,//描述
    @ColumnInfo(name = "icon") var icon: String?, //icon
    @ColumnInfo(name = "category") var category: String?, //分类
    @ColumnInfo(name = "added") var added: Boolean=false //是否已经添加到主页
)