package com.dev.orangebrowser.bloc.tabs.integration

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import com.dev.base.extension.*
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.databinding.FragmentTabBinding

class BottomBarAnimateIntegration(
    private var binding: FragmentTabBinding,
    var fragment: TabFragment,
    var savedInstanceState: Bundle?,
    var sessionManager: SessionManager,
    var session: Session
) {
    init {
        initBottomBar(savedInstanceState = savedInstanceState)
    }

    private fun initBottomBar(savedInstanceState: Bundle?) {
        setBottomBarInitialState(savedInstanceState)
    }

    //设置初始化状态
    private fun setBottomBarInitialState(savedInstanceState: Bundle?) {
        //后退
        if (session.screenNumber == Session.HOME_SCREEN) {
            binding.back.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
            //当url存在的时候
            if (session.url.isNotBlank() && session.url != Session.NO_EXIST_URL) {
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
            } else {
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
            }
            binding.search.visibility= View.VISIBLE
            binding.home.visibility=View.GONE
        } else {
            binding.back.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
            if (session.canGoForward) {
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
            } else {
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
            }
            binding.search.visibility= View.GONE
            binding.home.visibility=View.VISIBLE
        }
        binding.counterNumber.text = sessionManager.size.toString()
    }
    private fun setTopBarBySession(session:Session){
        //后退
        if (session.screenNumber == Session.HOME_SCREEN) {
            binding.back.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
            //当url存在的时候
            if (session.url.isNotBlank() && session.url != Session.NO_EXIST_URL) {
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
            } else {
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
            }
            binding.search.visibility= View.VISIBLE
            binding.home.visibility=View.GONE
        } else {
            binding.back.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
            if (session.canGoForward) {
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimary)
            } else {
                binding.forward.setTextColor(fragment.activityViewModel.theme.value!!.colorPrimaryDisable)
            }
            binding.search.visibility= View.GONE
            binding.home.visibility=View.VISIBLE
        }
        binding.counterNumber.text = sessionManager.size.toString()
    }
    //隐藏
    fun hide() {
        binding.bottomBarAnimate.onGlobalLayoutComplete {
            it.animate().alpha(0f).translationY(it.height.toFloat()).setInterpolator(DEFAULT_INTERPOLATOR).setDuration(NORMAL_ANIMATION).start()
        }
    }

    fun show(session:Session) {
        setTopBarBySession(session)
        binding.bottomBarAnimate.animate().alpha(1f).translationY(0f).setInterpolator(DEFAULT_INTERPOLATOR).setDuration(NORMAL_ANIMATION).start()
    }
}
