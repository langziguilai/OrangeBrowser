package com.dev.orangebrowser.bloc.browser


import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.dev.base.support.BackHandler
import com.dev.base.BaseFragment
import com.dev.base.extension.*
import com.dev.base.support.UserInteractionHandler
import com.dev.base.support.ViewBoundFeatureWrapper
import com.dev.orangebrowser.R
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.extension.appData
import com.dev.util.DensityUtil
import com.dev.view.GridView
import com.dev.view.IconfontTextView
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import es.dmoral.toasty.Toasty
import java.util.*


class BrowserFragment : BaseFragment(), BackHandler, UserInteractionHandler {

    //    @Inject
//    lateinit var sessionManager: SessionManager
//    @Inject
//    lateinit var sessionUseCases: SessionUseCases
//    @Inject
//    lateinit var tabsUseCases: TabsUseCases
    lateinit var viewModel: BrowserViewModel
    //    private val sessionFeature = ViewBoundFeatureWrapper<SessionFeature>()
//    //    private val toolbarIntegration = ViewBoundFeatureWrapper<ToolbarIntegration>()
//    private val contextMenuIntegration = ViewBoundFeatureWrapper<ContextMenuIntegration>()
//    private val downloadsFeature = ViewBoundFeatureWrapper<DownloadsFeature>()
//    private val promptsFeature = ViewBoundFeatureWrapper<PromptFeature>()
//    private val fullScreenFeature = ViewBoundFeatureWrapper<FullScreenFeature>()
//    private val customTabsIntegration = ViewBoundFeatureWrapper<CustomTabsIntegration>()
//    private val findInPageIntegration = ViewBoundFeatureWrapper<FindInPageIntegration>()
//    private val sitePermissionFeature = ViewBoundFeatureWrapper<SitePermissionsFeature>()
//    private val pictureInPictureIntegration = ViewBoundFeatureWrapper<PictureInPictureIntegration>()
    private val backButtonHandler: List<ViewBoundFeatureWrapper<*>> = listOf(
//        fullScreenFeature,
//        findInPageIntegration,
////    toolbarIntegration,
//        sessionFeature,
//        customTabsIntegration
    )
    //view
    lateinit var topBar: View
    lateinit var topMenuPanel: GridView
    lateinit var overLayerForTopMenuPanel: View //遮罩层
    lateinit var bottomBar: View
    lateinit var bottomMenuPanel: GridView
    private lateinit var bottomMenuGridView: GridView
    lateinit var overLayerForBottomMenuPanel: View //遮罩层
    lateinit var backBtn: IconfontTextView
    lateinit var forwardBtn: IconfontTextView
    lateinit var searchBtn: IconfontTextView
    lateinit var counterNumber: TextView
    lateinit var menuBtn: IconfontTextView
    lateinit var webView: WebView
    //
    private val backHandlers = LinkedList<BackHandler>()
    private var bottomPanelBackHandler: BackHandler? = null
    private var topPanelBackHandler: BackHandler? = null
    //
    private val sessionId: String
        get() = arguments?.getString(SESSION_ID) ?: ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(BrowserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding=FragmentBrowserBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_browser
    }


    override fun initView(view: View, savedInstanceState: Bundle?) {

        initTopBar(view, savedInstanceState)
        initBottomBar(view, savedInstanceState)
        initWebView(view, savedInstanceState)

    }

    //TODO:webview
    private fun initWebView(view: View, savedInstanceState: Bundle?) {
        webView = view.findViewById(R.id.web_view)
        webView.loadUrl("https://www.ihotmm.com")
        val listener = object : OnWebViewScrollDirectionListener {
            override fun onScrollDirection(direction: Int) {
                if (direction == OnWebViewScrollDirectionListener.UP) {
                    bottomBar.animate().translationY(bottomBar.height.toFloat()).setDuration(FAST_ANIMATION)
                        .setInterpolator(
                            AccelerateInterpolator()
                        ).start()
                    topBar.animate().translationY(-topBar.height.toFloat()).setDuration(FAST_ANIMATION).setInterpolator(
                        AccelerateInterpolator()
                    ).start()
                } else {
                    bottomBar.animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(
                        AccelerateInterpolator()
                    ).start()
                    topBar.animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(
                        AccelerateInterpolator()
                    ).start()
                }
            }

        }
        webView.setOnTouchListener(WebViewOnTouchListener(listener))
    }

    //初始化TopBar
    private fun initTopBar(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.search_text).setOnClickListener {
            if (!overLayerForTopMenuPanel.isHidden()) {
                toggleTopPanel(Runnable {
                    RouterActivity?.loadSearchFragment()
                })
            } else {
                RouterActivity?.loadSearchFragment()
            }
        }
        view.findViewById<View>(R.id.top_menu).setOnClickListener {
            toggleTopPanel()
        }
        topMenuPanel = view.findViewById(R.id.top_menu_panel)
        initTopMenuGridView(topMenuPanel)
        overLayerForTopMenuPanel = view.findViewById(R.id.over_layer_top_panel)
        overLayerForTopMenuPanel.setOnClickListener {
            toggleTopPanel()
        }
        //initial hide
        topMenuPanel.apply {
            onGlobalLayoutComplete {
                it.animate().translationY(-this.height.toFloat()).setDuration(0).start()
            }
        }
        topPanelBackHandler = object : BackHandler {
            override fun onBackPressed(): Boolean {
                toggleTopPanel()
                return true
            }

        }
    }

    private fun initTopMenuGridView(topMenuPanel: GridView) {
        val adapter = object : BaseQuickAdapter<ActionItem, CustomBaseViewHolder>(
            R.layout.item_top_action_item,
            appData.topMenuActionItems
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
                helper.setText(R.id.icon, item.iconRes)
                helper.setText(R.id.name, item.nameRes)
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            onTopMenuActionItemClick(appData.topMenuActionItems[position])
        }
        topMenuPanel.adapter = adapter
    }

    //TODO:处理Top Menu Item的点击
    private fun onTopMenuActionItemClick(actionItem: ActionItem) {
        when (actionItem.iconRes) {
            //扫码
            R.string.ic_scan -> {
                toggleTopPanel(Runnable {
                    RouterActivity?.loadScanFragment()
                })
            }
            //TODO:分享
            R.string.ic_share -> {

            }
            //阅读模式
            R.string.ic_read -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //看图模式
            R.string.ic_image -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //标记广告
            R.string.ic_ad_mark -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //页内查找
            R.string.ic_search -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //离线保存
            R.string.ic_save -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //翻译
            R.string.ic_translate -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //源码
            R.string.ic_code -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //资源嗅探
            R.string.ic_resources_fang -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //添加到主页
            R.string.ic_store -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
            //天网
            R.string.ic_sky_net -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleTopPanel(runnable: Runnable = Runnable { }) {
        //如果是隐藏的，那么显示
        if (overLayerForTopMenuPanel.isHidden()) {
            showTopPanel(runnable)
            backHandlers.add(0, topPanelBackHandler!!)
        } else {
            hideTopPanel(runnable)
            backHandlers.remove(topPanelBackHandler)
        }
    }

    private fun hideTopPanel(runnable: Runnable = Runnable { }) {
        overLayerForTopMenuPanel.hide()
        topMenuPanel.apply {
            animate().translationY(-this.height.toFloat()).setDuration(FAST_ANIMATION).setInterpolator(
                AccelerateInterpolator()
            ).withEndAction(runnable).start()
        }
    }

    private fun showTopPanel(runnable: Runnable = Runnable { }) {
        overLayerForTopMenuPanel.show()
        topMenuPanel.apply {
            animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator())
                .withEndAction(runnable).start()
        }
        //如果bottomPanelBar是显示的，那么隐藏bottomPanel
        if (overLayerForBottomMenuPanel.isShowing()) {
            toggleBottomPanel()
        }
    }

    //初始化BottomBar
    private fun initBottomBar(view: View, savedInstanceState: Bundle?) {
        backBtn = view.findViewById(R.id.back)
        forwardBtn = view.findViewById(R.id.forward)
        //点击返回主页
        view.findViewById<View>(R.id.home).setOnClickListener {
            RouterActivity?.loadHomeFragment(sessionId)
        }
        counterNumber = view.findViewById(R.id.counterNumber)
        //展示TabFragment
        view.findViewById<View>(R.id.counter).setOnClickListener {
            showTabs(sessionId)
        }
        menuBtn = view.findViewById(R.id.menu)
        menuBtn.setOnClickListener {
            toggleBottomPanel()
        }
        bottomMenuPanel = view.findViewById(R.id.bottom_menu_panel)
        bottomMenuGridView = view.findViewById(R.id.bottom_menu_grid_view)
        initBottomMenuGridView(bottomMenuGridView)
        view.findViewById<View>(R.id.bottom_menu_grid_view_close).setOnClickListener {
            toggleBottomPanel()
        }
        overLayerForBottomMenuPanel = view.findViewById(R.id.over_layer_bottom_panel)
        overLayerForBottomMenuPanel.setOnClickListener {
            toggleBottomPanel()
        }
        bottomMenuPanel.apply {
            onGlobalLayoutComplete {
                it.animate().translationY(this.height.toFloat() + DensityUtil.dip2px(requireContext(), 16f))
                    .setDuration(0).start()
            }
        }
        bottomPanelBackHandler = object : BackHandler {
            override fun onBackPressed(): Boolean {
                toggleBottomPanel()
                return true
            }
        }
    }

    private fun initBottomMenuGridView(bottomMenuGridView: GridView) {
        val adapter = object : BaseQuickAdapter<ActionItem, CustomBaseViewHolder>(
            R.layout.item_bottom_action_item,
            appData.bottomMenuActionItems
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
                if (item.active) {
                    helper.setTextColor(R.id.icon, resources.getColor(R.color.colorPrimaryActive))
                    helper.setTextColor(R.id.name, resources.getColor(R.color.colorPrimaryActive))
                } else {
                    helper.setTextColor(R.id.icon, resources.getColor(R.color.colorPrimary))
                    helper.setTextColor(R.id.name, resources.getColor(R.color.colorPrimary))
                }
                helper.setText(R.id.icon, item.iconRes)
                helper.setText(R.id.name, item.nameRes)
            }
        }
        adapter.setOnItemClickListener { _, view, position ->
            onBottomMenuActionItemClick(view, appData.bottomMenuActionItems[position])
        }
        bottomMenuGridView.adapter = adapter
    }

    //TODO:处理Bottom Menu Item的点击
    private fun onBottomMenuActionItemClick(view: View, actionItem: ActionItem) {
        when (actionItem.iconRes) {
            //无图模式
            R.string.ic_forbid_image -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                } else {
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimary))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            //隐身
            R.string.ic_desktop -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                } else {
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimary))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            //全局视野
            R.string.ic_fullscreen -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                } else {
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimary))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            //电脑
            R.string.ic_privacy -> {
                actionItem.active = !actionItem.active
                if (actionItem.active) {
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimaryActive))
                } else {
                    view.findViewById<TextView>(R.id.icon).setTextColor(resources.getColor(R.color.colorPrimary))
                    view.findViewById<TextView>(R.id.name).setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            //发现
            R.string.ic_found -> {
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadFoundFragment()
                })
            }
            //历史
            R.string.ic_history -> {
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadHistoryFragment()
                })

            }
            //书签
            R.string.ic_bookmark -> {
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadBookMarkFragment()
                })

            }
            //收藏
            R.string.ic_star -> {
                Toasty.warning(requireContext(), R.string.webview_not_available_hint, Toast.LENGTH_SHORT, true).show()
            }
            //主题
            R.string.ic_theme -> {
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadThemeFragment()
                })

            }
            //下载
            R.string.ic_download -> {
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadDownloadFragment()
                })

            }
            //设置
            R.string.ic_setting -> {
                toggleBottomPanel(Runnable {
                    RouterActivity?.loadSettingFragment()
                })

            }
            //退出
            R.string.ic_quit -> {
                RouterActivity?.quit()
            }
        }
    }

    private fun toggleBottomPanel(runnable: Runnable = Runnable { }) {
        //如果是隐藏的，那么显示
        if (overLayerForBottomMenuPanel.isHidden()) {
            showBottomPanel(runnable)
            backHandlers.add(0, bottomPanelBackHandler!!)
        } else {
            hideBottomPanel(runnable)
            backHandlers.remove(bottomPanelBackHandler)
        }
    }

    private fun showBottomPanel(runnable: Runnable = Runnable { }) {
        overLayerForBottomMenuPanel.show()
        bottomMenuPanel.apply {
            animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator())
                .withEndAction(runnable).start()
        }
    }

    private fun hideBottomPanel(runnable: Runnable = Runnable { }) {
        overLayerForBottomMenuPanel.hide()
        bottomMenuPanel.apply {
            animate().translationY(this.height.toFloat() + DensityUtil.dip2px(requireContext(), 16f)).setDuration(
                FAST_ANIMATION
            ).withEndAction(runnable).start()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun showTabs(sessionId: String) {
        RouterActivity?.loadTabFragment(sessionId)
    }

    private fun fullScreenChanged(enabled: Boolean) {

    }

    @Suppress("ReturnCount")
    override fun onBackPressed(): Boolean {
        for (backHandler in backHandlers) {
            if (backHandler.onBackPressed()) {
                return true
            }
        }
        return false
    }

    override fun onHomePressed(): Boolean {
        val handled = false

        return handled
    }

    override fun onPictureInPictureModeChanged(enabled: Boolean) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

        }
    }

    companion object {
        const val SESSION_ID = "session_id"
        private const val REQUEST_CODE_DOWNLOAD_PERMISSIONS = 1
        private const val REQUEST_CODE_PROMPT_PERMISSIONS = 2
        private const val REQUEST_CODE_APP_PERMISSIONS = 3

        fun newInstance(sessionId: String? = null): BrowserFragment = BrowserFragment().apply {
            arguments = Bundle().apply {
                putString(SESSION_ID, sessionId)
            }
        }
    }
}

interface OnWebViewScrollDirectionListener {
    companion object {
        const val UP = 1
        const val DOWN = 2
    }

    fun onScrollDirection(direction: Int)
}

class WebViewOnTouchListener(var listener: OnWebViewScrollDirectionListener) : View.OnTouchListener {
    var isDragging = false
    var initialY = -1f
    var mLastY = -1f

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val action = event.actionMasked
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                initialY = event.y
                mLastY = event.y
            }
            MotionEvent.ACTION_UP -> {
                val dy = event.y - initialY
                if (Math.abs(dy) > ViewConfiguration.getTouchSlop()) {
                    if (dy > 0) {
                        listener.onScrollDirection(OnWebViewScrollDirectionListener.DOWN)
                    } else {
                        listener.onScrollDirection(OnWebViewScrollDirectionListener.UP)
                    }
                }
                var initialY = -1f
                var mLastY = -1f
            }
            MotionEvent.ACTION_CANCEL -> {
                val dy = event.y - initialY
                if (Math.abs(dy) > ViewConfiguration.getTouchSlop()) {
                    if (dy > 0) {
                        listener.onScrollDirection(OnWebViewScrollDirectionListener.DOWN)
                    } else {
                        listener.onScrollDirection(OnWebViewScrollDirectionListener.UP)
                    }
                }
                var initialY = -1f
                var mLastY = -1f
            }
            MotionEvent.ACTION_MOVE -> {
                mLastY = event.y
            }
        }
        return false
    }

}