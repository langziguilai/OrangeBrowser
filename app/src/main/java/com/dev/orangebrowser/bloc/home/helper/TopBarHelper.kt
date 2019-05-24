package com.dev.orangebrowser.bloc.home.helper

import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.widget.Toast
import com.dev.base.extension.*
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.data.model.ActionItem
import com.dev.orangebrowser.databinding.FragmentHomeBinding
import com.dev.orangebrowser.extension.RouterActivity
import com.dev.orangebrowser.extension.appData
import com.dev.view.GridView
import com.dev.view.NavigationBarUtil
import com.dev.view.StatusBarUtil
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import es.dmoral.toasty.Toasty


class TopBarHelper(var binding: FragmentHomeBinding, var fragment: HomeFragment, var savedInstanceState: Bundle?, var bottomBarHelper: BottomBarHelper){
    private lateinit var topPanelBackHandler: BackHandler
    init{
        initTopBar(savedInstanceState)
    }
    private fun initTopBar(savedInstanceState: Bundle?) {
        binding.searchText.setOnClickListener {
            if (!binding.overLayerTopPanel.isHidden()){
                toggleTopPanel(Runnable{
                    fragment.RouterActivity?.loadSearchFragment(fragment.sessionId)
                })
            }else{
                fragment.RouterActivity?.loadSearchFragment(fragment.sessionId)
            }
        }
        binding.topMenu.setOnClickListener {
            toggleTopPanel()
        }
        initTopMenuGridView(binding.topMenuPanel)
        binding.overLayerTopPanel.setOnClickListener {
            toggleTopPanel()
        }
        //initial hide
        binding.topMenuPanel.apply {
            onGlobalLayoutComplete{
                if (fragment.context!=null){
                    it.translationY=-this.height.toFloat()
                }
            }
        }
        topPanelBackHandler=object: BackHandler {
            override fun onBackPressed(): Boolean {
                toggleTopPanel()
                return true
            }
        }
        StatusBarUtil.setStatusBarBackGroundColorAndIconColor(fragment.requireActivity(),fragment.activityViewModel.theme.value!!.colorPrimary)
        NavigationBarUtil.setNavigationBarColor(fragment.requireActivity(),fragment.activityViewModel.theme.value!!.colorPrimary)
    }

    private fun initTopMenuGridView(topMenuPanel: GridView) {
        val adapter=object: BaseQuickAdapter<ActionItem, CustomBaseViewHolder>(R.layout.item_top_action_item,fragment.appData.topMenuActionItems){
            override fun convert(helper: CustomBaseViewHolder, item: ActionItem) {
                helper.setText(R.id.icon,item.iconRes)
                helper.setText(R.id.name,item.nameRes)
            }
        }
        adapter.setOnItemClickListener { _, _, position ->
            onTopMenuActionItemClick(fragment.appData.topMenuActionItems[position])
        }
        topMenuPanel.adapter=adapter
    }

    //处理Top Menu Item的点击
    private fun onTopMenuActionItemClick(actionItem: ActionItem) {
        when(actionItem.iconRes){
            //扫码
            R.string.ic_scan->{
                toggleTopPanel(Runnable {
                    fragment.RouterActivity?.loadScanActivity()
                })
            }
            R.string.ic_share->{
                fragment.requireContext().showToast(fragment.getString(R.string.webview_not_available_hint))
            }
            //阅读模式
            R.string.ic_read->{
                fragment.requireContext().showToast(fragment.getString(R.string.webview_not_available_hint))
            }
            //看图模式
            R.string.ic_image->{
                fragment.requireContext().showToast(fragment.getString(R.string.webview_not_available_hint))
            }
            //页内查找
            R.string.ic_search->{
                fragment.requireContext().showToast(fragment.getString(R.string.webview_not_available_hint))
            }
            //离线保存
            R.string.ic_save->{
                fragment.requireContext().showToast(fragment.getString(R.string.webview_not_available_hint))
            }
            //翻译
            R.string.ic_translate->{
                fragment.requireContext().showToast(fragment.getString(R.string.webview_not_available_hint))
            }
            //资源嗅探
            R.string.ic_resources_fang->{
                fragment.requireContext().showToast(fragment.getString(R.string.webview_not_available_hint))
            }
            //添加到主页
            R.string.ic_store->{
                fragment.requireContext().showToast(fragment.getString(R.string.webview_not_available_hint))
            }
        }
    }

    private fun toggleTopPanel(runnable: Runnable= Runnable { }){
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
            ).withEndAction(runnable).start()
        }
    }
    private fun showTopPanel(runnable: Runnable= Runnable { }){
        binding.overLayerTopPanel.show()
        binding.topMenuPanel.apply {
            animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).withEndAction(runnable).start()
        }
        //如果bottomPanelBar是显示的，那么隐藏bottomPanel
        if (binding.overLayerBottomPanel.isShowing()){
            bottomBarHelper.toggleBottomPanel()
        }
    }
}