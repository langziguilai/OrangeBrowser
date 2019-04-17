/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dev.browser.feature.sitepermissions.SitePermissions

/**
 * Internal entity representing a site permission as it gets saved to the database.
 */
@Entity(tableName = "favorite_link")
data class FavoriteLinkEntity(
    @PrimaryKey
    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "date")
    var date: Long,
    @ColumnInfo(name = "category_id")
    var categoryId: Int,
    @ColumnInfo(name = "category_name")
    var categoryName: String
)
//通过categoryId过滤
fun filterFavoriteLinksByCategoryId(categoryId: Int,data:List<FavoriteLinkEntity>):List<FavoriteLinkEntity>{
    return data.filter { it.categoryId==categoryId }.sortedWith(object:Comparator<FavoriteLinkEntity>{
        override fun compare(o1: FavoriteLinkEntity, o2: FavoriteLinkEntity): Int {
           if (o1.date>o2.date)
               return 1
            if (o1.date==o2.date)
                return 0
            return -1
        }

    })
}