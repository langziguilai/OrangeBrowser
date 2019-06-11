package com.dev.browser.utils

import android.util.Base64

object Base64 {
    fun encodeToUriString(data: String) =
        "data:text/html;base64," + Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)
}