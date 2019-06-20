package com.dev.orangebrowser.bloc.imageMode

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.browser.feature.downloads.DownloadManager
import com.dev.browser.session.Download
import com.dev.browser.session.SessionManager
import com.dev.browser.support.DownloadUtils
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.config.ErrorCode
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.utils.PositionUtils
import com.dev.orangebrowser.utils.PositionUtils.calculateRecyclerViewLeftMargin
import com.dev.orangebrowser.utils.PositionUtils.calculateRecyclerViewTopMargin
import com.dev.orangebrowser.view.LongClickFrameLayout
import com.dev.orangebrowser.view.contextmenu.Action
import com.dev.orangebrowser.view.contextmenu.CommonContextMenuAdapter
import com.dev.orangebrowser.view.contextmenu.MenuItem
import com.dev.util.StringUtil
import com.dev.view.StatusBarUtil
import com.dev.view.dialog.DialogBuilder
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.gjiazhe.scrollparallaximageview.ScrollParallaxImageView
import com.gjiazhe.scrollparallaximageview.parallaxstyle.VerticalMovingStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*
import javax.inject.Inject

class ImageModeModeFragment : BaseFragment(), BackHandler {


    companion object {
        val Tag = "ImageModeModeFragment"
        fun newInstance(sessionId: String) = ImageModeModeFragment().apply {
            arguments = Bundle().apply {
                putString(BrowserFragment.SESSION_ID, sessionId)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        sessionManager.selectedSession?.apply {
            RouterActivity?.loadHomeOrBrowserFragment(this.id, R.anim.holder, R.anim.slide_right_out)
        }
        return true
    }

    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var downloadManager: DownloadManager
    lateinit var viewModel: ImageModeViewModel
    lateinit var activityViewModel: MainViewModel

    lateinit var recyclerView: RecyclerView
    lateinit var header: View
    lateinit var containerWrapper: View
    lateinit var container: LongClickFrameLayout
    lateinit var topOverLayer: FrameLayout
    lateinit var nextPageSpinner: Spinner
    lateinit var attrSpinner: Spinner
    lateinit var spinnerContainer: LinearLayout
    lateinit var useLastChildCheckBox:AppCompatCheckBox
    //data
    private var images = LinkedList<String>()
    lateinit var adapter: BaseQuickAdapter<String, CustomBaseViewHolder>
    var showAllImages: Boolean = false
    var html = ""
    var imageAttr = "abs:src"
    var nextPageSelector = ""
    var sessionUrl=""
    var nextPageUrl=""
    var replaceNthChildWithLastChild=false
    var loadCompleted=false
    //图片属性
    private var imageAttributes = LinkedList<KeyValue>()
    //链接选择器
    private var linkSelectors = LinkedList<KeyValue>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(ImageModeViewModel::class.java)
    }

    //获取layoutResourceId
    override fun getLayoutResId(): Int {
        return R.layout.fragment_image_mode
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {
        topOverLayer = view.findViewById<FrameLayout>(R.id.top_over_lay).apply {
            setOnClickListener {
                hideSettingView()
                viewModel.refresh(url = sessionUrl,showAllImages = showAllImages,imageAttr = imageAttr,nextPageSelector = nextPageSelector)
            }
        }
        nextPageSpinner = view.findViewById(R.id.spinner_next)
        attrSpinner = view.findViewById(R.id.spinner_attr)
        useLastChildCheckBox=view.findViewById<AppCompatCheckBox>(R.id.use_last_child).apply{
            this.setOnCheckedChangeListener { _, isChecked ->
                replaceNthChildWithLastChild=isChecked
                if (replaceNthChildWithLastChild){
                    nextPageSelector=StringUtil.replaceLast(nextPageSelector,"nth-child\\(\\d\\)","last-child")
                }
            }
        }

        spinnerContainer = view.findViewById(R.id.spiner_container)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        header = view.findViewById(R.id.header)
        containerWrapper = view.findViewById(R.id.container_wrapper)
        view.findViewById<View>(R.id.setting)?.apply {
            setOnClickListener {
                showSettingView()
            }
        }
        view.findViewById<View>(R.id.back)?.apply {
            setOnClickListener {
                onBackPressed()
            }
        }
        container = view.findViewById(R.id.container)
        view.findViewById<View>(R.id.switchToggle)?.apply {
            setOnClickListener {
                showAllImages = !showAllImages
                if (showAllImages) {
                    requireContext().showToast(getString(R.string.tip_switch_to_show_all_images))
                } else {
                    requireContext().showToast(getString(R.string.tip_switch_to_show_main_images))
                }
                //刷新
                viewModel.refresh(url = sessionUrl,showAllImages = showAllImages,imageAttr = imageAttr,nextPageSelector = nextPageSelector)
            }
        }
        spinnerContainer.onGlobalLayoutComplete {
            hideSettingView()
        }
        //监听数据

        viewModel.errCodeLiveData.observe(this,Observer<Int>{
            when(it){
                ErrorCode.LOAD_FAIL->{
                    requireContext().apply {
                        showToast(getString(R.string.load_fail))
                    }
                    adapter.loadMoreFail()
                }
            }
        })
        viewModel.nextPageUrlLiveData.observe(this,Observer<String>{
            if (it!=nextPageUrl){
                Log.d("nextPageUrlLiveData","is $it")
                nextPageUrl=it
            }else{
                adapter.loadMoreEnd()
                loadCompleted=true
            }
        })
        viewModel.refreshImagesLiveData.observe(this,Observer<List<String>>{
            images.clear()
            images.addAll(it)
            adapter.notifyDataSetChanged()
        })
        viewModel.loadMoreImagesLiveData.observe(this,Observer<List<String>>{
            val oldSize=images.size
            images.addAll(it)
            adapter.notifyItemRangeInserted(oldSize,it.size)
            adapter.loadMoreComplete()
        })
        viewModel.htmlLiveData.observe(this,Observer<String>{
            html=it
            //获取imageAttributes和LinkSelector
            if (linkSelectors.size==0 && imageAttributes.size==0){
                launch(Dispatchers.IO) {
                    val doc=Jsoup.parse(html)
                    doc.setBaseUri(sessionUrl)
                    imageAttributes=getImageAttributes(doc)
                    linkSelectors=getLinkSelectors(doc)
                    launch(Dispatchers.Main) {
                        initSpinner()
                    }
                }
            }
        })
    }
    private fun initSpinner(){
        attrSpinner.adapter = KeyValueAdapter(data = imageAttributes)
        attrSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (imageAttributes.size > 0) {
                    imageAttr = imageAttributes[0].value
                }
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                imageAttr = imageAttributes[position].value
            }
        }
        nextPageSpinner.adapter = KeyValueAdapter(data = linkSelectors)
        nextPageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (linkSelectors.size > 0) {
                    nextPageSelector = linkSelectors[0].key
                }
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                nextPageSelector = if (replaceNthChildWithLastChild){
                    StringUtil.replaceLast(linkSelectors[position].key,"nth-child\\(\\d\\)","last-child")
                }else{
                    linkSelectors[position].key
                }
            }
        }
    }

    private fun getImageAttributes(document:Document):LinkedList<KeyValue>{
        val list=LinkedList<KeyValue>()
        document.select("img").forEach {ele->
            ele.attributes().forEach {
                list.add(KeyValue(key = it.value,value = it.key))
            }
        }
        return list
    }
    private fun getLinkSelectors(document: Document):LinkedList<KeyValue>{
        val list=LinkedList<KeyValue>()
        document.select("a").forEach {ele->
            list.add(KeyValue(key = ele.cssSelector(), value = ele.text()))
        }
        return list
    }
    private fun showSettingView() {
        topOverLayer.show()
        spinnerContainer.animate().apply {
            duration = 250
        }.translationY(0f).start()
    }

    private fun hideSettingView() {
        spinnerContainer.animate().translationY(-spinnerContainer.height.toFloat()).apply {
            duration = 250
        }.withEndAction {
            topOverLayer.hide()
        }.start()
    }

    var downloadDialog: Dialog? = null


    private fun showDialog() {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        super.onActivityCreated(savedInstanceState)
    }

    override fun initData(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(), activityViewModel.theme.value!!.colorPrimary)
        header.setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
        containerWrapper.setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
        val session = sessionManager.findSessionById(arguments?.getString(BrowserFragment.SESSION_ID) ?: "")
        if (session == null) {
            RouterActivity?.loadHomeOrBrowserFragment(sessionManager.selectedSession?.id ?: "")
            return
        }
        adapter = object :
            BaseQuickAdapter<String, CustomBaseViewHolder>(R.layout.item_scroll_parallax_image, images) {
            override fun convert(helper: CustomBaseViewHolder, item: String) {
                helper.loadNoCropImage(R.id.image, url = item, referer = session.url)
                helper.itemView.findViewById<ScrollParallaxImageView>(R.id.image).setParallaxStyles(
                    VerticalMovingStyle()
                )
            }
        }
        adapter.setPreLoadNumber(2)
        adapter.setOnLoadMoreListener({
            //如果加载完成，则不再加载
            if (loadCompleted){
                adapter.loadMoreEnd()
                return@setOnLoadMoreListener
            }
            Log.d("OnLoadMoreListener","nextPageUrl is $nextPageUrl")
            if (nextPageUrl.isBlank()){
                adapter.loadMoreComplete()
            }else{
                viewModel.loadMore(url = nextPageUrl,showAllImages = showAllImages,imageAttr = imageAttr,nextPageSelector = nextPageSelector)
            }
        }, recyclerView)
        adapter.disableLoadMoreIfNotFullPage()
        adapter.setEnableLoadMore(true)
        initImageItemContextMenu(adapter)
        recyclerView.adapter = adapter
        sessionUrl=session.url
        viewModel.refresh(url = sessionUrl,showAllImages = showAllImages,imageAttr = imageAttr,nextPageSelector = nextPageSelector)

    }

    var imageItemContextMenu: Dialog? = null
    private fun initImageItemContextMenu(adapter: BaseQuickAdapter<String, CustomBaseViewHolder>?) {
        adapter?.setOnItemLongClickListener { _, _, position ->
            imageItemContextMenu = DialogBuilder()
                .setLayoutId(R.layout.dialog_context_menu)
                .setHeightParent(1f)
                .setWidthPercent(1f)
                .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                    override fun onViewCreated(view: View) {
                        initImageItemContextMenuView(view, position)
                    }
                })
                .setGravity(Gravity.TOP)
                .build(requireContext())
            imageItemContextMenu?.show()
            true
        }
    }

    private fun initImageItemContextMenuView(view: View, position: Int) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            this.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = CommonContextMenuAdapter(
                R.layout.mozac_feature_contextmenu_item, listOf(
                    MenuItem(label = getString(R.string.menu_download_image), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            downloadImage(url = images[position], referer = sessionManager.selectedSession?.url ?: "")
                            imageItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_share), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            if (!requireContext().shareLink(
                                    title = getString(R.string.share_image),
                                    url = images[position]
                                )
                            ) {
                                requireContext().showToast(getString(R.string.tip_share_fail))
                            }
                            imageItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_copy_link), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            requireContext().copyText(getString(R.string.link), images[position])
                            requireContext().showToast(getString(R.string.tip_copy_link))
                            imageItemContextMenu?.dismiss()
                        }
                    })
                )
            )
        }
        recyclerView.onGlobalLayoutComplete {
            PositionUtils.initOffSet(it.context)
            (it.layoutParams as? FrameLayout.LayoutParams)?.apply {
                this.leftMargin = calculateRecyclerViewLeftMargin(
                    container.width,
                    it.width, container.getLongClickPosition().x
                )
                this.topMargin = calculateRecyclerViewTopMargin(
                    container.height,
                    it.height, container.getLongClickPosition().y
                )
                it.layoutParams = this
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun downloadImage(url: String, referer: String) {
        val fileName = DownloadUtils.guessFileName("", url, "")
        downloadManager.download(Download(url = url, fileName = fileName, referer = referer, contentType = "image/*"))
    }


    @SuppressLint("MissingPermission")
    private fun downloadAllImages() {
        val referer = sessionManager.selectedSession?.url ?: ""
        for (url in images) {
            val fileName = DownloadUtils.guessFileName("", url, "")
            downloadManager.download(
                Download(
                    url = url,
                    fileName = fileName,
                    referer = referer,
                    contentType = "image/*"
                )
            )
        }
    }
}

data class KeyValue(val key: String, val value: String)

class KeyValueAdapter(var data: List<KeyValue>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (convertView != null) {
            (convertView.tag as? ViewHolder)?.apply {
                this.title.text = data[position].value+" => "+data[position].key
            }
            return convertView
        }
        val view = View.inflate(parent.context, R.layout.item_title_selector, null)
        ViewHolder(view).apply {
            this.title.text = data[position].value+" => "+data[position].key
        }
        return view
    }

    override fun getItem(position: Int): KeyValue {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return data.size
    }

    class ViewHolder(var view: View) {
        val title: TextView = view.findViewById(R.id.title)

        init {
            view.tag = this
        }
    }
}