/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.awesomebar.provider

import com.dev.browser.concept.awesomebar.AwesomeBar
import com.dev.browser.feature.awesomebar.internal.loadLambda
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.icons.BrowserIcons
import com.dev.browser.session.SessionManager
import java.util.*

/**
 * A [AwesomeBar.SuggestionProvider] implementation that provides suggestions based on the sessions in the
 * [SessionManager] (Open tabs).
 */
class SessionSuggestionProvider(
    private val sessionManager: SessionManager,
    private val selectTabUseCase: TabsUseCases.SelectTabUseCase,
    private val icons: BrowserIcons? = null
) : AwesomeBar.SuggestionProvider {
    override val id: String = UUID.randomUUID().toString()

    override suspend fun onInputChanged(text: String): List<AwesomeBar.Suggestion> {
        if (text.isEmpty()) {
            return emptyList()
        }

        val suggestions = mutableListOf<AwesomeBar.Suggestion>()

        sessionManager.sessions.forEach { session ->
            if ((session.url.contains(text, ignoreCase = true) ||
                    session.title.contains(text, ignoreCase = true)) && !session.private
            ) {
                suggestions.add(
                    AwesomeBar.Suggestion(
                        provider = this,
                        id = session.id,
                        title = session.title,
                        description = session.url,
                        icon = icons.loadLambda(session.url),
                        onSuggestionClicked = { selectTabUseCase.invoke(session) }
                    )
                )
            }
        }

        return suggestions
    }
}
