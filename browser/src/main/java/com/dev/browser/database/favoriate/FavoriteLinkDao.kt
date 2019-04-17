/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.favoriate

import androidx.room.*
import com.dev.browser.database.history.FavoriteLinkEntity


/**
 * Internal dao for accessing and modifying sitePermissions in the database.
 */
@Dao
interface FavoriteLinkDao {
    @Insert
    fun insert(entity: FavoriteLinkEntity): Long
    @Update
    fun update(entity: FavoriteLinkEntity)
//    获取所有的收藏
    @Query("SELECT * FROM favorite_link ORDER BY date ASC")
    fun getFavoriteLinkList(): List<FavoriteLinkEntity>
//通过category ID来查询收藏
    @Query("SELECT * FROM favorite_link WHERE category_id=:categoryId ORDER BY date  ASC")
    fun getgetFavoriteLinkListByCategoryId(categoryId:String): List<FavoriteLinkEntity>
    @Delete
    fun delete(entity: FavoriteLinkEntity)
}
