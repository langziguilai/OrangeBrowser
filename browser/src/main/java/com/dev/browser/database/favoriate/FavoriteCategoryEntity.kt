/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.favoriate

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dev.browser.feature.sitepermissions.SitePermissions

/**
 * 收藏夹的分类Dao.
 */
@Entity(tableName = "favorite_category")
data class FavoriteCategoryEntity(
    @ColumnInfo(name = "date")
    var date: Long,
    @PrimaryKey
    @ColumnInfo(name = "category_name")
    var categoryName: String
)
