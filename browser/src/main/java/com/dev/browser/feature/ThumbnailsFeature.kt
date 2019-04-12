/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature

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
 * and will add it to the [Session.thumbnail] property.
 *
 * If the OS is under low memory conditions, the screenshot will be not taken.
 * Ideally, this should be used in conjunction with [SessionManager.onLowMemory] to allow
 * free up some [Session.thumbnail] from memory.
 */
class ThumbnailsFeature(
    private val context: Context,
    private val engineView: EngineView,
    sessionManager: SessionManager
) : LifecycleAwareFeature, CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val observer = ThumbnailsFeatureRequestObserver(sessionManager)

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

    internal inner class ThumbnailsFeatureRequestObserver(
        sessionManager: SessionManager
    ) : SelectionAwareSessionObserver(sessionManager) {

        override fun onLoadingStateChanged(session: Session, loading: Boolean) {
            if (!loading) {
                requestScreenshot(session)
            }
        }

//        override fun onProgress(session: Session, progress: Int) {
//            if (progress==100){
//
//            }
//        }
    }

    private fun requestScreenshot(session: Session) {
        if (!isLowOnMemory()) {
            engineView.captureThumbnail {
                //稍微延迟一点：等待初次渲染完成，但是如果用户开始的时候滑动太多
//                launch(Dispatchers.IO) {
//                    delay(200)
//                    launch(Dispatchers.Main) {
//                        session.thumbnail = it
//                    }
//                }
                session.thumbnail = it
            }
        } else {
            session.thumbnail = null
        }
    }

    @VisibleForTesting
    internal var testLowMemory = false

    private fun isLowOnMemory() = testLowMemory || context.isOSOnLowMemory()
}
