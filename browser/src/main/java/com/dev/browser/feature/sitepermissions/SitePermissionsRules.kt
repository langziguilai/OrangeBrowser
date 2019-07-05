/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.sitepermissions

import android.net.Uri
import com.dev.browser.concept.permission.Permission
import com.dev.browser.concept.permission.PermissionRequest
import com.dev.util.Keep

/**
 * Indicate how site permissions must behave by permission category.
 */
data class SitePermissionsRules(
    val camera: Action,
    val location: Action,
    val notification: Action,
    val microphone: Action,
    val exceptions: List<Uri>? = null
) {
    @Keep
    enum class Action {
        BLOCKED, ASK_TO_ALLOW;
    }

    internal fun getActionFrom(request: PermissionRequest): Action {
        return if (request.containsVideoAndAudioSources()) {
            getActionForCombinedPermission()
        } else {
            getActionForSinglePermission(request.permissions.first())
        }
    }

    internal fun isHostInExceptions(host: String): Boolean {
        if (exceptions == null || exceptions.isEmpty()) {
            return false
        }

        return exceptions.any {
            it.host == host
        }
    }

    private fun getActionForSinglePermission(permission: Permission): Action {
        return when (permission) {
            is Permission.ContentGeoLocation -> {
                location
            }
            is Permission.ContentNotification -> {
                notification
            }
            is Permission.ContentAudioCapture, is Permission.ContentAudioMicrophone -> {
                microphone
            }
            is Permission.ContentVideoCamera, is Permission.ContentVideoCapture -> {
                camera
            }
            else -> Action.ASK_TO_ALLOW
        }
    }

    private fun getActionForCombinedPermission(): Action {
        return if (camera == Action.BLOCKED || microphone == Action.BLOCKED) {
            Action.BLOCKED
        } else {
            Action.ASK_TO_ALLOW
        }
    }
}
