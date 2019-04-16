package com.dev.base.extension

import android.net.Uri

/**
 * Returns the host without common prefixes like "www" or "m".
 */
@Suppress("MagicNumber")
val Uri.hostWithoutCommonPrefixes: String?
    get() {
        val host = host ?: return null

        return when {
            host.startsWith("www.") -> host.substring(4)
            host.startsWith("mobile.") -> host.substring(7)
            host.startsWith("m.") -> host.substring(2)
            else -> host
        }
    }