package com.dev.orangebrowser.bloc.imageMode

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.ValueCallback
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.browser.session.SessionManager
import com.dev.browser.utils.WebviewUtils
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import com.dev.orangebrowser.utils.html2article.ContentExtractor
import com.dev.util.DensityUtil
import com.dev.util.StringUtil
import com.dev.view.dialog.DialogBuilder
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.GridDividerItemDecoration
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.dialog_read_mode_setting.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.util.*
import javax.inject.Inject

class ImageModeModeFragment : BaseFragment() {


    companion object {
        val Tag="ImageModeModeFragment"
        fun newInstance(sessionId:String) = ImageModeModeFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }
    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var viewModel: ImageModeViewModel
    lateinit var activityViewModel: MainViewModel

    lateinit var recyclerView: RecyclerView
    lateinit var header:View
    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel=ViewModelProviders.of(this,factory).get(ImageModeViewModel::class.java)
    }
    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_image_mode
    }
    override fun initView(view: View,savedInstanceState: Bundle?) {
        recyclerView=view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        recyclerView.addItemDecoration(GridDividerItemDecoration(0,DensityUtil.dip2px(requireContext(),10f),getColor(R.color.transparent)))
        header=view.findViewById(R.id.header)
        view.findViewById<View>(R.id.download)?.apply {
            showDialog()
        }
    }
    var downloadDialog: Dialog?=null
    private fun showDialog(){
        downloadDialog = DialogBuilder()
            .setLayoutId(R.layout.dialog_download_all_images)
            .setGravity(Gravity.CENTER)
            .setEnterAnimationId(R.anim.fade_in)
            .setExitAnimationId(R.anim.fade_out)
            .setCanceledOnTouchOutside(true)
            .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                override fun onViewCreated(view: View) {
                    view.findViewById<View>(R.id.cancel)?.apply {
                        setOnClickListener {
                            downloadDialog?.dismiss()
                        }
                    }
                    view.findViewById<View>(R.id.sure)?.apply {
                        setOnClickListener {
                            downloadAllImages()
                            downloadDialog?.dismiss()
                        }
                    }
                }
            }).build(requireContext())
        downloadDialog?.show()
    }
    private fun downloadAllImages(){

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
    }
    private var images= LinkedList<String>()
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
                    val elements=Jsoup.parse(html).select("img")
                    for (ele in elements){
                        val imageSrc=ele.attr("abs:src").trim()
                        //如果不是直接设置数据的，就添加
                        if (!imageSrc.startsWith("data:")){
                            images.add(imageSrc)
                        }
                    }
                    launch(Dispatchers.Main) {
                        recyclerView.adapter=object:BaseQuickAdapter<String,CustomBaseViewHolder>(R.layout.item_image_display,images){
                            override fun convert(helper: CustomBaseViewHolder, item: String) {
                               helper.loadImage(R.id.image,url=item,referer = session.url)
                            }
                        }
                    }
                }
            })
    }
}
