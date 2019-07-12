package com.dev.orangebrowser.bloc.imageMode

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
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
import com.dev.orangebrowser.config.ResultCode
import com.dev.orangebrowser.data.model.ImageModeMeta
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
import com.dev.view.button.CommonShapeButton
import com.dev.view.dialog.AlertDialogBuilder
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

const val SEPARATOR: String = ">"

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
    lateinit var imageModeMetaSpinner: Spinner
    lateinit var spinnerContainer: LinearLayout
    lateinit var useLastChildCheckBox: AppCompatCheckBox
    lateinit var deleteButton: CommonShapeButton
    lateinit var saveButton: CommonShapeButton
    lateinit var contentSelectTextView: TextView
    lateinit var nextLevelButton: AppCompatButton
    lateinit var preLevelButton: AppCompatButton
    lateinit var imageModeMetaList: LinkedList<ImageModeMeta>
    //data
    private var images = LinkedList<ImageInfo>()
    lateinit var adapter: BaseQuickAdapter<ImageInfo, CustomBaseViewHolder>
    var html = ""
    var sessionUrl = ""
    var nextPageUrl = ""
    var imageModeMeta = ImageModeMeta.default()

    private lateinit var document: Document //原始文档
    var targetImageSelector = ""  //目标图片的Selector
    var contentSelector = ""  //内容选择器
    var contentSelectorPartList = LinkedList<String>()
    var contentSelectorContainerIndex = -1

    var loadCompleted = false
    //图片属性
    private var imageAttributes = LinkedList<KeyValue>()
    //链接选择器
    private var linkSelectors = LinkedList<KeyValue>()
    //图片提取器
    private var imageModeMetaKeyValues = LinkedList<KeyValue>()
    private var isLoading=true
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
            }
        }
        imageModeMetaSpinner = view.findViewById(R.id.image_mode_meta_spinner)
        contentSelectTextView = view.findViewById(R.id.content_selector)
        //下一个层级
        nextLevelButton = view.findViewById<AppCompatButton>(R.id.next_level).apply {
            setOnClickListener {
                if (contentSelectorPartList.isNotEmpty()) {
                    if (contentSelectorContainerIndex < this@ImageModeModeFragment.contentSelectorPartList.size - 1) {
                        this@ImageModeModeFragment.contentSelectorContainerIndex += 1
                        this@ImageModeModeFragment.contentSelector = contentSelectorPartList.subList(
                            0,
                            this@ImageModeModeFragment.contentSelectorContainerIndex + 1
                        ).joinToString(SEPARATOR)
                        this@ImageModeModeFragment.contentSelectTextView.text =
                            this@ImageModeModeFragment.contentSelector
                        this@ImageModeModeFragment.refresh()
                    }
                }
            }
        }
        //上一个层级
        preLevelButton = view.findViewById<AppCompatButton>(R.id.pre_level).apply {
            setOnClickListener {
                if (contentSelectorPartList.isNotEmpty()) {
                    if (contentSelectorContainerIndex > 0) {
                        this@ImageModeModeFragment.contentSelectorContainerIndex -= 1
                        this@ImageModeModeFragment.contentSelector = contentSelectorPartList.subList(
                            0,
                            this@ImageModeModeFragment.contentSelectorContainerIndex + 1
                        ).joinToString(SEPARATOR)
                        this@ImageModeModeFragment.contentSelectTextView.text =
                            this@ImageModeModeFragment.contentSelector
                        this@ImageModeModeFragment.refresh()
                    }
                }
            }
        }
        nextPageSpinner = view.findViewById(R.id.spinner_next)
        attrSpinner = view.findViewById(R.id.spinner_attr)
        useLastChildCheckBox = view.findViewById<AppCompatCheckBox>(R.id.use_last_child).apply {
            this.setOnCheckedChangeListener { _, isChecked ->
                imageModeMeta.replaceNthChildWithLastChild = isChecked
                refresh()
            }
        }
        deleteButton = view.findViewById<CommonShapeButton>(R.id.delete).apply {
            setOnClickListener {
                deleteSiteRecord()
            }
        }
        saveButton = view.findViewById<CommonShapeButton>(R.id.save).apply {
            setOnClickListener {
                saveSiteRecord()
            }
        }
        spinnerContainer = view.findViewById(R.id.spiner_container)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        header = view.findViewById(R.id.header)
        containerWrapper = view.findViewById(R.id.container_wrapper)
        view.findViewById<View>(R.id.setting)?.apply {
            setOnClickListener {
                if (!isLoading){
                    showSettingView()
                }else{
                    requireContext().apply {
                        showToast(getString(R.string.tip_wait_for_load_finish))
                    }
                }
            }
        }
        view.findViewById<View>(R.id.back)?.apply {
            setOnClickListener {
                onBackPressed()
            }
        }
        container = view.findViewById(R.id.container)
        view.findViewById<View>(R.id.downloadButton)?.apply {
            setOnClickListener {
                showDownloadDialog()
            }
        }
        spinnerContainer.onGlobalLayoutComplete {
            hideSettingView()
        }
        //监听数据
        viewModel.resultCodeLiveData.observe(this, Observer<Int> {
            when (it) {
                ResultCode.LOAD_FAIL -> {
                    isLoading=false
                    requireContext().apply {
                        showToast(getString(R.string.load_fail))
                    }
                    adapter.loadMoreFail()
                }
                ResultCode.SAVE_SUCCESS -> {
                    requireContext().apply {
                        showToast(getString(R.string.save_success))
                    }
                }
                ResultCode.SAVE_FAIL -> {
                    requireContext().apply {
                        showToast(getString(R.string.save_fail))
                    }
                }
                ResultCode.DELETE_FAIL -> {
                    requireContext().apply {
                        showToast(getString(R.string.delete_fail))
                    }
                }
                ResultCode.DELETE_SUCCESS -> {
                    requireContext().apply {
                        showToast(getString(R.string.delete_success))
                    }
                }
            }
        })
        viewModel.nextPageUrlLiveData.observe(this, Observer<String> {
            if (it != nextPageUrl) {
                Log.d("nextPageUrlLiveData", "is $it")
                nextPageUrl = it
            } else {
                adapter.loadMoreEnd()
                loadCompleted = true
            }
        })
        viewModel.refreshImagesLiveData.observe(this, Observer<List<ImageInfo>> {
            isLoading=false
            loadCompleted = false
            images.clear()
            images.addAll(it)
            adapter.setNewData(images)
        })
        viewModel.loadMoreImagesLiveData.observe(this, Observer<List<ImageInfo>> {
            val oldSize = images.size
            images.addAll(it)
            adapter.notifyItemRangeInserted(oldSize, it.size)
            adapter.loadMoreComplete()
        })
        viewModel.htmlLiveData.observe(this, Observer<String> {
            html = it
            //获取imageAttributes和LinkSelector
            if (linkSelectors.size == 0 && imageAttributes.size == 0) {
                launch(Dispatchers.IO) {
                    val doc = Jsoup.parse(html)
                    doc.setBaseUri(sessionUrl)
                    imageAttributes = getImageAttributes(doc)
                    linkSelectors = getLinkSelectors(doc)
                    launch(Dispatchers.Main) {
                        initAttrSpinner()
                        initNextPageSpinner()
                        //初始化数据后更新ImageModeMeta
                        if (imageModeMetaList.isNotEmpty()) {
                            resetImageSiteMetaSetting(imageModeMetaList[0])
                        }
                        refresh()
                    }
                }
            }
        })
        viewModel.documentLiveData.observe(this, Observer {
            document = it
        })
        viewModel.imageModeMetasLiveData.observe(this, Observer {
            imageModeMetaList = LinkedList(it)
            initImageModeMetaSpinner()
        })
    }


    private fun resetImageSiteMetaSetting(newImageModeMeta: ImageModeMeta) {
        imageModeMeta = newImageModeMeta
        contentSelectorPartList.clear()
        contentSelectorPartList.addAll(contentSelector.split(SEPARATOR).map { str -> str.trim() })
        contentSelectorContainerIndex = contentSelectorPartList.size - 1
        updateImageSiteMetaSettingView(imageModeMeta)
    }

    //根据选择器来更新View
    private fun updateImageSiteMetaSettingView(imageModeMeta: ImageModeMeta) {
        val index = imageModeMetaList.indexOf(imageModeMeta)
        if (index >= 0) {
            imageModeMetaSpinner.setSelection(index)
        }

        useLastChildCheckBox.isChecked = imageModeMeta.replaceNthChildWithLastChild
        contentSelectTextView.text = imageModeMeta.contentSelector
        val imageAttributeKv = KeyValue.parse(imageModeMeta.imageAttrTitle)
        imageAttributes.forEachIndexed { index, keyValue ->
            if (keyValue.key == imageAttributeKv.key && keyValue.value == imageAttributeKv.value) {
                attrSpinner.setSelection(index)
            }
        }
        val nextPageSelectorKV = KeyValue.parse(imageModeMeta.nextPageSelectorTitle)
        linkSelectors.forEachIndexed { index, keyValue ->
            if (keyValue.key == nextPageSelectorKV.key && keyValue.value == nextPageSelectorKV.value) {
                nextPageSpinner.setSelection(index)
            }
        }
    }

    private fun refresh() {
        isLoading=true
        viewModel.refresh(
            url = sessionUrl,
            contentSelector = contentSelector,
            imageAttr = imageModeMeta.imageAttr,
            nextPageSelector = getRealNextPageSelector()
        )
    }

    private fun deleteSiteRecord() {
        AlertDialogBuilder()
            .setTitle(R.id.title, getString(R.string.delete_record))
            .setContent(R.id.content, getString(R.string.tip_delete_record))
            .setOnPositive(Runnable {
                imageModeMeta = if (imageModeMetaList.indexOf(imageModeMeta) >= 0) {
                    imageModeMetaList.remove(imageModeMeta)
                    if (imageModeMetaList.isNotEmpty()) {
                        imageModeMetaList[0]
                    } else {
                        ImageModeMeta.default()
                    }
                } else {
                    ImageModeMeta.default()
                }
                initImageModeMetaSpinner()
                resetImageSiteMetaSetting(imageModeMeta)
                viewModel.deleteImageModeMeta(imageModeMeta)
            }).build(requireContext()).show()
    }

    private fun saveSiteRecord() {
        val host = Uri.parse(sessionUrl).host ?: ""
        if (host.isNotBlank() && imageAttributes.isNotEmpty() && linkSelectors.isNotEmpty()) {
            viewModel.upSertImageModeMeta(
                ImageModeMeta(
                    uid = 0,
                    imageAttr = imageModeMeta.imageAttr,
                    nextPageSelector = imageModeMeta.nextPageSelector,
                    site = host,
                    replaceNthChildWithLastChild = useLastChildCheckBox.isChecked,
                    contentSelector = contentSelector,
                    uniqueKey = host + "_" + imageModeMeta.imageAttr + "_" + imageModeMeta.nextPageSelector,
                    imageAttrTitle = imageAttributeKeyValue.getDescription(),
                    nextPageSelectorTitle = linkSelectorKeyValue.getDescription()
                )
            )
        }

    }

    private fun initImageModeMetaSpinner() {
        imageModeMetaKeyValues.clear()
        imageModeMetaList.forEachIndexed { index, _ ->
            imageModeMetaKeyValues.add(KeyValue(key = index.toString(), value = ""))
        }
        imageModeMetaSpinner.adapter = KeyValueAdapter(data = imageModeMetaKeyValues)
        imageModeMetaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (imageModeMetaList.isNotEmpty()) {
                    resetImageSiteMetaSetting(imageModeMetaList.first())
                }
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (imageModeMetaList.size > position) {
                    resetImageSiteMetaSetting(imageModeMetaList[position])
                    refresh()
                }
            }
        }
    }

    private lateinit var imageAttributeKeyValue: KeyValue
    private fun initAttrSpinner() {
        attrSpinner.adapter = KeyValueAdapter(data = imageAttributes)
        attrSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (imageAttributes.size > 0) {
                    imageModeMeta.imageAttr = imageAttributes[0].value
                    imageAttributeKeyValue = imageAttributes[0]
                }
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                imageModeMeta.imageAttr = imageAttributes[position].value
                imageAttributeKeyValue = imageAttributes[position]
                refresh()
            }
        }
    }

    private lateinit var linkSelectorKeyValue: KeyValue
    private fun initNextPageSpinner() {
        nextPageSpinner.adapter = KeyValueAdapter(data = linkSelectors)
        nextPageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if (linkSelectors.size > 0 && imageModeMeta.nextPageSelector.isBlank()) {
                    imageModeMeta.nextPageSelector = linkSelectors[0].key
                    linkSelectorKeyValue = linkSelectors[0]
                }
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                linkSelectorKeyValue = linkSelectors[position]
                imageModeMeta.nextPageSelector = linkSelectors[position].key
                refresh()
            }
        }
    }


    private fun getImageAttributes(document: Document): LinkedList<KeyValue> {
        val list = LinkedList<KeyValue>()
        document.select("img").forEach { ele ->
            ele.attributes().forEach {
                list.add(KeyValue(key = it.value.trim(), value = it.key.trim()))
            }
        }
        return list
    }

    private fun getLinkSelectors(document: Document): LinkedList<KeyValue> {
        val list = LinkedList<KeyValue>()
        document.select("a").forEach { ele ->
            list.add(KeyValue(key = ele.cssSelector().trim(), value = ele.text().trim()))
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


    private fun showDownloadDialog() {
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
            BaseQuickAdapter<ImageInfo, CustomBaseViewHolder>(R.layout.item_scroll_parallax_image, images) {
            override fun convert(helper: CustomBaseViewHolder, item: ImageInfo) {
                helper.loadNoCropImage(R.id.image, url = item.url, referer = session.url)
                helper.itemView.findViewById<ScrollParallaxImageView>(R.id.image).setParallaxStyles(
                    VerticalMovingStyle()
                )
            }
        }
        adapter.setPreLoadNumber(2)
        adapter.setOnLoadMoreListener({
            //如果加载完成，则不再加载
            if (loadCompleted) {
                adapter.loadMoreEnd()
                return@setOnLoadMoreListener
            }
            Log.d("OnLoadMoreListener", "nextPageUrl is $nextPageUrl")
            if (nextPageUrl.isBlank()) {
                adapter.loadMoreComplete()
            } else {
                viewModel.loadMore(
                    url = nextPageUrl,
                    contentSelector = contentSelector,
                    imageAttr = imageModeMeta.imageAttr,
                    nextPageSelector = getRealNextPageSelector()
                )
            }
        }, recyclerView)
        adapter.disableLoadMoreIfNotFullPage()
        adapter.setEnableLoadMore(true)
        initImageItemContextMenu(adapter)
        recyclerView.adapter = adapter
        sessionUrl = session.url
        viewModel.loadImageModeMetas(sessionUrl)
    }

    private fun getRealNextPageSelector(): String {
        return if (imageModeMeta.replaceNthChildWithLastChild) {
            StringUtil.replaceLast(imageModeMeta.nextPageSelector, "nth-child\\(\\d\\)", "last-child")
        } else {
            imageModeMeta.nextPageSelector
        }
    }

    var imageItemContextMenu: Dialog? = null
    private fun initImageItemContextMenu(adapter: BaseQuickAdapter<ImageInfo, CustomBaseViewHolder>?) {
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
                            downloadImage(
                                url = images[position].url,
                                referer = sessionManager.selectedSession?.url ?: ""
                            )
                            imageItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_share), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            if (!requireContext().shareLink(
                                    title = getString(R.string.share_image),
                                    url = images[position].url
                                )
                            ) {
                                requireContext().showToast(getString(R.string.tip_share_fail))
                            }
                            imageItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.menu_copy_link), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            requireContext().copyText(getString(R.string.link), images[position].url)
                            requireContext().showToast(getString(R.string.tip_copy_link))
                            imageItemContextMenu?.dismiss()
                        }
                    }),
                    MenuItem(label = getString(R.string.set_as_target), action = object : Action<MenuItem> {
                        override fun execute(data: MenuItem) {
                            targetImageSelector = images[position].selector
                            val parentContainerLastIndex = targetImageSelector.lastIndexOf(SEPARATOR)
                            contentSelector = if (parentContainerLastIndex >= 0) {
                                targetImageSelector.substring(0, parentContainerLastIndex).trim()
                            } else {
                                ""
                            }
                            contentSelectorPartList.clear()
                            contentSelectorPartList.addAll(contentSelector.split(SEPARATOR).map { it.trim() })
                            contentSelectorContainerIndex = contentSelectorPartList.size - 1
                            contentSelectTextView.text = contentSelector
                            requireContext().showToast(getString(R.string.tip_set_content_selector))
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
        for (image in images) {
            val fileName = DownloadUtils.guessFileName("", image.url, "")
            downloadManager.download(
                Download(
                    url = image.url,
                    fileName = fileName,
                    referer = referer,
                    contentType = "image/*"
                )
            )
        }
    }
}

data class KeyValue(var key: String = "", var value: String = "") {
    fun getDescription(): String {
        if (key.isNotBlank() && value.isNotBlank()) return "$value => $key"
        if (key.isNotBlank()) return key
        if (value.isNotBlank()) return value
        return ""
    }

    companion object {
        fun parse(description: String): KeyValue {
            val kv = KeyValue()
            val list = description.split("=>")
            if (list.isNotEmpty()) {
                kv.value = list[0].trim()
            }
            if (list.size > 1) {
                kv.key = list[1].trim()
            }
            return kv
        }
    }
}

class KeyValueAdapter(var data: List<KeyValue>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (convertView != null) {
            (convertView.tag as? ViewHolder)?.apply {
                this.title.text = data[position].getDescription()
            }
            return convertView
        }
        val view = View.inflate(parent.context, R.layout.item_title_selector, null)
        ViewHolder(view).apply {
            this.title.text = data[position].getDescription()
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