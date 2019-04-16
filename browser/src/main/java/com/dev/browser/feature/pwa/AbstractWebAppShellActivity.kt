/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dev.browser.feature.pwa

import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import com.dev.base.extension.enterToImmersiveMode
import com.dev.browser.concept.Engine
import com.dev.browser.concept.EngineView
import com.dev.browser.feature.pwa.ext.applyOrientation
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.browser.session.manifest.WebAppManifest

/**
 * Activity for "standalone" and "fullscreen" web applications.
 */
abstract class AbstractWebAppShellActivity : AppCompatActivity() {
    abstract val engine: Engine
    abstract val sessionManager: SessionManager

    lateinit var session: Session
    lateinit var engineView: EngineView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // We do not "install" web apps yet. So there's no place we can load a manifest from yet.
        // https://github.com/mozilla-mobile/android-components/issues/2382
        val manifest = createTestManifest()

        applyConfiguration(manifest)
        renderSession(manifest)
    }

    override fun onDestroy() {
        super.onDestroy()

        sessionManager
            .getOrCreateEngineSession(session)
            .close()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun applyConfiguration(manifest: WebAppManifest) {
        if (manifest.display == WebAppManifest.DisplayMode.FULLSCREEN) {
            enterToImmersiveMode()
        }

        applyOrientation(manifest)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun renderSession(manifest: WebAppManifest) {
        setContentView(engine
            .createView(this)
            .also { engineView = it }
            .asView())

        session = Session(manifest.startUrl)
        engineView.render(sessionManager.getOrCreateEngineSession(session))
    }

    companion object {
        const val INTENT_ACTION = "mozilla.components.feature.pwa.SHELL"
    }
}
