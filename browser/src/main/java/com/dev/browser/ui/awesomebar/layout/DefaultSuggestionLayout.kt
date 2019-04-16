/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.ui.awesomebar.layout


import android.view.View
import androidx.annotation.LayoutRes
import com.dev.browser.concept.awesomebar.AwesomeBar
import com.dev.browser.ui.awesomebar.BrowserAwesomeBar

/**
 * Default implementation of [SuggestionLayout] to be used by [BrowserAwesomeBar].
 */
class DefaultSuggestionLayout : SuggestionLayout {
    override fun getLayoutResource(suggestion: AwesomeBar.Suggestion): Int {
        return if (suggestion.chips.isNotEmpty()) {
            DefaultSuggestionViewHolder.Chips.LAYOUT_ID
        } else {
            DefaultSuggestionViewHolder.Default.LAYOUT_ID
        }
    }

    override fun createViewHolder(
        awesomeBar: BrowserAwesomeBar,
        view: View,
        @LayoutRes layoutId: Int
    ): SuggestionViewHolder {
        return when (layoutId) {
            DefaultSuggestionViewHolder.Default.LAYOUT_ID ->
                DefaultSuggestionViewHolder.Default(awesomeBar, view)

            DefaultSuggestionViewHolder.Chips.LAYOUT_ID ->
                DefaultSuggestionViewHolder.Chips(awesomeBar, view)

            else -> throw IllegalArgumentException("Unknown layout: $layoutId")
        }
    }
}
