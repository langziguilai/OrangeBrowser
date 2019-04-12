/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature

import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.Engine
import com.dev.browser.concept.window.WindowRequest
import com.dev.browser.session.SelectionAwareSessionObserver
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager


/**
 * Feature implementation for handling window requests.
 */
class WindowFeature(private val engine: Engine, private val sessionManager: SessionManager,var windowFeatureListener: WindowFeatureListener?=null) : LifecycleAwareFeature {
    interface WindowFeatureListener{
        fun onOpenWindow(sessionAdd: Session)
        fun onCloseWindow(sessionRemove:Session)
    }
    internal val windowObserver = object : SelectionAwareSessionObserver(sessionManager) {
        override fun onOpenWindowRequested(session: Session, windowRequest: WindowRequest): Boolean {
            val newSession = Session(windowRequest.url, session.private)
            val newEngineSession = engine.createSession(session.private)
            windowRequest.prepare(newEngineSession)

            sessionManager.add(newSession, true, newEngineSession, parent = session)
            windowRequest.start()
            windowFeatureListener?.onOpenWindow(newSession)
            return true
        }

        override fun onCloseWindowRequested(session: Session, windowRequest: WindowRequest): Boolean {
            sessionManager.remove(session)
            windowFeatureListener?.onCloseWindow(session)
            return true
        }
    }

    /**
     * Starts the feature and a observer to listen for window requests.
     */
    override fun start() {
        windowObserver.observeSelected()
    }

    /**
     * Stops the feature and the window request observer.
     */
    override fun stop() {
        windowObserver.stop()
    }
}
