package com.dev.orangebrowser.bloc.resource

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.copyText
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.extension.shareLink
import com.dev.base.extension.showToast
import com.dev.base.support.BackHandler
import com.dev.browser.concept.EngineSession
import com.dev.browser.extension.isPermissionGranted
import com.dev.browser.feature.downloads.DownloadManager
import com.dev.browser.session.Download
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.browser.support.DownloadUtils
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import com.dev.orangebrowser.utils.PositionUtils
import com.dev.orangebrowser.view.LongClickFrameLayout
import com.dev.orangebrowser.view.contextmenu.Action
import com.dev.orangebrowser.view.contextmenu.CommonContextMenuAdapter
import com.dev.orangebrowser.view.contextmenu.MenuItem
import com.dev.util.DensityUtil
import com.dev.util.StringUtil
import com.dev.view.StatusBarUtil
import com.dev.view.dialog.DialogBuilder
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
        sessionManager.selectedSession?.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id,R.anim.holder,R.anim.slide_right_out)
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
    var selectorRecyclerView:RecyclerView?=null
    private lateinit var container:LongClickFrameLayout
    private lateinit var header:View
    private lateinit var containerWrapper:View
    override fun initView(view: View,savedInstanceState: Bundle?) {
        container=view.findViewById(R.id.container)
        header=view.findViewById(R.id.header)
        containerWrapper=view.findViewById(R.id.container_wrapper)
        selectorRecyclerView= view.findViewById<RecyclerView>(R.id.selector)?.apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            this.addItemDecoration(GridDividerItemDecoration(DensityUtil.dip2px(requireContext(),3f),0,getColor(R.color.transparent)))
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
                        displayRecyclerView?.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
        displayRecyclerView=view.findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            this.layoutManager=LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            this.addItemDecoration(GridDividerItemDecoration(0,DensityUtil.dip2px(requireContext(),0.5f),getColor(R.color.material_grey_300)))
        }
        view.findViewById<View>(R.id.back)?.apply {
            setOnClickListener {
                onBackPressed()
            }
        }
    }
    private fun selectCategory(category: String){
        if (category==selectors[0]){
            allResource.clear()
            allResource.addAll(imageResource)
            allResource.addAll(videoResource)
            allResource.addAll(audioResource)

        }
        if (category==selectors[1]){
            allResource.clear()
            allResource.addAll(imageResource)
        }
        if (category==selectors[2]){
            allResource.clear()
            allResource.addAll(videoResource)
        }
        if (category==selectors[3]){
            allResource.clear()
            allResource.addAll(audioResource)
        }
        displayRecyclerView?.adapter?.notifyDataSetChanged()
        selectedCategory=category
        selectorRecyclerView?.adapter?.notifyDataSetChanged()
    }
    private  var allResource:LinkedList<Resource> = LinkedList()
    private lateinit var imageResource:List<ImageResource>
    private lateinit var videoResource:List<VideoResource>
    private lateinit var audioResource:List<AudioResource>
    private  var displayRecyclerView:RecyclerView?=null
    private lateinit var  displayAdapter:BaseQuickAdapter<Resource,CustomBaseViewHolder>
    var session:Session?=null
    override fun initData(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(),activityViewModel.theme.value!!.colorPrimary)
        header.setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
        containerWrapper.setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
        session = sessionManager.findSessionById(arguments?.getString(BrowserFragment.SESSION_ID) ?: "")
        if (session == null) {
            RouterActivity?.loadHomeOrBrowserFragment(sessionManager.selectedSession?.id ?: "")
            return
        }
        sessionManager.getOrCreateEngineSession(session!!).executeJsFunction("javascript:getHtml();",
            ValueCallback<String> { value ->
                launch(Dispatchers.IO) {
                    val html = StringUtil.unEscapeString(value)
                    Log.d("html",html)
                    val doc=Jsoup.parse(html)
                    imageResource= doc.select("img").map {
                        val src=it.attr("src")
                        if(src.startsWith("//")){
                            it.attr("src", "http:$src")
                        }
                        ImageResource(link=it.attr("abs:src").trim())
                    }.filter { it.link.isNotBlank()}
                    videoResource= doc.select("video").map {
                        val poster=it.attr("poster")
                        val src=it.attr("src")
                        if(src.startsWith("//")){
                            it.attr("src", "http:$src")
                        }
                        var link=it.attr("abs:src").trim()
                        if (link.isBlank()){
                           val sources= it.select("source").map { source->source.attr("abs:src").trim() }
                            for (source in sources){
                                if (source.isNotBlank() && !source.startsWith("blob:")){
                                    link=source
                                    break
                                }
                            }
                        }
                       VideoResource(link=link,poster = poster)
                    }.filter { it.link.isNotBlank() }
                    audioResource= doc.select("audio").map {
                        val src=it.attr("src")
                        if(src.startsWith("//")){
                            it.attr("src", "http:$src")
                        }
                        var link=it.attr("abs:src").trim()
                        if (link.isBlank()){
                            val sources= it.select("source").map { source->source.attr("abs:src").trim() }
                            for (source in sources){
                                if (source.isNotBlank() && !source.startsWith("blob:")){
                                    link=source
                                    break
                                }
                            }
                        }
                        AudioResource(link=link)
                    }.filter { it.link.isNotBlank() }
                    allResource.addAll(imageResource)
                    allResource.addAll(videoResource)
                    allResource.addAll(audioResource)
                    selectedCategory = if (allResource.isNotEmpty()){
                        selectors.add(getString(R.string.all_resource)+"("+allResource.size+")")
                        getString(R.string.all_resource)+"("+allResource.size+")"
                    }else{
                        selectors.add(getString(R.string.all_resource))
                        getString(R.string.all_resource)
                    }
                    if (imageResource.isNotEmpty()){
                        selectors.add(getString(R.string.image)+"("+imageResource.size+")")
                    }else{
                        selectors.add(getString(R.string.image))
                    }
                    if (videoResource.isNotEmpty()){
                        selectors.add(getString(R.string.video)+"("+videoResource.size+")")
                    }else{
                        selectors.add(getString(R.string.video))
                    }
                    if (audioResource.isNotEmpty()){
                        selectors.add(getString(R.string.audio)+"("+audioResource.size+")")
                    }else{
                        selectors.add(getString(R.string.audio))
                    }

                    launch(Dispatchers.Main) {
                        displayAdapter=object:BaseQuickAdapter<Resource,CustomBaseViewHolder>(R.layout.item_resource,allResource){
                            override fun convert(helper: CustomBaseViewHolder, item: Resource) {
                                when (item) {
                                    is ImageResource -> {
                                          helper.setText(R.id.icon,getString(R.string.ic_image))
                                    }
                                    is VideoResource -> {
                                        helper.setText(R.id.icon,getString(R.string.ic_video))
                                    }
                                    is AudioResource -> {
                                        helper.setText(R.id.icon,getString(R.string.ic_audio))
                                    }
                                }
                                helper.setTextColor(R.id.icon,activityViewModel.theme.value!!.colorPrimary)
                                helper.setText(R.id.title,item.link)
                            }
                        }
                        initResourceItemDialog(displayAdapter)
                        displayRecyclerView?.adapter=displayAdapter
                        selectorRecyclerView?.adapter?.notifyDataSetChanged()
                        displayRecyclerView?.adapter?.notifyDataSetChanged()
                    }
                }
            })
    }
    var resourceItemDialog: Dialog?=null
    private fun initResourceItemDialog(adapter: BaseQuickAdapter<Resource, CustomBaseViewHolder>?) {
        adapter?.setOnItemLongClickListener { _, _, position ->
            resourceItemDialog=  DialogBuilder()
                    .setLayoutId(R.layout.dialog_context_menu)
                    .setHeightParent(1f)
                    .setWidthPercent(1f)
                    .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                        override fun onViewCreated(view: View) {
                            initResourceItemDialogView(view,position)
                        }
                    })
                    .setGravity(Gravity.TOP)
                    .build(requireContext())
            resourceItemDialog?.show()
            true
        }
    }

    private fun initResourceItemDialogView(view:View,position:Int){
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = CommonContextMenuAdapter(
                R.layout.mozac_feature_contextmenu_item, listOf(
                    MenuItem(label = getString(R.string.menu_download),action = object: Action<MenuItem> {
                        @SuppressLint("MissingPermission")
                        override fun execute(data: MenuItem) {
                            resourceItemDialog?.dismiss()
                            val resource=allResource[position]

                            val download=Download(
                                url = resource.link,
                                fileName = DownloadUtils.guessFileName(null, resource.link, null),
                                referer = session?.url,
                                cookies = session?.getCookies(resource.link)
                            )
                            if(resource is VideoResource){
                                download.poster= resource.poster
                            }
                            if (requireContext().applicationContext.isPermissionGranted(
                                    Manifest.permission.INTERNET,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                )){
                                DownloadManager.getInstance(requireContext().applicationContext).download(download)
                            }else{
                                requireContext().showToast(getString(R.string.tip_permission_write_external_storage))
                            }
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_share),action = object: Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            resourceItemDialog?.dismiss()
                            if(!requireContext().shareLink(title =getString(R.string.share),url =allResource[position].link)){
                                requireContext().showToast(getString(R.string.tip_share_fail))
                            }
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_copy_link),action = object: Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            resourceItemDialog?.dismiss()
                            requireContext().copyText(getString(R.string.link),allResource[position].link)
                            requireContext().showToast(getString(R.string.tip_copy_link))

                        }
                    })
                )
            )
        }
        recyclerView.onGlobalLayoutComplete {
            PositionUtils.initOffSet(requireContext())
            (it.layoutParams as? FrameLayout.LayoutParams)?.apply {
                this.leftMargin = PositionUtils.calculateRecyclerViewLeftMargin(
                    container.width,
                    it.width, container.getLongClickPosition().x
                )
                this.topMargin = PositionUtils.calculateRecyclerViewTopMargin(
                    container.height,
                    it.height, container.getLongClickPosition().y
                )
                it.layoutParams = this
            }
        }
    }
}

abstract class Resource(var link:String="")
class ImageResource(link:String):Resource(link)
class VideoResource(link:String,var poster:String=""):Resource(link)
class AudioResource(link:String):Resource(link)
