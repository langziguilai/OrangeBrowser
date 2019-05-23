package com.dev.orangebrowser.bloc.readMode

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.browser.session.SessionManager
import com.dev.browser.utils.WebviewUtils
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import javax.inject.Inject
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import android.widget.TextView
import com.dev.base.support.BackHandler
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.utils.html2article.ContentExtractor
import com.dev.util.StringUtil
import com.dev.view.dialog.DialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ReadModeFragment : BaseFragment(), BackHandler {
    companion object {
        val Tag = "ReadModeFragment"
        const val SMALL_FONT_SIZE = "16"
        const val MEDIUM_FONT_SIZE = "18"
        const val LARGE_FONT_SIZE = "20"
        const val BG_WHITE = "#FFFFFF"
        const val TEXT_COLOR_FOR_BG_WHITE = "#1B1B1B"
        const val BG_MO = "#F8F1E3"
        const val TEXT_COLOR_FOR_BG_MO = "#4F321C"
        const val BG_GERY = "#5A5A5C"
        const val TEXT_COLOR_FOR_BG_GERY = "#CBCBCB"
        const val BG_BLACK = "#121212"
        const val TEXT_COLOR_FOR_BG_BLACK = "#B0B0B0"
        const val STYLE_WHITE = 1
        const val STYLE_MO = 2
        const val STYLE_GREY = 3
        const val STYLE_BLACK = 4
        fun newInstance(sessionId: String) = ReadModeFragment().apply {
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
    @Inject
    lateinit var tabsUseCases: TabsUseCases
    lateinit var viewModel: ReadModeViewModel
    lateinit var activityViewModel: MainViewModel
    lateinit var container: FrameLayout
    lateinit var titleTextView: TextView
    lateinit var backBtn: TextView
    lateinit var menuBtn: TextView
    lateinit var header: View
    var webView: WebView? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(ReadModeViewModel::class.java)
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_read_mode
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
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
                showDialog()
            }
        }
    }

    private fun initContentView() {
        container.removeAllViews()
        webView = WebView(requireContext().applicationContext)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                tabsUseCases.addTab.invoke(url, true, true, sessionManager.selectedSession)
                RouterActivity?.loadBrowserFragment(sessionManager.selectedSession!!.id)
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: String): Boolean {
                val url = request
                tabsUseCases.addTab.invoke(url, true, true, sessionManager.selectedSession)
                RouterActivity?.loadBrowserFragment(sessionManager.selectedSession!!.id)
                return true
            }
        }
        container.addView(webView, params)
    }

    private fun setFontSize(size: String) {
        webView?.evaluateJavascript("setFontSize($size)", null)
    }

    private fun setStyle(style: Int) {
        var bgColor = BG_WHITE
        var textColor = TEXT_COLOR_FOR_BG_WHITE
        when (style) {
            STYLE_WHITE -> {
                bgColor = BG_WHITE
                textColor = TEXT_COLOR_FOR_BG_WHITE
            }
            STYLE_MO -> {
                bgColor = BG_MO
                textColor = TEXT_COLOR_FOR_BG_MO
            }
            STYLE_GREY -> {
                bgColor = BG_GERY
                textColor = TEXT_COLOR_FOR_BG_GERY
            }
            STYLE_BLACK -> {
                bgColor = BG_BLACK
                textColor = TEXT_COLOR_FOR_BG_BLACK
            }
        }
        webView?.evaluateJavascript("setStyle('$bgColor','$textColor')", null)
    }

    var readModeSettingDialog: Dialog? = null
    private fun showDialog() {
        readModeSettingDialog = DialogBuilder()
            .setLayoutId(R.layout.dialog_read_mode_setting)
            .setGravity(Gravity.BOTTOM)
            .setWidthPercent(1f)
            .setEnterAnimationId(R.anim.slide_up)
            .setExitAnimationId(R.anim.slide_down)
            .setCanceledOnTouchOutside(true)
            .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                override fun onViewCreated(view: View) {
                    view.findViewById<View>(R.id.font_size_small)?.apply {
                        setOnClickListener {
                            setFontSize(SMALL_FONT_SIZE)
                        }
                    }
                    view.findViewById<View>(R.id.font_size_medium)?.apply {
                        setOnClickListener {
                            setFontSize(MEDIUM_FONT_SIZE)
                        }
                    }
                    view.findViewById<View>(R.id.font_size_large)?.apply {
                        setOnClickListener {
                            setFontSize(LARGE_FONT_SIZE)
                        }
                    }
                    view.findViewById<TextView>(R.id.style_white)?.apply {
                        this.setBackgroundColor(Color.parseColor(BG_WHITE))
                        this.setTextColor(Color.parseColor(TEXT_COLOR_FOR_BG_WHITE))
                        setOnClickListener {
                            setStyle(STYLE_WHITE)
                        }
                    }
                    view.findViewById<TextView>(R.id.style_mo)?.apply {
                        this.setBackgroundColor(Color.parseColor(BG_MO))
                        this.setTextColor(Color.parseColor(TEXT_COLOR_FOR_BG_MO))
                        setOnClickListener {
                            setStyle(STYLE_MO)
                        }
                    }
                    view.findViewById<TextView>(R.id.style_grey)?.apply {
                        this.setBackgroundColor(Color.parseColor(BG_GERY))
                        this.setTextColor(Color.parseColor(TEXT_COLOR_FOR_BG_GERY))
                        setOnClickListener {
                            setStyle(STYLE_GREY)
                        }
                    }
                    view.findViewById<TextView>(R.id.style_black)?.apply {
                        this.setBackgroundColor(Color.parseColor(BG_BLACK))
                        this.setTextColor(Color.parseColor(TEXT_COLOR_FOR_BG_BLACK))
                        setOnClickListener {
                            setStyle(STYLE_BLACK)
                        }
                    }
                }
            }).build(requireContext())
        readModeSettingDialog?.show()
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
                    val html = StringUtil.unEscapeString(value)
                    val article = ContentExtractor.getArticleByHtml(html)
                    val css = WebviewUtils.getInjectFileContent(requireContext(), "inject/read_mode.css")
                    val js = WebviewUtils.getInjectFileContent(requireContext(), "inject/read_mode.js")
                    launch(Dispatchers.Main) {
                        titleTextView.text = article.title
                        Log.d("html", article.contentHtml)
                        val html = """
                            <html>
                               <head>
                                   <style>
                                    $css
                                   </style>

                               </head>
                               <body style="margin:0px">
                               ${article.contentHtml}
                                <script>
                                      $js
                                   </script>
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
