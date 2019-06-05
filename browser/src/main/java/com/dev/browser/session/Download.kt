/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.session

import android.os.Environment

/**
 * Value type that represents a Download.
 *
 * @property url The full url to the mContent that should be downloaded.
 * @property fileName A canonical filename for this download.
 * @property contentType Content type (MIME type) to indicate the media type of the download.
 * @property contentLength The file size reported by the server.
 * @property userAgent The user agent to be used for the download.
 * @property destinationDirectory The matching destination directory for this type of download.
 */
data class Download(
    val url: String,
    var fileName: String,
    val referer:String?=null,
    val cookies:String?=null,
    val contentType: String? = null,
    val contentLength: Long? = null,
    val userAgent: String? = null,
    var destinationDirectory: String = Environment.DIRECTORY_DOWNLOADS
)
