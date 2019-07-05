package com.dev.browser.utils

import android.util.Base64
import com.dev.util.KeepMemberIfNecessary
import com.dev.util.KeepNameIfNecessary

@KeepNameIfNecessary
object Base64 {
    @KeepMemberIfNecessary
    fun encodeToUriString(data: String) =
        "data:text/html;base64," + Base64.encodeToString(data.toByteArray(), Base64.DEFAULT)
}