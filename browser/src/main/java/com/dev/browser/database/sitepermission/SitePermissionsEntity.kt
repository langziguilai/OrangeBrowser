/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.sitepermission

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dev.browser.feature.sitepermissions.SitePermissions

/**
 * Internal entity representing a site permission as it gets saved to the database.
 */
@Entity(tableName = "site_permissions")
data class SitePermissionsEntity(

    @PrimaryKey
    @ColumnInfo(name = "origin")
    var origin: String,

    @ColumnInfo(name = "location")
    var location: SitePermissions.Status,

    @ColumnInfo(name = "notification")
    var notification: SitePermissions.Status,

    @ColumnInfo(name = "microphone")
    var microphone: SitePermissions.Status,

    @ColumnInfo(name = "camera_back")
    var cameraBack: SitePermissions.Status,

    @ColumnInfo(name = "camera_front")
    var cameraFront: SitePermissions.Status,

    @ColumnInfo(name = "bluetooth")
    var bluetooth: SitePermissions.Status,

    @ColumnInfo(name = "local_storage")
    var localStorage: SitePermissions.Status,

    @ColumnInfo(name = "saved_at")
    var savedAt: Long
) {

    internal fun toSitePermission(): SitePermissions {
        return SitePermissions(
            origin,
            location,
            notification,
            microphone,
            cameraBack,
            cameraFront,
            bluetooth,
            localStorage,
            savedAt
        )
    }
}

internal fun SitePermissions.toSitePermissionsEntity(): SitePermissionsEntity {
    return SitePermissionsEntity(
        origin,
        location,
        notification,
        microphone,
        cameraBack,
        cameraFront,
        bluetooth,
        localStorage,
        savedAt
    )
}
