/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.engine.system.window

import android.os.Message
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.dev.browser.concept.EngineSession
import com.dev.browser.concept.window.WindowRequest
import com.dev.browser.engine.system.SystemEngineSession

/**
 * WebView-based implementation of [WindowRequest].
 *
 * @property webView the WebView from which the request originated.
 * @property newWebView the WebView to use for opening a new window, may be null for close requests.
 * @property openAsDialog whether or not the window should be opened as a dialog, defaults to false.
 * @property triggeredByUser whether or not the request was triggered by the user, defaults to false.
 * @property resultMsg the message to send to the new WebView, may be null.
 */
class SystemWindowRequest(
    private val webView: WebView,
    val newWebView: WebView? = null,
    val openAsDialog: Boolean = false,
    val triggeredByUser: Boolean = false,
    private val resultMsg: Message? = null
) : WindowRequest {

    override val url: String = newWebView?.url ?: ""

    override fun prepare(engineSession: EngineSession) {
        newWebView?.let {
            (engineSession as SystemEngineSession).webView = it
        }
    }

    override fun start(callback:Runnable?) {
        val message = resultMsg
        val transport = message?.obj as? WebView.WebViewTransport
        transport?.let {
            it.webView = newWebView
            message.sendToTarget()
            //此时可以获取url了
            val webViewClient= object:WebViewClient(){
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    callback?.run()
                    return false
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    callback?.run()
                    return false
                }
            }
            it.webView.webViewClient=webViewClient
        }
    }
}
