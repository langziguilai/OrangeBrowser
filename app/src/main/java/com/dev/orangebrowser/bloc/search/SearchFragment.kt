package com.dev.orangebrowser.bloc.search

import android.content.Context
import android.os.Bundle
import android.text.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.base.extension.hide
import com.dev.base.extension.hideKeyboard
import com.dev.base.extension.show
import com.dev.base.extension.updateIme
import com.dev.base.support.BackHandler
import com.dev.base.support.ViewBoundFeatureWrapper
import com.dev.browser.concept.fetch.Client
import com.dev.browser.concept.searchbar.SearchBar
import com.dev.browser.concept.storage.HistoryStorage
import com.dev.browser.domain.Domain
import com.dev.browser.domain.autocomplete.CustomDomainsProvider
import com.dev.browser.domain.autocomplete.ShippedDomainsProvider
import com.dev.browser.feature.awesomebar.AwesomeBarFeature
import com.dev.browser.feature.search.SearchUseCases
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.search.SearchEngine
import com.dev.browser.search.SearchEngineManager
import com.dev.browser.session.SessionManager
import com.dev.browser.ui.inlineautocomplete.InlineAutocompleteEditText
import com.dev.browser.ui.inlineautocomplete.OnFilterListener
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.data.dao.SearchHistoryItemDao
import com.dev.orangebrowser.databinding.FragmentSearchBinding
import com.dev.orangebrowser.extension.*
import com.dev.orangebrowser.utils.browser.SearchHistorySuggestionProvider
import com.dev.util.ColorKitUtil
import com.dev.view.NavigationBarUtil
import com.dev.view.StatusBarUtil
import javax.inject.Inject

class SearchFragment : BaseFragment(), SearchBar, BackHandler {


    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var searchEngineManager: SearchEngineManager
    @Inject
    lateinit var searchUseCases: SearchUseCases
    @Inject
    lateinit var tabsUseCases: TabsUseCases
    @Inject
    lateinit var sessionUseCases: SessionUseCases
    @Inject
    lateinit var client: Client
    @Inject
    lateinit var historyStorage: HistoryStorage
    @Inject
    lateinit var customDomainsProvider: CustomDomainsProvider
    @Inject
    lateinit var defaultDomainsProvider: ShippedDomainsProvider
    @Inject
    lateinit var searchHistoryItemDao: SearchHistoryItemDao
    private val originalSessionId: String
        get() = arguments?.getString(BrowserFragment.SESSION_ID) ?: ""

    private val primaryColor: Int
        get() = arguments?.getInt(COLOR_PRIMARY, activityViewModel.theme.value!!.colorPrimary)
            ?: activityViewModel.theme.value!!.colorPrimary
    private var lastUrl: String?=null
    private val awesomeBarFeature = ViewBoundFeatureWrapper<AwesomeBarFeature>()

    lateinit var viewModel: SearchViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentSearchBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(SearchViewModel::class.java)
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_search
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        super.onActivityCreated(savedInstanceState)
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    private fun updateStyle(color: Int) {
        binding.topBar.setBackgroundColor(color)
        binding.containerWrapper.setBackgroundColor(color)
        StatusBarUtil.setIconColor(requireActivity(), color)
        NavigationBarUtil.setNavigationBarColor(requireActivity(), color)
        //如果是白色背景
        if (ColorKitUtil.isBackGroundWhiteMode(color)) {
            binding.clear.setTextColor(getColor(R.color.colorBlack))
            binding.cancel.setTextColor(getColor(R.color.colorBlack))
            binding.go.setTextColor(getColor(R.color.colorBlack))
            binding.search.setTextColor(getColor(R.color.colorBlack))
            binding.searchText.setTextColor(getColor(R.color.colorBlack))
            binding.searchText.setHintTextColor(getColor(R.color.colorBlack))
            binding.searchText.autoCompleteBackgroundColor = getColor(R.color.color_AFAFAF)
        } else {
            binding.clear.setTextColor(getColor(R.color.colorWhite))
            binding.cancel.setTextColor(getColor(R.color.colorWhite))
            binding.go.setTextColor(getColor(R.color.colorWhite))
            binding.search.setTextColor(getColor(R.color.colorWhite))
            binding.searchText.setTextColor(getColor(R.color.colorWhite))
            binding.searchText.setHintTextColor(getColor(R.color.colorWhite))
        }
    }
    var currentSessionId:String?=null
    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        lastUrl=sessionManager.selectedSession?.url
        currentSessionId=sessionManager.selectedSession?.id
        //update style
        //如果是沉浸式风格，则随页面变换颜色
        if (getSpBool(R.string.pref_setting_user_immerse_browse_style, false)){
            updateStyle(primaryColor)
        }else{
            updateStyle(activityViewModel.theme.value!!.colorPrimary)
        }

        //设置跳转到本页面的时候就弹出键盘，并且光标闪烁
        binding.searchText.isFocusable = true
        binding.searchText.isFocusableInTouchMode = true
        binding.searchText.requestFocus()
        val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(binding.searchText, 0)
        sessionManager.findSessionById(originalSessionId)?.apply {

            if (!this.url.isBlank()) {
                binding.searchText.applyAutocompleteResult(
                    InlineAutocompleteEditText.AutocompleteResult(text = this.url, source = "", totalItems = 1)
                )
                binding.searchText.updateIme(EditorInfo.IME_ACTION_GO)
                binding.go.show()
                binding.search.hide()
                binding.cancel.hide()
            } else {
                binding.searchText.updateIme(EditorInfo.IME_ACTION_DONE)
                binding.go.hide()
                binding.search.hide()
                binding.cancel.show()
            }
        }
        toggleClearIcon()
        binding.clear.setOnClickListener {
            binding.searchText.setText("")
        }
        binding.cancel.setOnClickListener {
            RouterActivity?.loadHomeOrBrowserFragment(originalSessionId,lastUrl = lastUrl)
        }
        binding.go.setOnClickListener {
            //跳转
            if (binding.searchText.imeOptions == EditorInfo.IME_ACTION_GO) {
                sessionUseCases.loadUrl.invoke(getUrl(binding.searchText.text.toString()))
                RouterActivity?.apply {
                    this.loadBrowserFragment(originalSessionId,lastUrl = lastUrl)
                }
            }
        }
        binding.search.setOnClickListener {
            if (binding.searchText.imeOptions == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.saveSearchItem(binding.searchText.text.toString().trim(), Runnable {
                    searchUseCases.defaultSearch.invoke(binding.searchText.text.toString(), getSearchEngine())
                    RouterActivity?.apply {
                        this.loadBrowserFragment(originalSessionId,lastUrl = lastUrl)
                    }
                })
            }
        }
        binding.searchText.setOnCommitListener {
            //跳转
            if (binding.searchText.imeOptions == EditorInfo.IME_ACTION_GO) {
                sessionUseCases.loadUrl.invoke(getUrl(binding.searchText.text.toString()))
                RouterActivity?.apply {
                    this.loadBrowserFragment(originalSessionId,lastUrl = lastUrl)
                }
            }
            //搜索
            if (binding.searchText.imeOptions == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.saveSearchItem(binding.searchText.text.toString().trim(), Runnable {
                    searchUseCases.defaultSearch.invoke(binding.searchText.text.toString(), getSearchEngine())
                    RouterActivity?.apply {
                        this.loadBrowserFragment(originalSessionId,lastUrl = lastUrl)
                    }
                })
            }
        }
        binding.searchText.setOnFilterListener(object : OnFilterListener {
            override fun invoke(it: String) {
                val defaultSuggestion = defaultDomainsProvider.getAutocompleteSuggestion(it)
                if (defaultSuggestion != null) {
                    binding.searchText.applyAutocompleteResult(
                        InlineAutocompleteEditText.AutocompleteResult(
                            text = defaultSuggestion.text,
                            source = defaultSuggestion.source,
                            totalItems = defaultSuggestion.totalItems
                        )
                    )
                    return
                }
                val customSuggestion = customDomainsProvider.getAutocompleteSuggestion(it)
                if (customSuggestion != null) {
                    binding.searchText.applyAutocompleteResult(
                        InlineAutocompleteEditText.AutocompleteResult(
                            text = customSuggestion.text,
                            source = customSuggestion.source,
                            totalItems = customSuggestion.totalItems
                        )
                    )
                    return
                }
                binding.searchText.applyAutocompleteResult(
                    InlineAutocompleteEditText.AutocompleteResult(
                        text = it,
                        source = it,
                        totalItems = 0
                    )
                )
                return
            }

        })
        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                toggleClearIcon()
                mOnEditListener?.apply {
                    onTextChanged(binding.searchText.originalText)
                }
                updateViewByInput(binding.searchText.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
        awesomeBarFeature.set(
            owner = this,
            view = binding.root,
            feature = AwesomeBarFeature(
                awesomeBar = binding.awesomeBar,
                searchBar = this,
                onEditComplete = fun() {},
                onEditStart = fun() {},
                afterSuggestionClicked = ::loadCurrentSelectSession
            ).apply {
                addSearchProvider(
                    getSearchEngine(),
                    searchUseCases.defaultSearch,//跳转页面
                    client
                )
                addSessionProvider(
                    sessionManager,
                    tabsUseCases.selectTab
                ) //点击后，选中session
                addCustomProvider(SearchHistorySuggestionProvider(searchEngine = getSearchEngine(),
                    searchHistoryItemDao = searchHistoryItemDao,loadUrlUseCase = sessionUseCases.loadUrl))
                addHistoryProvider(
                    historyStorage,
                    sessionUseCases.loadUrl
                )  //点击后，//跳转页面
                addClipboardProvider(
                    requireContext(),
                    sessionUseCases.loadUrl
                ) //点击后，//跳转页面
            }
        )
    }

    //获取搜索引擎
    private fun getSearchEngine(): SearchEngine {
        return searchEngineManager.getDefaultSearchEngine(
            requireContext(),
            getSpString(R.string.pref_setting_search_engine_name, "")
        )
    }

    private fun updateViewByInput(input: String) {
        hideAllButton()
        if (input.isBlank()) {
            binding.searchText.updateIme(EditorInfo.IME_ACTION_DONE)
            binding.cancel.show()
            return
        }
        if (isUrl(input)) {
            binding.searchText.updateIme(EditorInfo.IME_ACTION_GO)
            binding.go.show()
            return
        }
        binding.search.show()
        binding.searchText.updateIme(EditorInfo.IME_ACTION_SEARCH)
    }

    private fun hideAllButton() {
        binding.search.hide()
        binding.cancel.hide()
        binding.go.hide()
    }

    //是否为URL
    private fun isUrl(value: String): Boolean {
        return value.matches(Regex("""^(https?://)?(www\.)?([\w-]+\.)+\w+(:\d+)?(/[\w-#_%&=?.*]*)?"""))
    }

    private fun getUrl(value: String): String {
        return Domain.create(value).url
    }

    private fun toggleClearIcon() {
        if (binding.searchText.text.isEmpty()) {
            binding.clear.visibility = View.GONE
        } else {
            binding.clear.visibility = View.VISIBLE
        }
    }

    private fun loadCurrentSelectSession() {
        sessionManager.selectedSession?.apply {
            if (currentSessionId!=null && this.id==currentSessionId){
                RouterActivity?.loadBrowserFragment(this.id,lastUrl = lastUrl)
            }else{
                RouterActivity?.loadBrowserFragment(this.id)
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    var mOnEditListener: SearchBar.OnEditListener? = null

    override fun setOnEditListener(listener: SearchBar.OnEditListener?) {
        mOnEditListener = listener
    }

    override fun onBackPressed(): Boolean {
        val session = sessionManager.findSessionById(originalSessionId)
        if (session == null) {
            RouterActivity?.loadHomeFragment(originalSessionId)
            return true
        }
        session.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id,lastUrl = lastUrl)
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        //再此处隐藏，不然，界面会跳动
        binding.searchText.hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.searchText.updateIme(EditorInfo.IME_ACTION_GO)
    }

    companion object {
        val Tag = "SearchFragment"
        const val COLOR_PRIMARY = "color_primary"
        const val LAST_URL = "last_url"
        fun newInstance(sessionId: String, color: Int? = null, lastUrl: String? = null) = SearchFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
                if (color != null) {
                    putInt(COLOR_PRIMARY, color)
                }
                if (lastUrl != null && lastUrl.isNotBlank()) {
                    putString(LAST_URL, lastUrl)
                }
            }
        }
    }
}
