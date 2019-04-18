package com.dev.orangebrowser.bloc.search

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.style.BackgroundColorSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.base.extension.hide
import com.dev.base.extension.show
import com.dev.base.extension.updateIme
import com.dev.base.support.BackHandler
import com.dev.base.support.ViewBoundFeatureWrapper
import com.dev.browser.concept.fetch.Client
import com.dev.browser.concept.searchbar.SearchBar
import com.dev.browser.concept.storage.HistoryStorage
import com.dev.browser.domain.Domain
import com.dev.browser.domain.autocomplete.CustomDomainsProvider
import com.dev.browser.domain.autocomplete.DomainAutocompleteResult
import com.dev.browser.domain.autocomplete.ShippedDomainsProvider
import com.dev.browser.feature.awesomebar.AwesomeBarFeature
import com.dev.browser.feature.search.SearchUseCases
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.search.SearchEngine
import com.dev.browser.search.SearchEngineManager
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentSearchBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
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
    val originalSessionId: String
        get() = arguments?.getString(BrowserFragment.SESSION_ID) ?: ""


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

    private var inputText: String = ""
    private var isInSuggestMode: Boolean = false
    //设置文本
    fun setDirectText(input:String) {
        isInSuggestMode = false
        binding.searchTextDisplay.text=input
        binding.searchText.setSelection(binding.searchText.text.length)
        binding.searchText.isCursorVisible = true
    }
    //设置Spannable
    private fun setSpannableText(suggestion: DomainAutocompleteResult) {
        isInSuggestMode = true
        binding.searchTextDisplay.text = buildSuggestionString(suggestion)
        binding.searchText.isCursorVisible = false

    }

    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        //设置跳转到本页面的时候就弹出键盘，并且光标闪烁
        binding.searchText.isFocusable = true
        binding.searchText.isFocusableInTouchMode = true
        binding.searchText.requestFocus()
        val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(binding.searchText, 0)
//        初始化的时候有问题，暂时不添加这个功能
//        sessionManager.findSessionById(originalSessionId)?.apply {
//            binding.searchTextDisplay.text=buildSpannableString(this.url)
//            binding.searchText.setText(this.url)
//            binding.searchText.isCursorVisible=false
//        }
//        //
//        binding.searchText.setOnClickListener {
//            binding.searchText.isCursorVisible=true
//        }
        toggleClearIcon()
        binding.clear.setOnClickListener {
            binding.searchText.setText("")
            binding.searchTextDisplay.text = ""
        }
        binding.cancel.setOnClickListener {
            RouterActivity?.loadHomeOrBrowserFragment(originalSessionId)
        }
        binding.go.setOnClickListener {
            //跳转
            if (binding.searchText.imeOptions==EditorInfo.IME_ACTION_GO){
                sessionUseCases.loadUrl.invoke(getUrl(binding.searchTextDisplay.text.toString()))
                RouterActivity?.apply {
                    this.loadBrowserFragment(originalSessionId)
                }
            }
        }
        binding.search.setOnClickListener {
            if (binding.searchText.imeOptions==EditorInfo.IME_ACTION_SEARCH){
                searchUseCases.defaultSearch.invoke(binding.searchTextDisplay.text.toString(),getSearchEngine())
                RouterActivity?.apply {
                    this.loadBrowserFragment(originalSessionId)
                }
            }
        }
        binding.searchText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DEL) {
                    //跳出建议模式
                    if (isInSuggestMode) {
                        setDirectText(inputText)
                        return true
                    }
                    return false
                }
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER) {
                    //跳转
                    if (binding.searchText.imeOptions==EditorInfo.IME_ACTION_GO){
                        sessionUseCases.loadUrl.invoke(getUrl(binding.searchTextDisplay.text.toString()))
                        RouterActivity?.apply {
                            this.loadBrowserFragment(originalSessionId)
                        }
                        return true
                    }
                    //搜索
                    if (binding.searchText.imeOptions==EditorInfo.IME_ACTION_SEARCH){
                        searchUseCases.defaultSearch.invoke(binding.searchTextDisplay.text.toString(),getSearchEngine())
                        RouterActivity?.apply {
                            this.loadBrowserFragment(originalSessionId)
                        }
                        return true
                    }
                }
                return false
            }
        })
        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                toggleClearIcon()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    inputText = s.toString()
                    mOnEditListener?.apply {
                        onTextChanged(inputText)
                    }
                    updateSearchTextDisplay(inputText)
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
                    searchUseCases.defaultSearch,//点击后，新增一个Session
                    client
                )
                addSessionProvider(
                    sessionManager,
                    tabsUseCases.selectTab
                ) //点击后，选中session
                addHistoryProvider(
                    historyStorage,
                    sessionUseCases.loadUrl
                )  //点击后，新增一个Session
                addClipboardProvider(
                    requireContext(),
                    sessionUseCases.loadUrl
                ) //点击后，新增一个Session
            }
        )
    }
    fun updateSearchTextDisplay(input:String){
        if (!input.isBlank()){
            customDomainsProvider.getAutocompleteSuggestion(input)?.apply {
                isInSuggestMode = true
                setSpannableText(this)
                updateViewByInput(this.text)
                return
            }
            defaultDomainsProvider.getAutocompleteSuggestion(input)?.apply {
                isInSuggestMode = true
                setSpannableText(this)
                updateViewByInput(this.text)
                return
            }
        }
        setDirectText(input)
        updateViewByInput(input)
    }
    private fun buildSuggestionString(suggestion:DomainAutocompleteResult):Spannable?{
        val spannable=SpannableStringBuilder(suggestion.text)
        spannable.setSpan(BackgroundColorSpan(requireContext().resources.getColor(R.color.color_6C6C6C)),suggestion.input.length,suggestion.text.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return spannable
    }
    private fun buildSpannableString(str:String):Spannable?{
        val spannable=SpannableStringBuilder(str)
        spannable.setSpan(BackgroundColorSpan(requireContext().resources.getColor(R.color.color_6C6C6C)),0,str.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return spannable
    }
    //获取搜索引擎
    fun getSearchEngine():SearchEngine{
       return  searchEngineManager.getDefaultSearchEngine(requireContext())
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
        return Regex("""(https?://)?(www.)?(.+)+(\..+)+""").matches(value)
    }
    private fun getUrl(value:String):String{
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
            RouterActivity?.loadBrowserFragment(this.id)
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

    var mOnEditListener: SearchBar.OnEditListener? = null

    override fun setOnEditListener(listener: SearchBar.OnEditListener?) {
        mOnEditListener = listener
    }

    override fun onBackPressed(): Boolean {
        if (isInSuggestMode) {
            setDirectText(inputText)
            return true
        }
        val session = sessionManager.findSessionById(originalSessionId)
        if (session == null) {
            RouterActivity?.loadHomeFragment(originalSessionId)
            return true
        }
        session.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id)
        }
        return true
    }

    companion object {
        val Tag = "SearchFragment"
        fun newInstance(sessionId: String) = SearchFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }
}
