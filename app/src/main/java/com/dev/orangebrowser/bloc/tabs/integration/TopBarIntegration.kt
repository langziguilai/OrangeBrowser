package com.dev.orangebrowser.bloc.tabs.integration

import android.os.Bundle
import androidx.annotation.ColorInt
import com.dev.base.extension.*
import com.dev.browser.session.Session
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.tabs.TabFragment
import com.dev.orangebrowser.databinding.FragmentTabBinding
import com.dev.util.ColorKitUtil
import com.dev.view.NavigationBarUtil
import com.dev.view.StatusBarUtil


class TopBarIntegration(
    var binding: FragmentTabBinding,
    var fragment: TabFragment,
    var savedInstanceState: Bundle?,
    var session: Session
){

    init {
        initTopBar(savedInstanceState)
    }

    private fun initTopBar(savedInstanceState: Bundle?) {
        setTopBarInitialState(savedInstanceState)
    }
    fun setTopbarBySession(session:Session){
        //优先设置为title，其次为url
        if(session.screenNumber!=Session.HOME_SCREEN){
            if (session.title.isNotBlank()){
                if (session.title==Session.HOME_TITLE){
                    binding.searchText.text=""
                }else{
                    binding.searchText.text=session.title
                }

            }else if (session.url.isNotBlank()){
                binding.searchText.text=session.url
            }
        }else{
            binding.searchText.text=""
        }

        if (session.securityInfo.secure){
            binding.securityIcon.show()
        }else{
            binding.securityIcon.hide()
        }
        if(session.loading){
            binding.reloadIcon.hide()
            binding.stopIcon.show()
            binding.progress.show()
        } else {
            binding.reloadIcon.show()
            binding.stopIcon.hide()
            binding.progress.hide()
        }
    }
    private fun setTopBarInitialState(savedInstanceState:Bundle?){
        //优先设置为title，其次为url
        if(session.screenNumber!=Session.HOME_SCREEN){
            if (session.title.isNotEmpty()) {
                binding.searchText.text = session.title
            } else if (session.url.isNotEmpty()) {
                binding.searchText.text = session.url
            }
        }

        if (session.securityInfo.secure){
            binding.securityIcon.show()
        }else{
            binding.securityIcon.hide()
        }
        if(session.loading){
            binding.reloadIcon.hide()
            binding.stopIcon.show()
            binding.progress.show()
        } else {
            binding.reloadIcon.show()
            binding.stopIcon.hide()
            binding.progress.hide()
        }
    }
    private fun updateTextColor(color:Int){
       var textColor=fragment.requireContext().resources.getColor(R.color.colorWhite)
       if(ColorKitUtil.isBackGroundLightMode(color)){
            textColor=fragment.requireContext().resources.getColor(R.color.colorBlack)
       }
        binding.searchText.setTextColor(textColor)
        binding.topMenu.setTextColor(textColor)
    }
    private fun updateTopBarBackGround(color:Int){
        StatusBarUtil.setStatusBarBackGroundColorAndIconColor(fragment.requireActivity(),color)
        NavigationBarUtil.setNavigationBarColor(fragment.requireActivity(),color)
        //binding.topBar.setBackgroundColor(color)
        binding.topbarBackground.setBackgroundColor(color)
    }
    //隐藏
    fun hide(){
        val color=fragment.activityViewModel.theme.value!!.colorPrimary
        updateTextColor(color)
        updateTopBarBackGround(color)
        binding.topBar.onGlobalLayoutComplete {
            it.animate().setDuration(NORMAL_ANIMATION).setInterpolator(DEFAULT_INTERPOLATOR).alpha(0f).withEndAction {
                it.alpha=0f
            }.start()
        }
    }
    //显示
    fun show(session:Session){
        setTopbarBySession(session)
        val color=session.themeColorMap[session.url] ?: fragment.activityViewModel.theme.value!!.colorPrimary
        updateTextColor(color)
        updateTopBarBackGround(color)
        binding.topBar.animate().setDuration(NORMAL_ANIMATION).setInterpolator(DEFAULT_INTERPOLATOR).alpha(1f).withEndAction{
            binding.topBar.alpha=1f
        }.start()

    }
}
