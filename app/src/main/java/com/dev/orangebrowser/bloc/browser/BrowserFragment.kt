package com.dev.orangebrowser.bloc.browser


import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.base.support.BackHandler
import com.dev.base.support.LifecycleAwareFeature
import com.dev.base.support.UserInteractionHandler
import com.dev.base.support.ViewBoundFeatureWrapper
import com.dev.browser.concept.Engine
import com.dev.browser.engine.SystemEngineView
import com.dev.browser.feature.session.*
import com.dev.browser.feature.sitepermissions.SitePermissionsFeature
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.integration.*
import com.dev.orangebrowser.bloc.browser.integration.helper.*
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.view.WebViewToggleBehavior
import com.dev.view.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_browser.*
import java.util.*
import javax.inject.Inject


class BrowserFragment : BaseFragment(), BackHandler, UserInteractionHandler {
    @Inject
    lateinit var applicationContext:Context
    @Inject
    lateinit var engine:Engine
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var sessionUseCases: SessionUseCases
    @Inject
    lateinit var tabsUseCases: TabsUseCases
    //    @Inject
//    lateinit var tabsUseCases: TabsUseCases
    lateinit var viewModel: BrowserViewModel
    lateinit var activityViewModel: MainViewModel

    private val sessionFeature = ViewBoundFeatureWrapper<SessionFeature>()
    private val thumbnailsFeature = ViewBoundFeatureWrapper<ThumbnailsFeature>()
    private val windowFeature = ViewBoundFeatureWrapper<WindowFeature>()
    private val contextMenuIntegration = ViewBoundFeatureWrapper<ContextMenuIntegration>()
    private val sessionManagerListenerIntegration=ViewBoundFeatureWrapper<SessionManagerListenerIntegration>()
//    private val downloadsFeature = ViewBoundFeatureWrapper<DownloadsFeature>()
//    private val promptsFeature = ViewBoundFeatureWrapper<PromptFeature>()
    private val fullScreenFeature = ViewBoundFeatureWrapper<FullScreenFeature>()
//    private val customTabsIntegration = ViewBoundFeatureWrapper<CustomTabsIntegration>()
//    private val findInPageIntegration = ViewBoundFeatureWrapper<FindInPageIntegration>()
    private val sitePermissionFeature = ViewBoundFeatureWrapper<SitePermissionsFeature>()
    private val pictureInPictureIntegration = ViewBoundFeatureWrapper<PictureInPictureIntegration>()
    private val topBarIntegration = ViewBoundFeatureWrapper<TopBarIntegration>()
    private val bottomBarIntegration = ViewBoundFeatureWrapper<BottomBarIntegration>()
    private val miniBottomBarIntegration = ViewBoundFeatureWrapper<MiniBottomBarIntegration>()
    private val topPanelMenuIntegration = ViewBoundFeatureWrapper<TopPanelMenuIntegration>()
    private val bottomPanelMenuIntegration = ViewBoundFeatureWrapper<BottomPanelMenuIntegration>()
    private val webViewScrollHandlerIntegration = ViewBoundFeatureWrapper<WebViewScrollHandlerIntegration>()
    private val webViewLifeCycleIntegration = ViewBoundFeatureWrapper<WebViewLifeCycleIntegration>()
    private val styleIntegration = ViewBoundFeatureWrapper<StyleIntegration>()

    private val backButtonHandler: List<ViewBoundFeatureWrapper<*>> = listOf(
//        fullScreenFeature,
//        findInPageIntegration,
////    toolbarIntegration,
//        sessionFeature,
//        customTabsIntegration
    )

    lateinit var binding: FragmentBrowserBinding
    //
    val backHandlers = LinkedList<BackHandler>()
    //
    lateinit var  fullScreenHelper:FullScreenHelper
    //
    init {
        backHandlers.add(adaptToBackHandler(fullScreenFeature))
        backHandlers.add(adaptToBackHandler(sessionFeature))
    }

    private fun <T : LifecycleAwareFeature> adaptToBackHandler(wrapper: ViewBoundFeatureWrapper<T>): BackHandler {
        return object : BackHandler {
            override fun onBackPressed(): Boolean {
                return wrapper.onBackPressed()
            }
        }
    }

    //
    val sessionId: String
        get() = arguments?.getString(SESSION_ID) ?: ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(BrowserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBrowserBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        return binding.root
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_browser
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        super.onActivityCreated(savedInstanceState)
    }

    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {

        val session=sessionManager.findSessionById(sessionId) ?: sessionManager.selectedSessionOrThrow
        val bottomPanelHelper = BottomPanelHelper(binding, this)
        val topPanelHelper = TopPanelHelper(binding, this, bottomPanelHelper)
        val webViewVisionHelper=WebViewVisionHelper(binding)
        //将EngineView添加到上面去
        binding.webViewContainer.removeAllViews()
        val engineView= SystemEngineView(requireContext().applicationContext)
        binding.webViewContainer.addView(engineView,FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        //get behavior
        (binding.webViewContainer.layoutParams as? CoordinatorLayout.LayoutParams)?.apply {
            (this.behavior as? WebViewToggleBehavior)?.apply {
                this.setSession(session)
                this.setHelper(webViewVisionHelper)
            }
        }
        fullScreenHelper= FullScreenHelper(binding,requireActivity())


        bottomBarIntegration.set(
            feature = BottomBarIntegration(
                binding = binding,
                fragment = this,
                savedInstanceState = savedInstanceState,
                bottomPanelHelper = bottomPanelHelper,
                sessionUseCases = sessionUseCases,
                sessionManager = sessionManager,
                session = session
            ), owner = this, view = binding.root
        )
        miniBottomBarIntegration.set(feature = MiniBottomBarIntegration(
            binding=binding,
            session = session,
            sessionUseCases = sessionUseCases,
            webViewVisionHelper = webViewVisionHelper,
            fragment = this
        ),owner = this,view=binding.root)
        topBarIntegration.set(
            feature = TopBarIntegration(
                binding = binding,
                fragment = this,
                savedInstanceState = savedInstanceState,
                topPanelHelper = topPanelHelper,
                session = session,
                sessionUseCases = sessionUseCases
            ), owner = this, view = binding.root
        )
        bottomPanelMenuIntegration.set(
            feature = BottomPanelMenuIntegration(
                binding = binding,
                fragment = this,
                savedInstanceState = savedInstanceState,
                bottomPanelHelper = bottomPanelHelper,
                sessionUseCases = sessionUseCases,
                session = session
            ), owner = this, view = binding.root
        )
        topPanelMenuIntegration.set(
            feature = TopPanelMenuIntegration(
                binding = binding,
                fragment = this,
                savedInstanceState = savedInstanceState,
                topPanelHelper = topPanelHelper
            ), owner = this, view = binding.root
        )
        webViewScrollHandlerIntegration.set(
            feature = WebViewScrollHandlerIntegration(
                binding = binding,
                session = session,
                webViewVisionHelper = webViewVisionHelper
            ), owner = this, view = binding.root
        )
        webViewLifeCycleIntegration.set(
            WebViewLifeCycleIntegration(binding = binding, owner = this,engineView =engineView),
            owner = this,
            view = binding.root
        )
        styleIntegration.set(
            feature = StyleIntegration(binding=binding,fragment = this,session = session,sessionManager = sessionManager),
            owner = this,
            view = binding.root
        )
        //
        contextMenuIntegration.set(
            feature = ContextMenuIntegration(requireContext(),fragmentManager!!,
                sessionManager,tabsUseCases,web_view_container,engineView,sessionId),
            owner = this,
            view = binding.root
        )
        sessionManagerListenerIntegration.set(
            feature = SessionManagerListenerIntegration(session=session,activity = RouterActivity,sessionManager = sessionManager),
            owner = this,
            view = binding.root
        )
        sessionFeature.set(
            feature = SessionFeature(
                sessionManager,
                sessionUseCases,
                engineView,
                session.id
            ), owner = this, view = binding.root
        )
        thumbnailsFeature.set(
            feature = ThumbnailsFeature(requireContext(), engineView, sessionManager),
            owner = this,
            view = binding.root
        )

        windowFeature.set(
            feature = WindowFeature(
                engine = engine,
                sessionManager = sessionManager
            ),
            owner = this,
            view = binding.root
        )
        //pictureInPicture
        pictureInPictureIntegration.set(
            feature = PictureInPictureIntegration(sessionManager,requireActivity()),
            owner = this,
            view = binding.root
        )
        //全局模式
        fullScreenFeature.set(
            feature = FullScreenFeature(
                sessionManager = sessionManager,
                sessionUseCases = sessionUseCases,
                sessionId = sessionId, fullScreenChanged = ::fullScreenChanged
            ),
            owner = this,
            view = binding.root)

        sitePermissionFeature.set(
            feature = SitePermissionsFeature(
                anchorView = binding.bottomBar,
                sessionManager = sessionManager
            ) { permissions ->
                requestPermissions(permissions, REQUEST_CODE_APP_PERMISSIONS)
            },
            owner = this,
            view = binding.root
        )

    }

    override fun initData(savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }






    private fun fullScreenChanged(enabled: Boolean) {
        val session=sessionManager.findSessionById(sessionId)
        session?.apply {
            fullScreenHelper.toggleFullScreen(session =this,fullScreen = enabled)
        }
    }

    @Suppress("ReturnCount")
    override fun onBackPressed(): Boolean {
        for (backHandler in backHandlers) {
            if (backHandler.onBackPressed()) {
                return true
            }
        }
        RouterActivity?.loadHomeFragment(sessionId)
        return true
    }

    override fun onHomePressed(): Boolean {
        var handled = false
        pictureInPictureIntegration.withFeature {
            handled = it.onHomePressed()
        }

        return handled
    }

    override fun onPictureInPictureModeChanged(enabled: Boolean) {
        val fullScreenMode = sessionManager.selectedSession?.fullScreenMode ?: false
        // If we're exiting PIP mode and we're in fullscreen mode, then we should exit fullscreen mode as well.
        if (!enabled && fullScreenMode) {
            onBackPressed()
            fullScreenChanged(false)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            REQUEST_CODE_APP_PERMISSIONS -> sitePermissionFeature.withFeature {
                it.onPermissionsResult(grantResults)
            }
        }
    }

    override fun onDestroy() {
        binding.webViewContainer.removeAllViews()
        super.onDestroy()
    }
    override fun onDetach() {
        RouterActivity?.apply {
            StatusBarUtil.setStatusBarColor(RouterActivity!!,activityViewModel.theme.value!!.colorPrimary)
        }
        super.onDetach()
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


