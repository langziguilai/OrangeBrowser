package com.dev.orangebrowser.bloc.browser.integration

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentManager
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.EngineView
import com.dev.browser.feature.contextmenu.ContextMenuCandidate
import com.dev.browser.feature.contextmenu.ContextMenuFeature
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.SessionManager
import kotlinx.android.synthetic.main.fragment_browser.view.*

class ContextMenuIntegration(
    context: Context,
    fragmentManager: FragmentManager,
    sessionManager: SessionManager,
    tabsUseCases: TabsUseCases,
    parentView: View,
    engineView: EngineView,
    sessionId: String? = null
) : LifecycleAwareFeature {
    private val feature = ContextMenuFeature(fragmentManager, sessionManager,
        ContextMenuCandidate.defaultCandidates(context, tabsUseCases, parentView),engineView, sessionId)

    override fun start() {
        feature.start()
    }

    override fun stop() {
        feature.stop()
    }
}
