package com.dev.browser.feature.awesomebar


import android.content.Context
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.awesomebar.AwesomeBar
import com.dev.browser.concept.fetch.Client
import com.dev.browser.concept.searchbar.SearchBar
import com.dev.browser.concept.storage.HistoryStorage
import com.dev.browser.domain.autocomplete.CustomDomainsProvider
import com.dev.browser.domain.autocomplete.DomainAutocompleteProvider
import com.dev.browser.feature.awesomebar.provider.ClipboardSuggestionProvider
import com.dev.browser.feature.awesomebar.provider.HistoryStorageSuggestionProvider
import com.dev.browser.feature.awesomebar.provider.SearchSuggestionProvider
import com.dev.browser.feature.awesomebar.provider.SessionSuggestionProvider
import com.dev.browser.feature.search.SearchUseCases
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.search.SearchEngine
import com.dev.browser.session.SessionManager

/**
 * Connects an [AwesomeBar] with a [Toolbar] and allows adding multiple [AwesomeBar.SuggestionProvider] implementations.
 */
class AwesomeBarFeature(
    private val awesomeBar: AwesomeBar,
    private val searchBar: SearchBar,
    private val onEditStart: (() -> Unit)? = null,
    private val onEditComplete: (() -> Unit)? = null,
    private val afterSuggestionClicked:(()->Unit)? =null
):LifecycleAwareFeature {


    init {
        searchBar.setOnEditListener(object : SearchBar.OnEditListener {
            override fun onTextChanged(text: String) = awesomeBar.onInputChanged(text)

            override fun onStartEditing() {
                onEditStart?.invoke()
                awesomeBar.onInputStarted()
            }

            override fun onStopEditing() {
                onEditComplete?.invoke()
                awesomeBar.onInputCancelled()
            }
        })

        //在点击Suggestion ViewHolder之后触发,即先添加session再选中session或直接选中session，然后我们通过
        //sessionManager获取当前选中的Session，然后进行跳转
        awesomeBar.setAfterSuggestionClickedListener{
            afterSuggestionClicked?.invoke()
        }
    }

    /**
     * Add a [AwesomeBar.SuggestionProvider] for "Open tabs" to the [AwesomeBar].
     */
    fun addSessionProvider(
        sessionManager: SessionManager,
        selectTabUseCase: TabsUseCases.SelectTabUseCase
    ): AwesomeBarFeature {
        val provider = SessionSuggestionProvider(sessionManager, selectTabUseCase)
        awesomeBar.addProviders(provider)
        return this
    }

    /**
     * Add a [AwesomeBar.SuggestionProvider] for search engine suggestions to the [AwesomeBar].
     */
    fun addSearchProvider(
        searchEngine: SearchEngine,
        searchUseCase: SearchUseCases.SearchUseCase,
        fetchClient: Client,
        mode: SearchSuggestionProvider.Mode = SearchSuggestionProvider.Mode.SINGLE_SUGGESTION
    ): AwesomeBarFeature {
        awesomeBar.addProviders(SearchSuggestionProvider(searchEngine, searchUseCase, fetchClient, mode=mode))
        return this
    }

    /**
     * Add a [AwesomeBar.SuggestionProvider] for browsing history to the [AwesomeBar].
     */
    fun addHistoryProvider(
        historyStorage: HistoryStorage,
        loadUrlUseCase: SessionUseCases.LoadUrlUseCase
    ): AwesomeBarFeature {
        awesomeBar.addProviders(HistoryStorageSuggestionProvider(historyStorage, loadUrlUseCase))
        return this
    }
    /**
     * Add a [AwesomeBar.SuggestionProvider] for clipboard to the [AwesomeBar].
     */
    fun addClipboardProvider(
        context: Context,
        loadUrlUseCase: SessionUseCases.LoadUrlUseCase
    ): AwesomeBarFeature {
        awesomeBar.addProviders(ClipboardSuggestionProvider(context, loadUrlUseCase))
        return this
    }

    override fun start() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        searchBar.setOnEditListener(null)
    }
}
