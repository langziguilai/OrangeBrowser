package com.dev.orangebrowser.bloc.imageMode

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.base.BaseFragment
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.history.MySectionEntity
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.getColor
import com.dev.orangebrowser.utils.PositionUtils
import com.dev.orangebrowser.utils.PositionUtils.calculateRecyclerViewLeftMargin
import com.dev.orangebrowser.utils.PositionUtils.calculateRecyclerViewTopMargin
import com.dev.orangebrowser.utils.html2article.ContentExtractor
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
        fragmentManager?.popBackStack()
        return true
    }

    @Inject
    lateinit var sessionManager: SessionManager
    lateinit var viewModel: ImageModeViewModel
    lateinit var activityViewModel: MainViewModel

    lateinit var recyclerView: RecyclerView
    lateinit var header: View
    lateinit var containerWrapper: View
    lateinit var container: LongClickFrameLayout
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
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        header = view.findViewById(R.id.header)
        containerWrapper = view.findViewById(R.id.container_wrapper)
        view.findViewById<View>(R.id.download)?.apply {
            setOnClickListener {
                showDialog()
            }
        }
        view.findViewById<View>(R.id.back)?.apply {
            setOnClickListener {
                onBackPressed()
            }
        }
        container = view.findViewById(R.id.container)
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

    private var images = LinkedList<String>()
    override fun initData(savedInstanceState: Bundle?) {
        StatusBarUtil.setIconColor(requireActivity(),activityViewModel.theme.value!!.colorPrimary)
        header.setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
        containerWrapper.setBackgroundColor(activityViewModel.theme.value!!.colorPrimary)
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
                    val elements = Jsoup.parse(article.contentHtml).select("img")
                    for (ele in elements) {
                        val imageSrc = ele.attr("abs:src").trim()
                        //如果不是直接设置数据的，就添加
                        if (!imageSrc.startsWith("data:") && imageSrc.isNotBlank()) {
                            images.add(imageSrc)
                        }
                    }
                    launch(Dispatchers.Main) {
                        val adapter = object :
                            BaseQuickAdapter<String, CustomBaseViewHolder>(R.layout.item_image_display, images) {
                            override fun convert(helper: CustomBaseViewHolder, item: String) {
                                helper.loadNoCropImage(R.id.image, url = item, referer = session.url)
                                helper.itemView.findViewById<ScrollParallaxImageView>(R.id.image).setParallaxStyles(
                                    VerticalMovingStyle()
                                )
                            }
                        }
                        initImageItemContextMenu(adapter)
                        recyclerView.adapter = adapter
                    }
                }
            })
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
                    MenuItem(label = getString(R.string.menu_download), action = object : Action<MenuItem> {
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

    //TODO:下载一个
    private fun downloadImage(url: String, referer: String) {

    }

    //TODO:下载所有
    private fun downloadAllImages() {

    }
}
