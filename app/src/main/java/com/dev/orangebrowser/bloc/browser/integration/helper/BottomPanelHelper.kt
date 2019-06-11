package com.dev.orangebrowser.bloc.browser.integration.helper

import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import com.dev.base.extension.FAST_ANIMATION
import com.dev.base.extension.hide
import com.dev.base.extension.isHidden
import com.dev.base.extension.show
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.util.DensityUtil

class BottomPanelHelper(private var binding: FragmentBrowserBinding, var fragment: BrowserFragment){
    private var bottomPanelBackHandler: BackHandler
    init {
        bottomPanelBackHandler=object: BackHandler {
            override fun onBackPressed(): Boolean {
                toggleBottomPanel()
                return true
            }
        }
    }
    //动态添加backHandler
    fun toggleBottomPanel(runnable: Runnable= Runnable { }){
        //如果是隐藏的，那么显示
        if (binding.overLayerBottomPanel.isHidden()){
            showBottomPanel(runnable)
            fragment.backHandlers.add(0,bottomPanelBackHandler!!)
        }else{
            hideBottomPanel(runnable)
            fragment.backHandlers.remove(bottomPanelBackHandler)
        }
    }
    private fun showBottomPanel(runnable: Runnable= Runnable {  }) {
        binding.overLayerBottomPanel.show()
        binding.bottomMenuPanel.apply {
            animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).withEndAction(runnable).start()
        }
    }
    private fun hideBottomPanel(runnable: Runnable= Runnable { }) {
        binding.overLayerBottomPanel.hide()
        binding.bottomMenuPanel.apply {
            animate().translationY(this.height.toFloat()+ DensityUtil.dip2px(fragment.requireContext(),16f)).setDuration(
                FAST_ANIMATION
            ).withEndAction(runnable).start()
        }
    }
}