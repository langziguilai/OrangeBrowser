/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.icons

import android.content.Context
import com.dev.browser.concept.Engine
import com.dev.browser.concept.webextension.WebExtension
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import com.dev.browser.icons.decoder.ICOIconDecoder
import com.dev.browser.icons.decoder.IconDecoder
import com.dev.browser.icons.generator.DefaultIconGenerator
import com.dev.browser.icons.generator.IconGenerator
import com.dev.browser.support.log.Logger
import java.util.concurrent.Executors

/**
 * Entry point for loading icons for websites.
 *
 * @param generator The [IconGenerator] to generate an icon if no icon could be loaded.
 * @param decoders List of [IconDecoder] instances to use when decoding a loaded icon into a [android.graphics.Bitmap].
 */
class BrowserIcons(
    private val context: Context,
    private val generator: IconGenerator = DefaultIconGenerator(context),
    private val decoders: List<IconDecoder> = listOf(
        ICOIconDecoder()
    ),
    jobDispatcher: CoroutineDispatcher = Executors.newFixedThreadPool(THREADS).asCoroutineDispatcher()
) {
    private val scope = CoroutineScope(jobDispatcher)

    /**
     * Asynchronously loads an [Icon] for the given [IconRequest].
     */
    fun loadIcon(request: IconRequest): Deferred<Icon> = scope.async {
        // For now we only generate an icon.
        generator.generate(context, request)
    }

    /**
     * Installs the "icons" extension in the engine in order to dynamically load icons for loaded websites.
     */
    fun install(engine: Engine) {
        engine.installWebExtension(
            WebExtension(
                id = "browser-icons",
                url = "resource://android/assets/extensions/browser-icons/"
            ),
            onSuccess = {
                Logger.debug("Installed browser-icons extension")
            },
            onError = { _, throwable ->
                Logger.error("Could not install browser-icons extension", throwable)
            })
    }

    companion object {
        // Number of worker threads we are using internally.
        private const val THREADS = 3

        private const val TARGET_SIZE_DP = 48
        private const val MAXIMUM_SIZE_DP = 64
    }
}
