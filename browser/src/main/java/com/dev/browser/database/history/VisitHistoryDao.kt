/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.history

import androidx.room.*


/**
 * Internal dao for accessing and modifying sitePermissions in the database.
 */
@Dao
internal interface VisitHistoryDao {

    @Insert
    fun insert(entity: VisitHistoryEntity): Long

    @Update
    fun update(entity: VisitHistoryEntity)

    @Query("SELECT * FROM visit_history ORDER BY date DESC")
    fun getVisitHistoryList(): List<VisitHistoryEntity>
    @Query("SELECT * FROM visit_history WHERE date<=:end AND date>=:start ORDER BY date DESC")
    fun getVisitHistoryListByRange(start:Long,end:Long): List<VisitHistoryEntity>
    @Query("SELECT * FROM visit_history WHERE url=:uri")
    fun getVisitHistoryByUri(uri:String): VisitHistoryEntity?
    @Query("SELECT * FROM visit_history WHERE url IN(:uris)")
    fun getVisitHistoryByUris(uris:List<String>): List<VisitHistoryEntity>
    @Query("SELECT * FROM visit_history WHERE title like :query ORDER BY date LIMIT :limit ")
    fun getVisitHistoryByQuery(query:String,limit:Int): List<VisitHistoryEntity>
    @Query("SELECT * FROM visit_history WHERE title like :query ORDER BY date LIMIT 1 ")
    fun getVisitHistoryByQueryOne(query:String): VisitHistoryEntity?
    @Delete
    fun deleteHistory(entity: VisitHistoryEntity)
}
