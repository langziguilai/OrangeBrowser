package com.dev.orangebrowser.bloc.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.base.support.BackHandler
import com.dev.base.support.ViewBoundFeatureWrapper
import com.dev.browser.concept.fetch.Client
import com.dev.browser.concept.searchbar.SearchBar
import com.dev.browser.concept.storage.HistoryStorage
import com.dev.browser.feature.awesomebar.AwesomeBarFeature
import com.dev.browser.feature.search.SearchUseCases
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.feature.tabs.TabsUseCases
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

    fun exitSuggestMode() {
        isInSuggestMode = false
        binding.searchText.setText(inputText)
        binding.searchText.setSelection(binding.searchText.text.length)
        binding.searchText.isCursorVisible = true
    }

    fun enterSuggestMode(suggestion: String) {
        isInSuggestMode = true
        binding.searchText.setText(suggestion)
        binding.searchText.setSelection(suggestion.length)
        binding.searchText.isCursorVisible = false

    }

    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        //设置跳转到本页面的时候就弹出键盘，并且光标闪烁
        binding.searchText.isFocusable = true
        binding.searchText.isFocusableInTouchMode = true
        binding.searchText.requestFocus()
        val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(binding.searchText, 0)
        //
        val session = sessionManager.findSessionById(originalSessionId)
        session?.apply {
            binding.searchText.setText(this.url)
            binding.searchText.setSelection(binding.searchText.text.length)
        }
        toggleClearIcon()
        binding.clear.setOnClickListener {
            binding.searchText.setText("")
        }
        binding.cancel.setOnClickListener {
            RouterActivity?.loadHomeOrBrowserFragment(originalSessionId)
        }
        binding.searchText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DEL) {
                    //跳出建议模式
                    if (isInSuggestMode) {
                        exitSuggestMode()
                        return true
                    }
                    return false
                }
                return false
            }
        })
        binding.searchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                toggleClearIcon()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.apply {
                    if (!isInSuggestMode) {
                        inputText = this.toString()
                        mOnEditListener?.apply {
                            onTextChanged(inputText)
                        }
                    }
                }
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
                    searchEngineManager.getDefaultSearchEngine(requireContext()),
                    searchUseCases.newTabSearch,//点击后，新增一个Session
                    client
                )
                addSessionProvider(
                    sessionManager,
                    tabsUseCases.selectTab
                ) //点击后，选中session
                addHistoryProvider(
                    historyStorage,
                    tabsUseCases.addTab
                )  //点击后，新增一个Session
                addClipboardProvider(
                    requireContext(),
                    tabsUseCases.addTab
                ) //点击后，新增一个Session
            }
        )
    }
    private fun toggleClearIcon(){
        if (binding.searchText.text.isEmpty()){
            binding.clear.visibility=View.GONE
        }else{
            binding.clear.visibility=View.VISIBLE
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
            exitSuggestMode()
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
