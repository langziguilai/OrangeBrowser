/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.awesomebar.internal

import android.graphics.Bitmap
import com.dev.browser.icons.BrowserIcons
import com.dev.browser.icons.IconRequest

/**
 * Helper that creates a suspendable lambda to either load a static bitmap ([default]) or from [BrowserIcons] if not
 * null.
 */
fun BrowserIcons?.loadLambda(
    url: String,
    default: Bitmap? = null
): (suspend (width: Int, height: Int) -> Bitmap?)? {
    if (default != null) {
        return { _, _ -> default }
    }

    if (this != null) {
        return { _, _ -> loadIcon(IconRequest(url)).await().bitmap }
    }

    return null
}
