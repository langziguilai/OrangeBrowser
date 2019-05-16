/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.bookmark

import androidx.room.*


/**
 * Internal dao for accessing and modifying sitePermissions in the database.
 */
@Dao
interface BookMarkDao {
    @Insert
    fun insert(entity: FavoriteLinkEntity): Long
    @Update
    fun update(entity: FavoriteLinkEntity)
//    获取所有的标签
    @Query("SELECT * FROM book_mark ORDER BY date ASC")
    fun getFavoriteLinkList(): List<FavoriteLinkEntity>
//通过category ID来查询标签
    @Query("SELECT * FROM book_mark WHERE category_id=:categoryId ORDER BY date  ASC")
    fun getFavoriteLinkListByCategoryId(categoryId:String): List<FavoriteLinkEntity>
    @Delete
    fun delete(entity: FavoriteLinkEntity)
}
