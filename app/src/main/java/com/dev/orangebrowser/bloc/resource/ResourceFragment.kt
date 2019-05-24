package com.dev.orangebrowser.bloc.resource

import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.ValueCallback
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.support.BackHandler
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import com.dev.orangebrowser.utils.html2article.ContentExtractor
import com.dev.util.DensityUtil
import com.dev.util.StringUtil
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.GridDividerItemDecoration
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.noober.background.drawable.DrawableCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.util.*
import javax.inject.Inject

//资源嗅探
class ResourceFragment : BaseFragment(), BackHandler {


    companion object {
        val Tag="ResourceFragment"
        fun newInstance(sessionId:String) = ResourceFragment().apply {
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
    lateinit var viewModel: ResourceViewModel
    lateinit var activityViewModel:MainViewModel
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(ResourceViewModel::class.java)
    }
    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_resource
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel=ViewModelProviders.of(activity!!,factory).get(MainViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
    }
    var selectors:LinkedList<String> = LinkedList()
    var selectedCategory:String=""
    override fun initView(view: View,savedInstanceState: Bundle?) {
        view.findViewById<RecyclerView>(R.id.selector)?.apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            this.addItemDecoration(GridDividerItemDecoration(DensityUtil.dip2px(requireContext(),6f),0,getColor(R.color.transparent)))
            this.adapter=object:
                BaseQuickAdapter<String, CustomBaseViewHolder>(R.layout.item_category,selectors){
                override fun convert(helper: CustomBaseViewHolder, item: String) {
                    if (selectedCategory==item){
                        val bg= DrawableCreator.Builder().setSolidColor(activityViewModel.theme.value!!.colorPrimary)
                            .setCornersRadius(DensityUtil.dip2px(requireContext(),1000f).toFloat())
                            .setStrokeColor(getColor(R.color.color_EEEEEE)).setStrokeWidth(DensityUtil.dip2px(requireContext(),1f).toFloat())
                            .build()
                        helper.setTextColor(R.id.category,getColor(R.color.colorWhite))
                        helper.itemView.background=bg
                    }else{
                        val bg= DrawableCreator.Builder().setSolidColor(getColor(R.color.transparent))
                            .setCornersRadius(DensityUtil.dip2px(requireContext(),1000f).toFloat())
                            .setStrokeColor(getColor(R.color.color_EEEEEE)).setStrokeWidth(DensityUtil.dip2px(requireContext(),1f).toFloat())
                            .build()
                        helper.itemView.background=bg
                        helper.setTextColor(R.id.category,getColor(R.color.colorBlack))
                    }
                    helper.setText(R.id.category,item)
                    helper.itemView.setOnClickListener {
                        selectedCategory=item
                        selectCategory(item)
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }

    }
    private fun selectCategory(category: String){

    }
    private lateinit var allResource:List<String>
    private lateinit var imageResource:List<String>
    private lateinit var videoResource:List<String>
    private lateinit var audioResource:List<String>
    private lateinit var jsResource:List<String>
    lateinit var styleResource:List<String>
    var otherResource:List<String> = LinkedList()
    override fun initData(savedInstanceState: Bundle?) {
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
                    imageResource= Jsoup.parse(article.contentHtml).select("img").map { it.attr("abs:src") }.filter { it.isNotBlank() }
                    videoResource= Jsoup.parse(article.contentHtml).select("video").map { it.attr("abs:src") }.filter { it.isNotBlank() }
                    audioResource= Jsoup.parse(article.contentHtml).select("audio").map { it.attr("abs:src") }.filter { it.isNotBlank() }
                    jsResource= Jsoup.parse(article.contentHtml).select("script").map { it.attr("abs:href") }.filter { it.isNotBlank() }
                    styleResource= Jsoup.parse(article.contentHtml).select("link[rel='stylesheet']").map { it.attr("abs:href") }.filter { it.isNotBlank() }
                    launch(Dispatchers.Main) {

                    }
                }
            })
    }
}

enum class Resource(link:String=""){
    IMAGE,VIDEO,AUDIO,SCRIPT,STYLESHEET,OTHER
}