/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.session

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.EngineView
import com.dev.browser.extension.isOSOnLowMemory
import com.dev.browser.session.SelectionAwareSessionObserver
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Feature implementation for automatically taking thumbnails of sites.
 * The feature will take a screenshot when the page finishes loading,
 * and will add it to the [Session.themeThumbnail] property.
 *
 * If the OS is under low memory conditions, the screenshot will be not taken.
 * Ideally, this should be used in conjunction with [SessionManager.onLowMemory] to allow
 * free up some [Session.themeThumbnail] from memory.
 */
class ThemeBitmapCaptureFeature(
    private val context: Context,
    private val engineView: EngineView,
    private val sessionManager: SessionManager
) : LifecycleAwareFeature, CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val observer = ThemeBitmapCaptureFeatureRequestObserver(sessionManager)

    /**
     * Starts observing the selected session to listen for when a session finish loading.
     */
    override fun start() {
        observer.observeSelected()
    }

    /**
     * Stops observing the selected session.
     */
    override fun stop() {
        observer.stop()
        coroutineContext.cancelChildren()
    }

    internal inner class ThemeBitmapCaptureFeatureRequestObserver(
        sessionManager: SessionManager
    ) : SelectionAwareSessionObserver(sessionManager) {

        override fun onLoadingStateChanged(session: Session, loading: Boolean) {
            if (!loading) {
                //requestThemeBitmapScreenshot(session)
            }
        }
        private var captured=false
        override fun onProgress(session: Session, progress: Int) {
            //在progress大于70%时，capture图片
            if (progress>70){
                if (!captured){
                    requestThemeBitmapScreenshot(session)
                    captured=true
                }
            }else{
                captured=false
            }
        }
    }

    //请求获取themeBitmap截图，此截图在用后就可以删除了
    private fun requestThemeBitmapScreenshot(session: Session) {
        if (!isLowOnMemory()) {
            engineView.captureThumbnail {
                session.themeThumbnail = it
            }
        } else {
            session.themeThumbnail = null
        }
    }

    @VisibleForTesting
    internal var testLowMemory = false

    private fun isLowOnMemory() = testLowMemory || context.isOSOnLowMemory()
}
