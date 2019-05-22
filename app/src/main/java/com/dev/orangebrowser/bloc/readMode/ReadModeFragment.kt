package com.dev.orangebrowser.bloc.readMode

import android.content.Context
import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import android.view.View
import android.webkit.ValueCallback
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
import android.widget.TextView
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.utils.html2article.ContentExtractor
import com.dev.util.StringUtil
import com.dev.view.textview.ImageTextUtil
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
    lateinit var contentTextView:TextView
    lateinit var titleTextView:TextView
    lateinit var backBtn:TextView
    lateinit var menuBtn:TextView
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
        contentTextView=view.findViewById(R.id.content)
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
                    launch(Dispatchers.Main) {
                        titleTextView.text = article.title
                        ImageTextUtil.setImageText(contentTextView,article.contentHtml)
                    }
                }
            })
    }
}
