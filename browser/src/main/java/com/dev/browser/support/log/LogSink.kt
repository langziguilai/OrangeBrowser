package com.dev.browser.support.log

interface LogSink {
    fun log(
        priority: Log.Priority = Log.Priority.DEBUG,
        tag: String? = null,
        throwable: Throwable? = null,
        message: String? = null
    )
}