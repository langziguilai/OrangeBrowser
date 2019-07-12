package com.dev.orangebrowser.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName="image_mode_meta",indices = [Index(value=["unique_key"],unique = true)])
data class ImageModeMeta(
    @PrimaryKey(autoGenerate = true) var uid: Int=-1,
    @ColumnInfo(name = "site") var site:String="",
    @ColumnInfo(name = "image_attr") var imageAttr:String="abs:src",
    @ColumnInfo(name = "image_attr_title") var imageAttrTitle:String="",
    @ColumnInfo(name = "next_page_selector") var nextPageSelector:String="",
    @ColumnInfo(name = "next_page_selector_title") var nextPageSelectorTitle:String="",
    @ColumnInfo(name = "replace_nth_with_last") var replaceNthChildWithLastChild:Boolean=false,
    @ColumnInfo(name = "content_selector") var contentSelector:String="",
    @ColumnInfo(name = "unique_key") var uniqueKey:String=""){
    companion object{
        fun default():ImageModeMeta{
            return ImageModeMeta()
        }
    }
}