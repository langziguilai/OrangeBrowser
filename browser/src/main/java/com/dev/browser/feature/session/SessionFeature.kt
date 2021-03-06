/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.session

import com.dev.base.support.BackHandler
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.EngineView
import com.dev.browser.session.SessionManager

/**
 * Feature implementation for connecting the engine module with the session module.
 */
class SessionFeature(
    private val sessionManager: SessionManager,
    private val sessionUseCases: SessionUseCases,
    engineView: EngineView,
    private val sessionId: String? = null
) : LifecycleAwareFeature, BackHandler {
    private val presenter = EngineViewPresenter(sessionManager, engineView, sessionId)

    /**
     * Start feature: App is in the foreground.
     */
    override fun start() {
        presenter.start()
    }

    /**
     * Handler for back pressed events in activities that use this feature.
     *
     * @return true if the event was handled, otherwise false.
     */
    override fun onBackPressed(): Boolean {
        val session = sessionId?.let {
            sessionManager.findSessionById(it)
        } ?: sessionManager.selectedSession

        if (session?.canGoBack == true) {
            sessionUseCases.goBack.invoke(session)
            return true
        }

        return false
    }

    /**
     * Stop feature: App is in the background.
     */
    override fun stop() {
        presenter.stop()
    }
}
