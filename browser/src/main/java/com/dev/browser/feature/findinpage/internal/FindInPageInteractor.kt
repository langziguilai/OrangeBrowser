/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.findinpage.internal

import com.dev.base.extension.hideKeyboard
import com.dev.browser.concept.EngineSession
import com.dev.browser.concept.EngineView
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.browser.feature.findinpage.FindInPageFeature
import com.dev.browser.feature.findinpage.view.FindInPageView

/**
 * Interactor that implements [FindInPageView.Listener] and notifies the engine or feature about actions the user
 * performed (e.g. "find next result").
 */
internal class FindInPageInteractor(
    private val feature: FindInPageFeature,
    private val sessionManager: SessionManager,
    private val view: FindInPageView,
    private val engineView: EngineView?
) : FindInPageView.Listener {
    private var engineSession: EngineSession? = null

    fun start() {
        view.listener = this
    }

    fun stop() {
        view.listener = null
    }

    fun bind(session: Session) {
        engineSession = sessionManager.getEngineSession(session)
    }

    override fun onPreviousResult() {
        engineSession?.findNext(forward = false)
        engineView?.asView()?.clearFocus()
        view.asView().hideKeyboard()
    }

    override fun onNextResult() {
        engineSession?.findNext(forward = true)
        engineView?.asView()?.clearFocus()
        view.asView().hideKeyboard()
    }

    override fun onClose() {
        // We pass this event up to the feature. The feature is responsible for unbinding its sub components and
        // potentially notifying other dependencies.
        feature.unbind()
    }

    fun unbind() {
        engineSession?.clearFindMatches()
        engineSession = null
    }

    override fun onFindAll(query: String) {
        engineSession?.findAll(query)
    }

    override fun onClearMatches() {
        engineSession?.clearFindMatches()
    }
}
