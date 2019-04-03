/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.base.extension

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

/**
 * The (visible) version name of the application, as specified by the <manifest> tag's versionName
 * attribute. E.g. "2.0".
 */
val Context.appVersionName: String?
    get() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    }

/**
 * Returns the handle to a system-level service by name.
 */
inline fun <reified T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

/**
 * Returns whether or not the operating system is under low memory conditions.
 */
fun Context.isOSOnLowMemory(): Boolean {
    val activityManager = systemService<ActivityManager>(Context.ACTIVITY_SERVICE)
    return ActivityManager.MemoryInfo().also { memoryInfo ->
        activityManager.getMemoryInfo(memoryInfo)
    }.lowMemory
}

/**
 * Returns if a list of permission have been granted, if all the permission have been granted
 * returns true otherwise false.
 */
fun Context.isPermissionGranted(vararg permission: String): Boolean {
    return permission.all {
        ContextCompat.checkSelfPermission(this, it) == PERMISSION_GRANTED
    }
}
fun Context.getPreferenceKey(@StringRes resourceId: Int): String =
    resources.getString(resourceId)