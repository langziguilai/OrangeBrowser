package com.dev.orangebrowser.bloc.browser.integration


import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.feature.session.SessionUseCases
import com.dev.browser.session.Session
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.browser.integration.helper.WebViewVisionHelper
import com.dev.orangebrowser.databinding.FragmentBrowserBinding

//Mini底部导航栏
class MiniBottomBarIntegration(var binding: FragmentBrowserBinding, var fragment: BrowserFragment, var sessionUseCases: SessionUseCases, var session: Session, webViewVisionHelper: WebViewVisionHelper):
    LifecycleAwareFeature {
    private var sessionObserver: Session.Observer
    init{
        if (session.canGoForward){
            binding.miniForward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
        }else{
            binding.miniForward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
        }
        binding.miniBack.setOnClickListener {
            fragment.onBackPressed()
        }
        binding.miniForward.setOnClickListener {
            if (session.canGoForward){
                sessionUseCases.goForward.invoke(session)
            }
        }
        binding.miniShowBottomBar.setOnClickListener {
            session.enterFullScreenMode=false
            webViewVisionHelper.showBottomAndTopBarAnimate()
            webViewVisionHelper.hideMiniBottomBarAnimate()
        }
        //开始的时候隐藏
        binding.miniBottomBar.apply {
            onGlobalLayoutComplete {
                it.animate().translationY(this.height.toFloat())
                    .setDuration(0).start()
            }
        }
        sessionObserver=object:Session.Observer{
            override fun onProgress(session: Session, progress: Int) {
                   if (progress==100){
                       binding.miniProgress.progress=0
                   }else{
                       binding.miniProgress.progress=progress
                   }
            }
            override fun onNavigationStateChanged(session: Session, canGoBack: Boolean, canGoForward: Boolean) {
                if (canGoForward){
                    binding.miniForward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
                }else{
                    binding.miniForward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
                }
            }
        }
    }
    override fun start() {
        session.register(sessionObserver)
    }

    override fun stop() {
        session.unregister(sessionObserver)
    }
}
