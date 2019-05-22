package com.dev.orangebrowser.bloc.readMode

import android.content.Context
import android.os.Bundle
import android.util.JsonReader
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
import org.jsoup.Jsoup
import javax.inject.Inject
import android.util.JsonToken
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.dev.base.support.BackHandler
import com.dev.browser.concept.EngineViewLifecycleObserver
import com.dev.browser.engine.system.SystemEngineView
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.utils.html2article.ContentExtractor
import com.dev.util.StringUtil
import com.dev.view.textview.ImageTextUtil
import kotlinx.android.synthetic.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.StringReader


class ReadModeFragment : BaseFragment(),BackHandler {

    override fun onBackPressed(): Boolean {
        val session=sessionManager.findSessionById(arguments?.getString(BrowserFragment.SESSION_ID) ?: "")
        if (session==null){
            RouterActivity?.loadHomeOrBrowserFragment(sessionManager.selectedSession?.id ?: "")
        }else{
            RouterActivity?.loadHomeOrBrowserFragment(session.id)
        }
        return true
    }

    companion object {
        val Tag = "ReadModeFragment"
        fun newInstance(sessionId: String) = ReadModeFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }

    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var viewModel: ReadModeViewModel
    lateinit var activityViewModel:MainViewModel
    lateinit var container:FrameLayout
    lateinit var titleTextView:TextView
    lateinit var backBtn:TextView
    lateinit var menuBtn:TextView
     var webView:WebView?=null
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
        container=view.findViewById(R.id.container)
        initContentView()
        titleTextView=view.findViewById(R.id.title)
        backBtn=view.findViewById<TextView>(R.id.back).apply{
            setOnClickListener {
                onBackPressed()
            }
        }
        menuBtn=view.findViewById<TextView>(R.id.menu).apply {
            setOnClickListener {
                showDialog()
            }
        }
    }
    private fun initContentView(){
        container.removeAllViews()
        webView= WebView(requireContext().applicationContext)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        webView?.settings?.javaScriptEnabled=true
        webView?.webViewClient=object:WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        container.addView(webView,params)
    }
    private fun showDialog(){

    }
    override fun initData(savedInstanceState: Bundle?) {
        val session = sessionManager.findSessionById(arguments?.getString(BrowserFragment.SESSION_ID) ?: "")
        if (session == null) {
            RouterActivity?.loadHomeOrBrowserFragment(sessionManager.selectedSession?.id ?: "")
            return
        }
        sessionManager.getOrCreateEngineSession(session).executeJsFunction("javascript:getHtml();",
            ValueCallback<String> { value ->
                launch(Dispatchers.IO) {
                    val html= StringUtil.unEscapeString(value)

                    val article= ContentExtractor.getArticleByHtml(html)
                    val css=WebviewUtils.getInjectFileContent(requireContext(),"inject/read_mode.css")
                    val js=WebviewUtils.getInjectFileContent(requireContext(),"inject/read_mode.js")
                    launch(Dispatchers.Main) {
                        titleTextView.text = article.title
                        Log.d("html",article.contentHtml)
                        val html="""
                            <html>
                               <head>
                                   <style>
                                    $css
                                   </style>
                                   <script>
                                      $js
                                   </script>
                               </head>
                               <body>
                               ${article.contentHtml}
                               </body>
                            </html>
                        """
                        //ImageTextUtil.setImageText(contentTextView,article.contentHtml)
                        webView?.loadData(html,null,null)
                    }
                }
            })
    }

    override fun onDestroyView() {
        if (webView!=null){
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
