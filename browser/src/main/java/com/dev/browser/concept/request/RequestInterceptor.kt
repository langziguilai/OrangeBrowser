/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.concept.request

import com.dev.browser.concept.EngineSession
import com.dev.browser.support.ErrorType


/**
 * Interface for classes that want to intercept load requests to allow custom behavior.
 */
interface RequestInterceptor {
    /**
     * An alternative response for an intercepted request.
     */
    sealed class InterceptionResponse {
        data class Content(
            val data: String,
            val mimeType: String = "text/html",
            val encoding: String = "UTF-8"
        ) : InterceptionResponse()

        data class Url(val url: String) : InterceptionResponse()
    }

    /**
     * An alternative response for an error request.
     */
    data class ErrorResponse(
        val data: String,
        val url: String? = null,
        val mimeType: String = "text/html",
        val encoding: String = "UTF-8"
    )

    /**
     * A request to open an URI. This is called before each page load to allow
     * providing custom behavior.
     *
     * @param session The engine session that initiated the callback.
     * @return An [InterceptionResponse] object containing alternative mContent
     * or an alternative URL. Null if the original request should continue to
     * be loaded.
     */
    fun onLoadRequest(session: EngineSession, uri: String): InterceptionResponse? = null

    /**
     * A request that the engine wasn't able to handle that resulted in an error.
     *
     * @param session The engine session that initiated the callback.
     * @param errorType The error that was provided by the engine related to the
     * type of error caused.
     * @param uri The uri that resulted in the error.
     * @return An [ErrorResponse] object containing mContent to display for the
     * provided error type.
     */
    fun onErrorRequest(session: EngineSession, errorType: ErrorType, uri: String?): ErrorResponse? = null
}
