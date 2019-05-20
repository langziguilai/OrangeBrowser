/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.bookmark

import androidx.room.*


/**
 * 收藏夹的分类Dao.
 */
@Dao
interface BookMarkCategoryDao {
    @Insert
    fun insert(entity: BookMarkCategoryEntity): Long
    @Update
    fun update(entity: BookMarkCategoryEntity)
    @Query("SELECT * FROM book_mark_category ORDER BY date DESC")
    fun getCategoryList(): List<BookMarkCategoryEntity>
    @Delete
    fun deleteCategory(entity: BookMarkCategoryEntity)
    @Query("UPDATE book_mark_category SET category_name=:newCategory WHERE category_name=:category")
    fun updateCategoryName(category:String,newCategory:String)
}
