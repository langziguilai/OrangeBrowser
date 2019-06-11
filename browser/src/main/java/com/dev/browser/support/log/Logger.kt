package com.dev.browser.support.log

import android.os.SystemClock

/**
 * A wrapper for the <code>Log</code> object providing a more convenient API for logging.
 *
 * @param tag The tag to be used for log messages send via this logger.
 */
class Logger(
    private val tag: String? = null
) {
    /**
     * Send a DEBUG log message.
     */
    fun debug(message: String? = null, throwable: Throwable? = null) {
        Log.log(Log.Priority.DEBUG, tag = tag, message = message, throwable = throwable)
    }

    /**
     * Send a INFO log message.
     */
    fun info(message: String? = null, throwable: Throwable? = null) {
        Log.log(Log.Priority.INFO, tag = tag, message = message, throwable = throwable)
    }

    /**
     * Send a WARN log message.
     */
    fun warn(message: String? = null, throwable: Throwable? = null) {
        Log.log(Log.Priority.WARN, tag = tag, message = message, throwable = throwable)
    }

    /**
     * Send a ERROR log message.
     */
    fun error(message: String? = null, throwable: Throwable? = null) {
        Log.log(Log.Priority.ERROR, tag = tag, message = message, throwable = throwable)
    }

    /**
     * Measure the time it takes to execute the provided block and print a log message before and
     * after executing the block.
     *
     * Example log message:
     *   ⇢ doSomething()
     *   [..]
     *   ⇠ doSomething() [12ms]
     */
    fun measure(message: String, block: () -> Unit) {
        debug("⇢ $message")

        val start = SystemClock.elapsedRealtime()

        try {
            block()
        } finally {
            val took = SystemClock.elapsedRealtime() - start
            debug("⇠ $message [${took}ms]")
        }
    }

    companion object {
        private val DEFAULT = Logger()

        /**
         * Send a DEBUG log message.
         */
        fun debug(message: String? = null, throwable: Throwable? = null) = DEFAULT.debug(message, throwable)

        /**
         * Send a INFO log message.
         */
        fun info(message: String? = null, throwable: Throwable? = null) = DEFAULT.info(message, throwable)

        /**
         * Send a WARN log message.
         */
        fun warn(message: String? = null, throwable: Throwable? = null) = DEFAULT.warn(message, throwable)

        /**
         * Send a ERROR log message.
         */
        fun error(message: String? = null, throwable: Throwable? = null) = DEFAULT.error(message, throwable)

        /**
         * Measure the time it takes to execute the provided block and print a log message before and
         * after executing the block.
         *
         * Example log message:
         *   ⇢ doSomething()
         *   [..]
         *   ⇠ doSomething() [12ms]
         */
        fun measure(message: String, block: () -> Unit) {
            return DEFAULT.measure(message, block)
        }
    }
}
