package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/***
 * 首页展示的网站
 */

@Entity(tableName="main_page_site")
data class MainPageSite(
    @PrimaryKey(autoGenerate = true) var uid: Int=0,
    @ColumnInfo(name = "url") var url: String?, //首页
    @ColumnInfo(name = "name") var name: String?,//名称
    @ColumnInfo(name = "description") var description: String?,//描述
    @ColumnInfo(name = "icon") var icon: String?, //icon
    @ColumnInfo(name = "background_color") var backgroundColor: String?="0xeeeeee",//背景颜色
    @ColumnInfo(name = "text_icon") var textIcon: String?="", //使用文字来代表Icon
    @ColumnInfo(name = "text_icon_size") var textIconSize: Int?=42, //文字大小
    @ColumnInfo(name = "rank") var rank: Int= Int.MAX_VALUE //rank:值越小，排名越高
)