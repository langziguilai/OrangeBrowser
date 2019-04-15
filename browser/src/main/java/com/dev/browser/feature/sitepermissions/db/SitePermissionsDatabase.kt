/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.sitepermissions.db


import android.content.Context
import androidx.room.*
import com.dev.browser.feature.sitepermissions.SitePermissions

/**
 * Internal database for saving site permissions.
 */
@Database(entities = [SitePermissionsEntity::class], version = 1)
@TypeConverters(StatusConverter::class)
internal abstract class SitePermissionsDatabase : RoomDatabase() {
    abstract fun sitePermissionsDao(): SitePermissionsDao

    companion object {
        @Volatile
        private var instance: SitePermissionsDatabase? = null

        @Synchronized
        fun get(context: Context): SitePermissionsDatabase {
            instance?.let { return it }

            return Room.databaseBuilder(
                context,
                SitePermissionsDatabase::class.java,
                "site_permissions_database"
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
