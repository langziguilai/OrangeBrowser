/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.orangebrowser.bloc.browser.integration

import android.view.View
import com.dev.base.support.BackHandler
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.EngineView
import com.dev.browser.feature.findinpage.FindInPageFeature
import com.dev.browser.feature.findinpage.view.FindInPageView
import com.dev.browser.session.SessionManager


class FindInPageIntegration(
    private val sessionManager: SessionManager,
    engineView: EngineView,
    private val view: FindInPageView
) : LifecycleAwareFeature, BackHandler {
    private val feature = FindInPageFeature(sessionManager, view,engineView, ::onClose)

    override fun start() {
        feature.start()

        FindInPageIntegration.launch = this::launch
    }

    override fun stop() {
        feature.stop()

        FindInPageIntegration.launch = null
    }

    override fun onBackPressed(): Boolean {
        return feature.onBackPressed()
    }

    private fun onClose() {
        view.asView().visibility = View.GONE
    }

    fun launch() {
        val session = sessionManager.selectedSession ?: return

        view.asView().visibility = View.VISIBLE
        feature.bind(session)
    }

    companion object {
        // This is a workaround to let the menu parentView find this integration and active "Find in Page" mode. That's a bit
        // ridiculous and there's no need that we create the toolbar menu items at app start time. Instead the
        // ToolbarIntegration should create them and get the FindInPageIntegration injected as a dependency if the
        // menu items need them.
        var launch: (() -> Unit)? = null
            private set
    }
}
