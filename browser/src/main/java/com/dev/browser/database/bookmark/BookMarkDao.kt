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
    fun insert(entity: BookMarkEntity): Long
    @Update
    fun update(entity: BookMarkEntity)
    //    获取所有的标签
    @Query("SELECT * FROM book_mark ORDER BY date ASC")
    fun getBookMarkList(): List<BookMarkEntity>
    //通过category ID来查询标签
    @Query("SELECT * FROM book_mark WHERE category_name=:category ORDER BY date  ASC")
    fun getBookMarkListByCategory(category:String): List<BookMarkEntity>
    @Delete
    fun delete(entity: BookMarkEntity)
    @Query("SELECT * FROM book_mark WHERE url=:url  ORDER BY date  ASC  LIMIT 1")
    fun getBookMarkByUrl(url:String): BookMarkEntity?
    @Query("UPDATE  book_mark set category_name=:newCategoryName WHERE category_name=:category")
    fun updateCategoryName(category:String,newCategoryName:String="")
}
