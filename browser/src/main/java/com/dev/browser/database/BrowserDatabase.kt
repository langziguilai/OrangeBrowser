/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database


import android.content.Context
import androidx.room.*
import com.dev.browser.database.favoriate.FavoriteCategoryDao
import com.dev.browser.database.favoriate.FavoriteCategoryEntity
import com.dev.browser.database.favoriate.FavoriteLinkDao
import com.dev.browser.database.history.FavoriteLinkEntity
import com.dev.browser.database.history.VisitHistoryDao
import com.dev.browser.database.history.VisitHistoryEntity
import com.dev.browser.database.sitepermission.SitePermissionsDao
import com.dev.browser.database.sitepermission.SitePermissionsEntity
import com.dev.browser.feature.sitepermissions.SitePermissions

/**
 * Internal database for saving site permissions.
 */
@Database(entities = [SitePermissionsEntity::class,VisitHistoryEntity::class,FavoriteLinkEntity::class,FavoriteCategoryEntity::class], version = 1)
@TypeConverters(StatusConverter::class)
abstract class BrowserDatabase : RoomDatabase() {
    abstract fun sitePermissionsDao(): SitePermissionsDao
    abstract fun historyDao():VisitHistoryDao
    abstract fun favoriteLinkDao():FavoriteLinkDao
    abstract fun favoriteCategoryDao():FavoriteCategoryDao
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
