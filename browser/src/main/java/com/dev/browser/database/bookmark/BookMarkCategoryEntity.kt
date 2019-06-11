/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.bookmark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 收藏夹的分类Dao.
 */
@Entity(tableName = "book_mark_category")
data class BookMarkCategoryEntity(
    @ColumnInfo(name = "date")
    var date: Long,
    @PrimaryKey
    @ColumnInfo(name = "category_name")
    var categoryName: String
)
