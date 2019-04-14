package com.dev.orangebrowser.bloc.browser.integration.helper

import android.animation.ValueAnimator
import android.view.animation.AccelerateInterpolator
import androidx.core.view.ViewCompat
import com.dev.base.extension.FAST_ANIMATION
import com.dev.browser.session.Session
import com.dev.orangebrowser.databinding.FragmentBrowserBinding

class WebViewVisionHelper(var binding:FragmentBrowserBinding){
    fun showBottomAndTopBarAnimate(){
        animateShowTopBar()
        animateShowBottomBar()
    }
    fun hideBottomAndTopBarAnimate(){
        animateHideTopBar()
        animateHideBottomBar()
    }
    fun showMiniBottomBarAnimate(){
        binding.miniBottomBar.animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(
            AccelerateInterpolator()
        ).start()
    }
    fun hideMiniBottomBarAnimate(){
        binding.miniBottomBar.animate().translationY(binding.miniBottomBar.height.toFloat())
            .setDuration(FAST_ANIMATION)
            .setInterpolator(
                AccelerateInterpolator()
            ).start()
    }
    fun animateHideTopBar(){
        if (binding.topBar.top >= 0) {
            ValueAnimator.ofInt(0, -binding.topBar.height).apply {
                duration = FAST_ANIMATION
                addUpdateListener {
                    ViewCompat.offsetTopAndBottom(
                        binding.topBar,
                        (it.animatedValue as Int) - binding.topBar.top
                    )
                }
            }.start()
        }
    }
    fun animateShowTopBar(){
        if (binding.topBar.top < 0) {
            ValueAnimator.ofInt(-binding.topBar.height, 0).apply {
                duration = FAST_ANIMATION
                addUpdateListener {
                    ViewCompat.offsetTopAndBottom(
                        binding.topBar,
                        (it.animatedValue as Int) - binding.topBar.top
                    )
                }
            }.start()
        }
    }
    fun animateHideBottomBar(){
        if(binding.bottomBar.translationY<=0){
            binding.bottomBar.animate().translationY(binding.bottomBar.height.toFloat())
                .setDuration(FAST_ANIMATION)
                .setInterpolator(
                    AccelerateInterpolator()
                ).start()
        }
    }
    fun animateShowBottomBar(){
        binding.bottomBar.animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(
            AccelerateInterpolator()
        ).start()
    }
    fun restoreHideTopBar(){
        if (binding.topBar.top >= 0) {
            val offset=binding.topBar.bottom
            ViewCompat.offsetTopAndBottom(binding.topBar,-offset)
        }
    }

}