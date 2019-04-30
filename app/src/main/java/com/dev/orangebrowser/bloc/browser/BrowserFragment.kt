package com.dev.orangebrowser.bloc.browser


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.base.support.BackHandler
import com.dev.base.support.LifecycleAwareFeature
import com.dev.base.support.UserInteractionHandler
import com.dev.base.support.ViewBoundFeatureWrapper
import com.dev.browser.concept.Engine
import com.dev.browser.engine.system.SystemEngineView
import com.dev.browser.feature.downloads.DownloadsFeature
import com.dev.browser.feature.prompts.PromptFeature
import com.dev.browser.feature.session.*
import com.dev.browser.feature.sitepermissions.SitePermissionsFeature
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.integration.*
import com.dev.orangebrowser.bloc.browser.integration.helper.*
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appComponent
import com.dev.orangebrowser.bloc.browser.view.WebViewToggleBehavior
import com.dev.view.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_browser.*
import java.util.*
import javax.inject.Inject
import android.widget.RelativeLayout
import com.dev.browser.session.Session
import com.dev.orangebrowser.extension.appData


class BrowserFragment : BaseFragment(), BackHandler, UserInteractionHandler {
    @Inject
    lateinit var applicationContext: Context
    @Inject
    lateinit var engine: Engine
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var sessionUseCases: SessionUseCases
    @Inject
    lateinit var tabsUseCases: TabsUseCases

    lateinit var viewModel: BrowserViewModel
    lateinit var activityViewModel: MainViewModel

    private val sessionFeature = ViewBoundFeatureWrapper<SessionFeature>()
    private val thumbnailFeature = ViewBoundFeatureWrapper<ThumbnailFeature>()
    private val windowFeature = ViewBoundFeatureWrapper<WindowFeature>()
    private val contextMenuIntegration = ViewBoundFeatureWrapper<ContextMenuIntegration>()
    private val sessionManagerListenerIntegration = ViewBoundFeatureWrapper<SessionManagerListenerIntegration>()
    private val downloadsFeature = ViewBoundFeatureWrapper<DownloadsFeature>()
    private val promptsFeature = ViewBoundFeatureWrapper<PromptFeature>()
    private val fullScreenFeature = ViewBoundFeatureWrapper<FullScreenFeature>()
    //    private val customTabsIntegration = ViewBoundFeatureWrapper<CustomTabsIntegration>()
    private val findInPageIntegration = ViewBoundFeatureWrapper<FindInPageIntegration>()
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
    private val fullScreenProgressIntegration = ViewBoundFeatureWrapper<FullScreenProgressIntegration>()
    val thumbnailIntergration= ViewBoundFeatureWrapper<ThumbnailIntergration>()
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
    lateinit var fullScreenHelper: FullScreenHelper

    //
    init {
        backHandlers.add(adaptToBackHandler(fullScreenFeature))
        backHandlers.add(adaptToBackHandler(sessionFeature))
        backHandlers.add(adaptToBackHandler(findInPageIntegration))
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
    val session:Session
        get() = sessionManager.findSessionById(sessionId)!!
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
        sessionManager.findSessionById(sessionId)?.apply {
            this.screenNumber = BROWSER_SCREEN_NUM
        }
        var session = sessionManager.findSessionById(sessionId) ?: sessionManager.selectedSessionOrThrow
        //重新加载的时候还原为正常模式
        session.visionMode=Session.NORMAL_SCREEN_MODE
        //选中session
        sessionManager.select(session)
        val bottomPanelHelper = BottomPanelHelper(binding, this)
        val topPanelHelper = TopPanelHelper(binding, this, bottomPanelHelper)
        val webViewVisionHelper = WebViewVisionHelper(binding)
        //将EngineView添加到上面去
        binding.webViewContainer.removeAllViews()
        val engineView = SystemEngineView(requireContext().applicationContext)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        binding.webViewContainer.addView(
            engineView,
            params//FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        )

        //get behavior
        (binding.webViewContainer.layoutParams as? CoordinatorLayout.LayoutParams)?.apply {
            (this.behavior as? WebViewToggleBehavior)?.apply {
                this.setSession(session)
                this.setHelper(webViewVisionHelper)
            }
        }
        fullScreenHelper = FullScreenHelper(binding, requireActivity())


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
        miniBottomBarIntegration.set(
            feature = MiniBottomBarIntegration(
                binding = binding,
                session = session,
                sessionUseCases = sessionUseCases,
                webViewVisionHelper = webViewVisionHelper,
                fragment = this
            ), owner = this, view = binding.root
        )
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
        val findInPageIntegrationFeature = FindInPageIntegration(
            sessionManager = sessionManager,
            engineView = engineView, view = findInPage
        )
        findInPageIntegration.set(
            feature = findInPageIntegrationFeature,
            owner = this,
            view = binding.root
        )
        topPanelMenuIntegration.set(
            feature = TopPanelMenuIntegration(
                binding = binding,
                fragment = this,
                savedInstanceState = savedInstanceState,
                topPanelHelper = topPanelHelper,
                findInPageIntegration = findInPageIntegrationFeature
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
            WebViewLifeCycleIntegration(binding = binding, owner = this, engineView = engineView),
            owner = this,
            view = binding.root
        )
        styleIntegration.set(
            feature = StyleIntegration(
                binding = binding,
                fragment = this,
                session = session,
                sessionManager = sessionManager
            ),
            owner = this,
            view = binding.root
        )
        //
        contextMenuIntegration.set(
            feature = ContextMenuIntegration(
                requireContext(), fragmentManager!!,
                sessionManager, tabsUseCases, web_view_container, engineView, sessionId
            ),
            owner = this,
            view = binding.root
        )
        sessionManagerListenerIntegration.set(
            feature = SessionManagerListenerIntegration(
                session = session,
                activity = RouterActivity,
                sessionManager = sessionManager,
                binding = binding,
                fragment = this
            ),
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
        thumbnailFeature.set(
            feature = ThumbnailFeature(requireContext(), engineView, sessionManager),
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
            feature = PictureInPictureIntegration(sessionManager, requireActivity()),
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
            view = binding.root
        )

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
        downloadsFeature.set(
            feature = DownloadsFeature(
                applicationContext = applicationContext,
                sessionManager = sessionManager,
                sessionId = sessionId,
                fragmentManager = childFragmentManager,
                onNeedToRequestPermissions = { permissions ->
                    requestPermissions(permissions, REQUEST_CODE_DOWNLOAD_PERMISSIONS)
                }),
            owner = this,
            view = binding.root
        )
        promptsFeature.set(
            feature = PromptFeature(
                fragment = this,
                sessionManager = sessionManager,
                fragmentManager = requireFragmentManager(),
                onNeedToRequestPermissions = { permissions ->
                    requestPermissions(permissions, REQUEST_CODE_PROMPT_PERMISSIONS)
                }),
            owner = this,
            view = binding.root
        )
        WebViewBlinkFixIntegration(binding=binding,fragment = this,session = session)
        fullScreenProgressIntegration.set(
            feature = FullScreenProgressIntegration(binding=binding,session = session),
            owner = this,
            view = binding.root
        )
        thumbnailIntergration.set(
            feature = ThumbnailIntergration(context=requireContext(),view=binding.webViewContainer,sessionId = sessionId,sessionManager = sessionManager),
            owner = this,
            view = binding.root
        )
    }

    override fun initData(savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun fullScreenChanged(enabled: Boolean) {
        val session = sessionManager.findSessionById(sessionId)
        session?.apply {
            fullScreenHelper.toggleFullScreen(session = this, fullScreen = enabled)
        }
    }

    @Suppress("ReturnCount")
    override fun onBackPressed(): Boolean {
        for (backHandler in backHandlers) {
            if (backHandler.onBackPressed()) {
                return true
            }
        }
        redirect(binding=binding,session = session,runnable = Runnable {
            if (session.parentId!=null){
                sessionManager.remove(session,true)
            }else{
                RouterActivity?.loadHomeFragment(sessionId)
            }
        })

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
            REQUEST_CODE_DOWNLOAD_PERMISSIONS -> downloadsFeature.withFeature {
                it.onPermissionsResult(permissions, grantResults)
            }
            REQUEST_CODE_PROMPT_PERMISSIONS -> promptsFeature.withFeature {
                it.onPermissionsResult(permissions, grantResults)
            }
            REQUEST_CODE_APP_PERMISSIONS -> sitePermissionFeature.withFeature {
                it.onPermissionsResult(grantResults)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        promptsFeature.withFeature { it.onActivityResult(requestCode, resultCode, data) }
    }


    override fun onDestroy() {
        binding.webViewContainer.removeAllViews()
        super.onDestroy()
    }

    override fun onDetach() {
        RouterActivity?.apply {
            //恢复StatusBar的颜色
            StatusBarUtil.setStatusBarBackGroundColorAndIconColor(
                RouterActivity!!,
                activityViewModel.theme.value!!.colorPrimary
            )
        }
        //清除全局视野模式的active标志
        appData.bottomMenuActionItems.find {
            it.id==R.string.ic_normal_screen
        }?.active=false
        super.onDetach()
    }

    companion object {
        const val SESSION_ID = "session_id"
        private const val BROWSER_SCREEN_NUM = Session.HOME_SCREEN + 1
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


