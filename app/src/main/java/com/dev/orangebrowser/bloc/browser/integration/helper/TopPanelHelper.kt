package com.dev.orangebrowser.bloc.browser.integration.helper

import android.view.animation.AccelerateInterpolator
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding

class TopPanelHelper(var binding: FragmentBrowserBinding, var fragment: BrowserFragment,var bottomPanelHelper: BottomPanelHelper){
    private var topPanelBackHandler: BackHandler
    init {
        topPanelBackHandler=object: BackHandler {
            override fun onBackPressed(): Boolean {
                toggleTopPanel()
                return true
            }

        }
    }
    //动态添加backHandler
    fun toggleTopPanel(runnable: Runnable= Runnable { }){
        //如果是隐藏的，那么显示
        if (binding.overLayerTopPanel.isHidden()){
            showTopPanel(runnable)
            fragment.backHandlers.add(0,topPanelBackHandler!!)
        }else{
            hideTopPanel(runnable)
            fragment.backHandlers.remove(topPanelBackHandler)
        }
    }
    private fun hideTopPanel(runnable: Runnable= Runnable { }) {
        binding.overLayerTopPanel.hide()
        binding.topMenuPanel.apply {
            animate().translationY(-this.height.toFloat()).setDuration(FAST_ANIMATION).setInterpolator(
                AccelerateInterpolator()
            ).withEndAction {
                binding.topMenuPanel.hide()
                binding.topMenuPanelContainer.hide()
                runnable.run()
            }.start()
        }
    }
    private fun showTopPanel(runnable: Runnable= Runnable { }){
        binding.overLayerTopPanel.show()
        binding.topMenuPanelContainer.show()
        binding.topMenuPanel.show()
        binding.topMenuPanel.apply {
            animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).withEndAction(runnable).start()
        }
        //如果bottomPanelBar是显示的，那么隐藏bottomPanel
        if (binding.overLayerBottomPanel.isShowing()){
            bottomPanelHelper.toggleBottomPanel()
        }
    }
}