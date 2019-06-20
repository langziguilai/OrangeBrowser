package com.dev.orangebrowser.utils.browser

import com.dev.browser.concept.awesomebar.AwesomeBar
import com.dev.browser.concept.storage.HistoryStorage
import com.dev.browser.concept.storage.SearchResult
import com.dev.browser.feature.awesomebar.internal.loadLambda
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.icons.BrowserIcons
import com.dev.browser.search.SearchEngine
import com.dev.orangebrowser.data.dao.SearchHistoryItemDao
import java.util.*

/**
 * A [AwesomeBar.SuggestionProvider] implementation that provides suggestions based on the browsing
 * history stored in the [HistoryStorage].
 */
const val SUGGESTION_LIMIT=20
class SearchHistorySuggestionProvider(
    private val searchEngine: SearchEngine,
    private val searchHistoryItemDao: SearchHistoryItemDao,
    private val loadUrlUseCase: SessionUseCases.LoadUrlUseCase,
    private val icons: BrowserIcons? = null
) : AwesomeBar.SuggestionProvider {

    override val id: String = UUID.randomUUID().toString()

    override suspend fun onInputChanged(text: String): List<AwesomeBar.Suggestion> {
        if (text.isEmpty()) {
            return emptyList()
        }
        return searchHistoryItemDao.getSearchHistoryByQuery(text,SUGGESTION_LIMIT).map {
            SearchResult(
                id = "search_history_item_"+it.searchItem,
                url = searchEngine.buildSearchUrl(searchTerm = it.searchItem),
                score = 2,
                title = it.searchItem
            )
        }.into()
    }

    override val shouldClearSuggestions: Boolean
        // We do not want the suggestion of this provider to disappear and re-appear when text changes.
        get() = false

    private fun Iterable<SearchResult>.into(): List<AwesomeBar.Suggestion> {
        return this.map {
            AwesomeBar.Suggestion(
                provider = this@SearchHistorySuggestionProvider,
                id = it.id,
                icon = icons.loadLambda(it.url),
                title = it.title,
                description = it.url,
                score = it.score,
                onSuggestionClicked = { loadUrlUseCase.invoke(it.url) }
            )
        }
    }
}