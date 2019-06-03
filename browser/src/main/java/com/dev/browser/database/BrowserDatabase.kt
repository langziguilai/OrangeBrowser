/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database


import android.content.Context
import androidx.room.*
import com.dev.browser.database.bookmark.BookMarkCategoryDao
import com.dev.browser.database.bookmark.BookMarkCategoryEntity
import com.dev.browser.database.bookmark.BookMarkDao
import com.dev.browser.database.bookmark.BookMarkEntity
import com.dev.browser.database.download.DownloadDao
import com.dev.browser.database.download.DownloadEntity
import com.dev.browser.database.history.VisitHistoryDao
import com.dev.browser.database.history.VisitHistoryEntity
import com.dev.browser.database.sitepermission.SitePermissionsDao
import com.dev.browser.database.sitepermission.SitePermissionsEntity
import com.dev.browser.feature.sitepermissions.SitePermissions

/**
 * Internal database for saving site permissions.
 */
@Database(entities = [SitePermissionsEntity::class,
    VisitHistoryEntity::class,
    DownloadEntity::class,
    BookMarkEntity::class,
    BookMarkCategoryEntity::class], version = 1,exportSchema = false)
@TypeConverters(StatusConverter::class)
abstract class BrowserDatabase : RoomDatabase() {
    abstract fun sitePermissionsDao(): SitePermissionsDao
    abstract fun historyDao():VisitHistoryDao
    abstract fun bookMarkDao():BookMarkDao
    abstract fun bookMarkCategoryDao():BookMarkCategoryDao
    abstract fun downloadDao():DownloadDao
    companion object {
        @Volatile
        private var instance: BrowserDatabase? = null

        @Synchronized
        fun get(context: Context): BrowserDatabase {
            instance?.let { return it }

            return Room.databaseBuilder(
                context.applicationContext,
                BrowserDatabase::class.java,
                "browser_database"
            ).build().also { instance = it }
        }
    }
}

@Suppress("unused")
internal class StatusConverter {
    private val statusArray = SitePermissions.Status.values()

    @TypeConverter
    fun toInt(status: SitePermissions.Status): Int {
        return status.id
    }

    @TypeConverter
    fun toStatus(index: Int): SitePermissions.Status? {
        return statusArray.find { it.id == index }
    }
}
