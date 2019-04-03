package com.dev.orangebrowser.bloc.browser


import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.dev.base.support.BackHandler
import com.dev.base.BaseFragment
import com.dev.base.support.UserInteractionHandler
import com.dev.base.support.ViewBoundFeatureWrapper
import com.dev.orangebrowser.R
import com.dev.orangebrowser.extension.appComponent
import kotlinx.android.synthetic.main.browser_fragment.*
import javax.inject.Inject


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
    private val sessionId: String?
        get() = arguments?.getString(SESSION_ID)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //TODO:注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(BrowserViewModel::class.java)
    }

    override fun getLayoutResId(): Int {
        return R.layout.browser_fragment
    }

    override fun initView(view: View, savedInstanceState: Bundle?) {

    }

    override fun initData(savedInstanceState: Bundle?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun showTabs() {
        // For now we are performing manual fragment transactions here. Once we can use the new
        // navigation support library we may want to pass navigation graphs around.
//        activity?.supportFragmentManager?.beginTransaction()?.apply {
//            replace(R.id.container, TabsTrayFragment())
//            commit()
//        }
    }

    private fun fullScreenChanged(enabled: Boolean) {
        if (enabled) {
            //activity?.enterToImmersiveMode()
            //toolbar.visibility = View.GONE
        } else {
            //activity?.exitImmersiveModeIfNeeded()
            //toolbar.visibility = View.VISIBLE
        }
    }

    @Suppress("ReturnCount")
    override fun onBackPressed(): Boolean {
        return backButtonHandler.firstOrNull { it.onBackPressed() } != null
    }

    override fun onHomePressed(): Boolean {
        var handled = false

//        pictureInPictureIntegration.withFeature {
//            handled = it.onHomePressed()
//        }

        return handled
    }

    override fun onPictureInPictureModeChanged(enabled: Boolean) {
        //val fullScreenMode = sessionManager.selectedSession?.fullScreenMode ?: false
        // If we're exiting PIP mode and we're in fullscreen mode, then we should exit fullscreen mode as well.
//        if (!enabled && fullScreenMode) {
//            onBackPressed()
//            fullScreenChanged(false)
//        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
//            REQUEST_CODE_DOWNLOAD_PERMISSIONS -> downloadsFeature.withFeature {
//                it.onPermissionsResult(permissions, grantResults)
//            }
//            REQUEST_CODE_PROMPT_PERMISSIONS -> promptsFeature.withFeature {
//                it.onPermissionsResult(permissions, grantResults)
//            }
//            REQUEST_CODE_APP_PERMISSIONS -> sitePermissionFeature.withFeature {
//                it.onPermissionsResult(grantResults)
//            }
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