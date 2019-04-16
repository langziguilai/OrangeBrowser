/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.database.history

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dev.browser.feature.sitepermissions.SitePermissions

/**
 * Internal entity representing a site permission as it gets saved to the database.
 */
@Entity(tableName = "visit_history")
internal data class VisitHistoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "date")
    var date: Long,
    @ColumnInfo(name = "visit_type")
    var visitType: Int
)
