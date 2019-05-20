/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.bookmark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Internal entity representing a site permission as it gets saved to the database
 * @Entity(primaryKeys = {"column1","column2","column3"})多个字段组合而成的主键
 */
@Entity(tableName = "book_mark")
data class BookMarkEntity(
    @PrimaryKey
    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "date")
    var date: Long,
    @ColumnInfo(name = "category_name")
    var categoryName: String
)
//通过categoryId过滤
fun filterFavoriteLinksByCategory(categoryName: String, data:List<BookMarkEntity>):List<BookMarkEntity>{
    return data.filter { it.categoryName==categoryName }.sortedWith(object:Comparator<BookMarkEntity>{
        override fun compare(o1: BookMarkEntity, o2: BookMarkEntity): Int {
           if (o1.date>o2.date)
               return 1
            if (o1.date==o2.date)
                return 0
            return -1
        }

    })
}