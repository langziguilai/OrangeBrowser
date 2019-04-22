package com.dev.orangebrowser.bloc.tabs.integration

import com.dev.base.extension.DEFAULT_INTERPOLATOR
import com.dev.base.extension.NORMAL_ANIMATION
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.databinding.FragmentTabBinding
import com.dev.orangebrowser.extension.RouterActivity

class BottomBarIntegration(var binding: FragmentTabBinding,var sessionManager: SessionManager,var fragment:TabFragment,var sessionId:String){
    init {

        //清空
        binding.clear.setOnClickListener {
            sessionManager.removeSessions()
            fragment.exitAnimate(runnable = Runnable {
                fragment.RouterActivity?.loadHomeOrBrowserFragment(HomeFragment.NO_SESSION_ID)
            })
        }
        //新增
        binding.add.setOnClickListener {
            fragment.exitAnimate(runnable = Runnable {
                fragment.RouterActivity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
            })
        }
        //返回
        binding.goBack.setOnClickListener {
            fragment.exitAnimate(runnable = Runnable {
                fragment.RouterActivity?.loadHomeOrBrowserFragment(sessionId)
            })
        }
    }
    fun hide(){
        binding.bottomBar.animate()
            .translationY(binding.bottomBar.height.toFloat())
            .setInterpolator(DEFAULT_INTERPOLATOR)
            .setDuration(NORMAL_ANIMATION).alpha(0f).start()
    }
    fun show(){
        binding.bottomBar.onGlobalLayoutComplete {
            it.translationY=it.height.toFloat()
            it.animate()
                .setInterpolator(DEFAULT_INTERPOLATOR)
                .translationY(0f)
                .alpha(1f)
                .setDuration(NORMAL_ANIMATION).start()
        }
    }
}