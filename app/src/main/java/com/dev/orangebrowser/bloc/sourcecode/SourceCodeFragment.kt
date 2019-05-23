package com.dev.orangebrowser.bloc.sourcecode

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AbsSeekBar
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.text.htmlEncode
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.base.extension.show
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.browser.feature.findinpage.view.FindInPageBar
import com.dev.browser.session.SessionManager
import com.dev.browser.utils.WebviewUtils
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.utils.html2article.ContentExtractor
import com.dev.util.StringUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class SourceCodeFragment : BaseFragment(), BackHandler {


    companion object {
        val Tag="SourceCodeFragment"
        fun newInstance(sessionId:String) = SourceCodeFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }
    override fun onBackPressed(): Boolean {
        val session = sessionManager.findSessionById(arguments?.getString(BrowserFragment.SESSION_ID) ?: "")
        if (session == null) {
            RouterActivity?.loadHomeOrBrowserFragment(sessionManager.selectedSession?.id ?: "")
        } else {
            RouterActivity?.loadHomeOrBrowserFragment(session.id)
        }
        return true
    }
    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var viewModel: SourceCodeViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var container: FrameLayout
    lateinit var titleTextView: TextView
    lateinit var backBtn: TextView
    lateinit var menuBtn: TextView
    lateinit var header: View
    lateinit var findInPageBar: FindInPageBar
    var webView: WebView? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(SourceCodeViewModel::class.java)
    }
    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_source_code
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
    }
    override fun initView(view: View,savedInstanceState: Bundle?) {
        container = view.findViewById(R.id.container)
        initContentView()
        header = view.findViewById<View>(R.id.header)
        titleTextView = view.findViewById(R.id.title)
        backBtn = view.findViewById<TextView>(R.id.back).apply {
            setOnClickListener {
                onBackPressed()
            }
        }
        menuBtn = view.findViewById<TextView>(R.id.menu).apply {
            setOnClickListener {
                showSearchBar()
            }
        }
        findInPageBar=view.findViewById(R.id.findInPage)
    }
    private fun initContentView() {
        container.removeAllViews()
        webView = WebView(requireContext().applicationContext)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        webView?.settings?.javaScriptEnabled = true
        container.addView(webView, params)
    }
    //TODO:继承搜索功能
    private fun showSearchBar(){
        requireContext().showToast(getString(R.string.not_finish))
        //findInPageBar.show()
    }
    override fun initData(savedInstanceState: Bundle?) {
        header.setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
        val session = sessionManager.findSessionById(arguments?.getString(BrowserFragment.SESSION_ID) ?: "")
        if (session == null) {
            RouterActivity?.loadHomeOrBrowserFragment(sessionManager.selectedSession?.id ?: "")
            return
        }
        sessionManager.getOrCreateEngineSession(session).executeJsFunction("javascript:getHtml();",
            ValueCallback<String> { value ->
                launch(Dispatchers.IO) {
                    val htmlSource = StringUtil.unEscapeString(value).htmlEncode()
                    val css = WebviewUtils.getInjectFileContent(requireContext(), "inject/highlight.min.css")
                    val js = WebviewUtils.getInjectFileContent(requireContext(), "inject/highlight.min.js")
                    //<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.15.6/styles/default.min.css">
                    //<script src="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.15.6/highlight.min.js"></script>
                    //
                    launch(Dispatchers.Main) {
                        val html = """
                            <html>
                               <head>
                                    <style>
                                    $css
                                   </style>
                               </head>
                               <body style="margin:0px">
                                  <pre><code class="html"> $htmlSource</code></pre>
                                   <script>
                                      $js
                                   </script>
                                  <script>hljs.initHighlightingOnLoad();</script>
                               </body>
                            </html>
                        """
                        //ImageTextUtil.setImageText(contentTextView,article.contentHtml)
                        webView?.loadData(html, null, null)
                    }
                }
            })
    }

    override fun onDestroyView() {
        if (webView != null) {
            webView?.parent?.apply {
                (this as? ViewGroup)?.removeAllViews()
            }
            webView?.stopLoading()
            webView?.clearView()
            webView?.removeAllViews()
            webView?.destroy()
        }
        super.onDestroyView()
    }
}
