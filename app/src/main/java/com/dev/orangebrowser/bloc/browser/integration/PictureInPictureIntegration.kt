package com.dev.orangebrowser.bloc.browser.integration

import android.app.Activity
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.feature.session.PictureInPictureFeature
import com.dev.browser.session.SelectionAwareSessionObserver
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager

class PictureInPictureIntegration(
    sessionManager: SessionManager,
    activity: Activity
) : LifecycleAwareFeature {
    private val pictureFeature = PictureInPictureFeature(sessionManager, activity)
    private val observer = PictureInPictureObserver(sessionManager) { whiteListed = it }
    private var whiteListed = false

    override fun start() {
        observer.observeSelected()
    }

    override fun stop() {
        observer.stop()
    }

    fun onHomePressed() = if (whiteListed) {
        pictureFeature.enterPipModeCompat()
    } else {
        pictureFeature.onHomePressed()
    }
}

internal class PictureInPictureObserver(
    sessionManager: SessionManager,
    private val whiteListed: (Boolean) -> Unit
) : SelectionAwareSessionObserver(sessionManager) {
    private val whiteList = listOf("youtube.com/tv")

    override fun onSessionSelected(session: Session) {
        super.onSessionSelected(session)
        whiteListed(isWhitelisted(session.url))
    }

    override fun onUrlChanged(session: Session, url: String) {
        whiteListed(isWhitelisted(session.url))
    }

    private fun isWhitelisted(url: String): Boolean {
        val exists = whiteList.firstOrNull { url.contains(it) }
        return exists != null
    }
}
